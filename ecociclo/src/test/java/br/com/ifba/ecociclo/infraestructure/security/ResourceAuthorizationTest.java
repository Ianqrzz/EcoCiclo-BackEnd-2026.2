package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.agendamento.dto.request.AgendamentoRequestDTO;
import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import br.com.ifba.ecociclo.modulos.agendamento.repository.AgendamentoRepository;
import br.com.ifba.ecociclo.modulos.agendamento.service.AgendamentoService;
import br.com.ifba.ecociclo.modulos.coletor.repository.ColetorRepository;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.doador.repository.DoadorRepository;
import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import br.com.ifba.ecociclo.modulos.doacao.repository.DoacaoRepository;
import br.com.ifba.ecociclo.modulos.doacao.service.DoacaoService;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.modulos.endereco.repository.EnderecoRepository;
import br.com.ifba.ecociclo.modulos.endereco.service.EnderecoService;
import br.com.ifba.ecociclo.modulos.recompensa.dto.request.RecompensaRequestDTO;
import br.com.ifba.ecociclo.modulos.recompensa.model.Recompensa;
import br.com.ifba.ecociclo.modulos.recompensa.repository.RecompensaRepository;
import br.com.ifba.ecociclo.modulos.recompensa.service.RecompensaService;
import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import br.com.ifba.ecociclo.modulos.resgate.model.Resgate;
import br.com.ifba.ecociclo.modulos.resgate.repository.ResgateRepository;
import br.com.ifba.ecociclo.modulos.resgate.service.ResgateService;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.CadastroRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.UsuarioUpdateRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.repository.UsuarioRepository;
import br.com.ifba.ecociclo.modulos.usuario.service.UsuarioService;
import br.com.ifba.ecociclo.modulos.administrador.repository.AdministradorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

class ResourceAuthorizationTest {

    private final UUID ownerId = UUID.randomUUID();
    private final UUID otherId = UUID.randomUUID();
    private Doador actor;

    @BeforeEach
    void authenticateAsDoador() {
        actor = mock(Doador.class);
        when(actor.getId()).thenReturn(ownerId);
        when(actor.getPerfil()).thenReturn(TipoPerfil.DOADOR);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(actor, "token"));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void securityActorRecusaIdsDeOutraConta() {
        assertThrows(AccessDeniedException.class, () -> SecurityActor.requireSelfOrAdmin(otherId));
        assertThrows(AccessDeniedException.class, SecurityActor::requireAdmin);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("visitante", "token"));
        assertThrows(AccessDeniedException.class, SecurityActor::current);
    }

    @Test
    void agendamentosRecusamOperacoesDeOutraConta() {
        AgendamentoRepository agendamentos = mock(AgendamentoRepository.class);
        AgendamentoService service = new AgendamentoService(
                agendamentos, mock(DoadorRepository.class), mock(EnderecoRepository.class),
                mock(DoacaoRepository.class), mock(ColetorRepository.class));

        assertThrows(AccessDeniedException.class, () -> service.criarAgendamento(
                new AgendamentoRequestDTO(otherId, otherId, null, null, new DoacaoRequestDTO("Papel", 1, "", 1))));
        assertThrows(AccessDeniedException.class, () -> service.aceitarColeta(otherId, otherId));
        assertThrows(AccessDeniedException.class, () -> service.listarPorColetor(otherId));
        assertThrows(AccessDeniedException.class, () -> service.listarAgendamentos(null, otherId));
        assertThrows(AccessDeniedException.class, service::listarPendentes);

        Agendamento assignedToOther = mock(Agendamento.class);
        var collector = mock(br.com.ifba.ecociclo.modulos.coletor.model.Coletor.class);
        when(collector.getId()).thenReturn(otherId);
        when(assignedToOther.getColetor()).thenReturn(collector);
        when(agendamentos.findById(otherId)).thenReturn(Optional.of(assignedToOther));
        assertThrows(AccessDeniedException.class, () -> service.concluirColeta(otherId));

        var donor = mock(Doador.class);
        when(donor.getId()).thenReturn(otherId);
        when(assignedToOther.getDoador()).thenReturn(donor);
        assertThrows(AccessDeniedException.class, () -> service.cancelarAgendamento(otherId));
    }

    @Test
    void doacoesRecusamOperacoesDeAdministradorEDeOutroDoador() {
        DoacaoRepository doacoes = mock(DoacaoRepository.class);
        AgendamentoRepository agendamentos = mock(AgendamentoRepository.class);
        DoacaoService service = new DoacaoService(doacoes, agendamentos);
        assertThrows(AccessDeniedException.class,
                () -> service.salvar(new DoacaoRequestDTO("Papel", 1, "", 1)));
        assertThrows(AccessDeniedException.class, service::listarTodas);

        when(doacoes.findById(otherId)).thenReturn(Optional.of(mock(Doacao.class)));
        assertThrows(AccessDeniedException.class, () -> service.buscarPorId(otherId));
    }

    @Test
    void enderecosRecusamOperacoesDeAdministradorEDeOutroUsuario() {
        EnderecoRepository enderecos = mock(EnderecoRepository.class);
        EnderecoService service = new EnderecoService(enderecos, mock(UsuarioRepository.class));
        Endereco address = mock(Endereco.class);
        Usuario otherOwner = mock(Usuario.class);
        when(otherOwner.getId()).thenReturn(otherId);
        when(address.getUsuario()).thenReturn(otherOwner);
        when(enderecos.findById(otherId)).thenReturn(Optional.of(address));

        assertThrows(AccessDeniedException.class,
                () -> service.salvar(new EnderecoRequestDTO("Rua", "Centro", "Irecê", "BA", "44900000", otherId)));
        assertThrows(AccessDeniedException.class, service::listarTodos);
        assertThrows(AccessDeniedException.class, () -> service.buscarPorId(otherId));
        assertThrows(AccessDeniedException.class, () -> service.atualizar(otherId,
                new EnderecoRequestDTO("Rua nova", null, null, null, null, ownerId)));
        assertThrows(AccessDeniedException.class, () -> service.deletar(otherId));
    }

    @Test
    void recompensasEResgatesRecusamOperacoesRestritas() {
        RecompensaService recompensaService = new RecompensaService(
                mock(RecompensaRepository.class), mock(DoadorRepository.class), mock(ResgateRepository.class));
        RecompensaRequestDTO reward = new RecompensaRequestDTO("Caneca", 1, "", "Caneca", 10.0);
        assertThrows(AccessDeniedException.class, () -> recompensaService.salvar(reward));
        assertThrows(AccessDeniedException.class, () -> recompensaService.atualizar(otherId, reward));
        assertThrows(AccessDeniedException.class, () -> recompensaService.deletar(otherId));
        assertThrows(AccessDeniedException.class, () -> recompensaService.resgatar(otherId, otherId));

        ResgateRepository resgates = mock(ResgateRepository.class);
        Resgate resgate = mock(Resgate.class);
        Doador otherDonor = mock(Doador.class);
        when(otherDonor.getId()).thenReturn(otherId);
        when(resgate.getDoador()).thenReturn(otherDonor);
        when(resgates.findById(otherId)).thenReturn(Optional.of(resgate));
        ResgateService resgateService = new ResgateService(resgates);
        assertThrows(AccessDeniedException.class, resgateService::listarTodos);
        assertThrows(AccessDeniedException.class, () -> resgateService.buscarPorId(otherId));
        assertThrows(AccessDeniedException.class, () -> resgateService.listarPorDoador(otherId));
        assertThrows(AccessDeniedException.class, () -> resgateService.listarPorStatus(null));
        assertThrows(AccessDeniedException.class, () -> resgateService.listarPorDoadorEStatus(otherId, null));
    }

    @Test
    void usuariosRecusamOperacoesDeOutraContaECadastroDeAdmin() {
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        UsuarioService service = new UsuarioService(
                mock(DoadorRepository.class), mock(ColetorRepository.class), mock(AdministradorRepository.class),
                usuarios, mock(BCryptPasswordEncoder.class));

        assertThrows(AccessDeniedException.class, () -> service.salvar(new CadastroRequestDTO(
                "Admin", "123", "123", "admin@example.com", "senha1234", TipoPerfil.ADMINISTRADOR, null)));
        assertThrows(AccessDeniedException.class, () -> service.adicionarEndereco(otherId,
                new EnderecoRequestDTO("Rua", "Centro", "Irecê", "BA", "44900000", otherId)));
        assertThrows(AccessDeniedException.class, service::listarTodos);
        assertThrows(AccessDeniedException.class, () -> service.buscarPorId(otherId));
        assertThrows(AccessDeniedException.class, () -> service.atualizar(otherId, new UsuarioUpdateRequestDTO("Outro", "123")));
        assertThrows(AccessDeniedException.class, () -> service.deletar(otherId));
    }

    @Test
    void administradoresConseguemUsarOperacoesProtegidas() {
        when(actor.getPerfil()).thenReturn(TipoPerfil.ADMINISTRADOR);

        AgendamentoRepository agendamentos = mock(AgendamentoRepository.class);
        when(agendamentos.findAll()).thenReturn(java.util.List.of());
        when(agendamentos.findByStatusAndColetor_Id(org.mockito.ArgumentMatchers.any(), any())).thenReturn(java.util.List.of());
        AgendamentoService agendamentoService = new AgendamentoService(
                agendamentos, mock(DoadorRepository.class), mock(EnderecoRepository.class),
                mock(DoacaoRepository.class), mock(ColetorRepository.class));
        agendamentoService.listarAgendamentos(null, null);

        DoacaoRepository doacoes = mock(DoacaoRepository.class);
        when(doacoes.save(any(Doacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(doacoes.findAll()).thenReturn(java.util.List.of());
        when(doacoes.findById(ownerId)).thenReturn(Optional.of(mock(Doacao.class)));
        DoacaoService doacaoService = new DoacaoService(doacoes, mock(AgendamentoRepository.class));
        doacaoService.salvar(new DoacaoRequestDTO("Papel", 1, "", 1));
        doacaoService.listarTodas();
        doacaoService.buscarPorId(ownerId);

        EnderecoRepository enderecos = mock(EnderecoRepository.class);
        Endereco ownedAddress = mock(Endereco.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        when(ownedAddress.getUsuario().getId()).thenReturn(ownerId);
        when(enderecos.findAll()).thenReturn(java.util.List.of());
        when(enderecos.findById(ownerId)).thenReturn(Optional.of(ownedAddress));
        when(enderecos.save(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));
        EnderecoService enderecoService = new EnderecoService(enderecos, mock(UsuarioRepository.class));
        enderecoService.listarTodos();
        enderecoService.buscarPorId(ownerId);
        enderecoService.atualizar(ownerId, new EnderecoRequestDTO("Rua", "Centro", "Irecê", "BA", "44900000", ownerId));
        enderecoService.deletar(ownerId);

        RecompensaRepository recompensas = mock(RecompensaRepository.class);
        ResgateRepository resgates = mock(ResgateRepository.class);
        Recompensa reward = mock(Recompensa.class);
        when(reward.getId()).thenReturn(ownerId);
        when(reward.getNome()).thenReturn("Caneca");
        when(reward.getQuantidade()).thenReturn(1);
        when(reward.getBloqueado()).thenReturn(0);
        when(reward.getCustoPontos()).thenReturn(10.0);
        when(reward.isDisponivel()).thenReturn(true);
        when(recompensas.save(any(br.com.ifba.ecociclo.modulos.recompensa.model.Recompensa.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(recompensas.findById(ownerId)).thenReturn(Optional.of(reward));
        when(resgates.existsByRecompensaId(ownerId)).thenReturn(false);
        RecompensaService recompensaService = new RecompensaService(recompensas, mock(DoadorRepository.class), resgates);
        recompensaService.salvar(new RecompensaRequestDTO("Caneca", 1, "", "Caneca", 10.0));
        recompensaService.atualizar(ownerId, new RecompensaRequestDTO(null, null, null, null, null));
        recompensaService.deletar(ownerId);

        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        when(usuarios.findAll()).thenReturn(java.util.List.of());
        when(usuarios.findById(ownerId)).thenReturn(Optional.of(actor));
        when(usuarios.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarios.existsById(ownerId)).thenReturn(true);
        UsuarioService usuarioService = new UsuarioService(
                mock(DoadorRepository.class), mock(ColetorRepository.class), mock(AdministradorRepository.class),
                usuarios, mock(BCryptPasswordEncoder.class));
        usuarioService.listarTodos();
        usuarioService.buscarPorId(ownerId);
        usuarioService.atualizar(ownerId, new UsuarioUpdateRequestDTO("Doador", "123"));
        usuarioService.deletar(ownerId);

        when(resgates.findAll()).thenReturn(java.util.List.of());
        when(resgates.findByStatus(any())).thenReturn(java.util.List.of());
        when(resgates.findByDoador_Id(ownerId)).thenReturn(java.util.List.of());
        when(resgates.findByDoador_IdAndStatus(eq(ownerId), any())).thenReturn(java.util.List.of());
        ResgateService resgateService = new ResgateService(resgates);
        resgateService.listarTodos();
        resgateService.listarPorStatus(null);
        resgateService.listarPorDoador(ownerId);
        resgateService.listarPorDoadorEStatus(ownerId, null);
    }

    @Test
    void doadoresPodemUsarSeusAgendamentosEnderecosResgatesERecompensas() {
        AgendamentoRepository agendamentos = mock(AgendamentoRepository.class);
        DoadorRepository doadores = mock(DoadorRepository.class);
        EnderecoRepository enderecos = mock(EnderecoRepository.class);
        DoacaoRepository doacoes = mock(DoacaoRepository.class);
        ColetorRepository coletores = mock(ColetorRepository.class);
        Endereco ownedAddress = mock(Endereco.class);
        when(ownedAddress.getUsuario()).thenReturn(actor);
        when(enderecos.findById(otherId)).thenReturn(Optional.of(ownedAddress));
        when(doadores.findById(ownerId)).thenReturn(Optional.of(actor));
        when(doacoes.save(any(Doacao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(agendamentos.save(any(Agendamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        AgendamentoService agendamentoService = new AgendamentoService(
                agendamentos, doadores, enderecos, doacoes, coletores);
        agendamentoService.criarAgendamento(new AgendamentoRequestDTO(
                ownerId, otherId, null, null, new DoacaoRequestDTO("Papel", 1, "", 1)));

        Endereco otherAddress = mock(Endereco.class);
        Usuario otherUser = mock(Usuario.class);
        when(otherUser.getId()).thenReturn(otherId);
        when(otherAddress.getUsuario()).thenReturn(otherUser);
        when(enderecos.findById(ownerId)).thenReturn(Optional.of(otherAddress));
        assertThrows(AccessDeniedException.class, () -> agendamentoService.criarAgendamento(
                new AgendamentoRequestDTO(ownerId, ownerId, null, null, new DoacaoRequestDTO("Papel", 1, "", 1))));

        when(agendamentos.findByColetor_Id(ownerId)).thenReturn(java.util.List.of());
        agendamentoService.listarPorColetor(ownerId);
        agendamentoService.listarAgendamentos(br.com.ifba.ecociclo.modulos.agendamento.enums.StatusAgendamento.PENDENTE, ownerId);

        when(enderecos.findById(ownerId)).thenReturn(Optional.of(ownedAddress));
        when(enderecos.findAll()).thenReturn(java.util.List.of());
        when(enderecos.save(any(Endereco.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UsuarioRepository usuarioRepo = mock(UsuarioRepository.class);
        when(usuarioRepo.findById(ownerId)).thenReturn(Optional.of(actor));
        EnderecoService enderecoService = new EnderecoService(enderecos, usuarioRepo);
        enderecoService.salvar(new EnderecoRequestDTO("Rua", "Centro", "Irecê", "BA", "44900000", ownerId));

        RecompensaRepository recompensas = mock(RecompensaRepository.class);
        ResgateRepository resgates = mock(ResgateRepository.class);
        Recompensa reward = mock(Recompensa.class);
        when(reward.getId()).thenReturn(ownerId);
        when(reward.getNome()).thenReturn("Caneca");
        when(reward.getQuantidade()).thenReturn(2);
        when(reward.getBloqueado()).thenReturn(0);
        when(reward.getCustoPontos()).thenReturn(10.0);
        when(reward.isDisponivel()).thenReturn(true);
        when(recompensas.findById(otherId)).thenReturn(Optional.of(reward));
        when(actor.getPontuacao()).thenReturn(100.0);
        when(doadores.findById(ownerId)).thenReturn(Optional.of(actor));
        Resgate savedResgate = mock(Resgate.class);
        when(savedResgate.getDoador()).thenReturn(actor);
        when(savedResgate.getRecompensa()).thenReturn(reward);
        when(resgates.save(any(Resgate.class))).thenReturn(savedResgate);
        RecompensaService recompensaService = new RecompensaService(recompensas, doadores, resgates);
        recompensaService.resgatar(otherId, ownerId);

        Resgate ownedResgate = mock(Resgate.class);
        when(ownedResgate.getDoador()).thenReturn(actor);
        when(ownedResgate.getRecompensa()).thenReturn(reward);
        when(resgates.findById(otherId)).thenReturn(Optional.of(ownedResgate));
        when(resgates.findByDoador_Id(ownerId)).thenReturn(java.util.List.of());
        when(resgates.findByDoador_IdAndStatus(eq(ownerId), any())).thenReturn(java.util.List.of());
        ResgateService resgateService = new ResgateService(resgates);
        resgateService.buscarPorId(otherId);
        resgateService.listarPorDoador(ownerId);
        resgateService.listarPorDoadorEStatus(ownerId, StatusResgate.PENDENTE);
    }

    @Test
    void administradoresPodemConfirmarRetiradaDeResgate() {
        when(actor.getPerfil()).thenReturn(TipoPerfil.ADMINISTRADOR);
        RecompensaRepository recompensas = mock(RecompensaRepository.class);
        ResgateRepository resgates = mock(ResgateRepository.class);
        Recompensa reward = mock(Recompensa.class);
        when(reward.getId()).thenReturn(ownerId);
        when(reward.getNome()).thenReturn("Caneca");
        when(reward.getQuantidade()).thenReturn(2);
        when(reward.getBloqueado()).thenReturn(1);
        when(reward.getCustoPontos()).thenReturn(10.0);
        when(reward.isDisponivel()).thenReturn(true);
        Resgate resgate = mock(Resgate.class);
        when(resgate.getStatus()).thenReturn(StatusResgate.PENDENTE);
        when(resgate.getRecompensa()).thenReturn(reward);
        when(resgate.getDoador()).thenReturn(actor);
        when(resgates.findById(otherId)).thenReturn(Optional.of(resgate));
        when(resgates.save(any(Resgate.class))).thenReturn(resgate);
        when(recompensas.save(any(Recompensa.class))).thenReturn(reward);
        new RecompensaService(recompensas, mock(DoadorRepository.class), resgates).confirmarRetirada(otherId);
    }
}
