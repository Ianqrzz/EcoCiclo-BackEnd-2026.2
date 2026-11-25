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
import br.com.ifba.ecociclo.modulos.recompensa.repository.RecompensaRepository;
import br.com.ifba.ecociclo.modulos.recompensa.service.RecompensaService;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
