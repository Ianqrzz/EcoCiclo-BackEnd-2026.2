package br.com.ifba.ecociclo.modulos.usuario.controller;

import br.com.ifba.ecociclo.infraestructure.security.TokenService;
import br.com.ifba.ecociclo.modulos.agendamento.dto.request.AgendamentoRequestDTO;
import br.com.ifba.ecociclo.modulos.associacao.controller.AssociacaoController;
import br.com.ifba.ecociclo.modulos.associacao.dto.request.AssociacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.associacao.service.AssociacaoService;
import br.com.ifba.ecociclo.modulos.avaliacao.controller.AvaliacaoController;
import br.com.ifba.ecociclo.modulos.avaliacao.dto.request.AvaliacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.avaliacao.service.AvaliacaoService;
import br.com.ifba.ecociclo.modulos.doacao.controller.DoacaoController;
import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.doacao.service.DoacaoService;
import br.com.ifba.ecociclo.modulos.endereco.controller.EnderecoController;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.endereco.service.EnderecoService;
import br.com.ifba.ecociclo.modulos.recompensa.controller.RecompensaController;
import br.com.ifba.ecociclo.modulos.recompensa.dto.request.RecompensaRequestDTO;
import br.com.ifba.ecociclo.modulos.recompensa.service.RecompensaService;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.CadastroRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.UsuarioUpdateRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestValidationCoverageTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @AfterEach
    void tearDown() {
        validator = null;
    }

    @Test
    void validaLimitesDosDtosDeRequisicao() {
        UUID id = UUID.randomUUID();
        EnderecoRequestDTO endereco = new EnderecoRequestDTO("R".repeat(181), "bairro", "cidade", "BA", "44900000", id);
        DoacaoRequestDTO doacao = new DoacaoRequestDTO("", -1, "", 0);

        assertFalse(validator.validate(new AgendamentoRequestDTO(id, id, LocalDateTime.now(), "O".repeat(1001), doacao)).isEmpty());
        assertFalse(validator.validate(new AssociacaoRequestDTO("N".repeat(121), "1".repeat(21), endereco)).isEmpty());
        assertFalse(validator.validate(new AvaliacaoRequestDTO(5, "C".repeat(2001), id)).isEmpty());
        assertFalse(validator.validate(doacao).isEmpty());
        assertFalse(validator.validate(endereco).isEmpty());
        assertFalse(validator.validate(new RecompensaRequestDTO("N".repeat(121), 1, "I".repeat(2049), "D".repeat(2001), 1.0)).isEmpty());
        assertFalse(validator.validate(new CadastroRequestDTO("", "1", "1", "", "123", TipoPerfil.DOADOR, endereco)).isEmpty());
        assertFalse(validator.validate(new UsuarioUpdateRequestDTO("N".repeat(121), "T".repeat(31))).isEmpty());
    }

    @Test
    void validaObjetosAninhadosERegrasDeCampo() {
        UUID id = UUID.randomUUID();
        EnderecoRequestDTO enderecoRuim = new EnderecoRequestDTO("R".repeat(181), "b", "c", "BA", "44900000", id);
        AssociacaoRequestDTO associacao = new AssociacaoRequestDTO("Associação", "123", enderecoRuim);
        CadastroRequestDTO cadastro = new CadastroRequestDTO("Nome", "123", "123", "email", "senha1234", TipoPerfil.DOADOR, enderecoRuim);

        assertTrue(validator.validate(associacao).stream().anyMatch(v -> v.getPropertyPath().toString().startsWith("endereco.")));
        assertTrue(validator.validate(cadastro).stream().anyMatch(v -> v.getPropertyPath().toString().startsWith("endereco.")));
        assertTrue(validator.validate(new DoacaoRequestDTO(" ", 1, "", 1)).stream().anyMatch(v -> v.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void controllersAceitamDtosValidosEAsRotasDeEscritaMantemValidacao() throws Exception {
        UUID id = UUID.randomUUID();
        EnderecoRequestDTO endereco = new EnderecoRequestDTO("Rua A", "Centro", "Irecê", "BA", "44900000", id);
        AssociacaoRequestDTO associacaoDto = new AssociacaoRequestDTO("Associação", "123", endereco);
        AvaliacaoRequestDTO avaliacaoDto = new AvaliacaoRequestDTO(5, "Boa coleta", id);
        DoacaoRequestDTO doacaoDto = new DoacaoRequestDTO("Papel", 1, "", 1);
        EnderecoRequestDTO enderecoDto = new EnderecoRequestDTO("Rua A", "Centro", "Irecê", "BA", "44900000", id);
        RecompensaRequestDTO recompensaDto = new RecompensaRequestDTO("Caneca", 1, "", "Caneca", 10.0);
        CadastroRequestDTO cadastroDto = new CadastroRequestDTO("Nome", "123", "123", "a@b.com", "senha1234", TipoPerfil.DOADOR, endereco);
        UsuarioUpdateRequestDTO updateDto = new UsuarioUpdateRequestDTO("Nome", "123");

        AssociacaoService associacaoService = mock(AssociacaoService.class);
        assertEquals(HttpStatus.CREATED, new AssociacaoController(associacaoService).criar(associacaoDto).getStatusCode());
        assertEquals(HttpStatus.OK, new AssociacaoController(associacaoService).atualizar(id, associacaoDto).getStatusCode());

        AvaliacaoService avaliacaoService = mock(AvaliacaoService.class);
        assertEquals(HttpStatus.CREATED, new AvaliacaoController(avaliacaoService).criar(avaliacaoDto).getStatusCode());
        assertEquals(HttpStatus.OK, new AvaliacaoController(avaliacaoService).atualizar(id, avaliacaoDto).getStatusCode());

        assertEquals(HttpStatus.CREATED, new DoacaoController(mock(DoacaoService.class)).cadastrar(doacaoDto).getStatusCode());

        EnderecoController enderecoController = new EnderecoController(mock(EnderecoService.class));
        assertEquals(HttpStatus.CREATED, enderecoController.criar(enderecoDto).getStatusCode());
        assertEquals(HttpStatus.OK, enderecoController.atualizar(id, enderecoDto).getStatusCode());

        RecompensaController recompensaController = new RecompensaController(mock(RecompensaService.class));
        assertEquals(HttpStatus.CREATED, recompensaController.cadastrar(recompensaDto).getStatusCode());
        assertEquals(HttpStatus.OK, recompensaController.atualizar(id, recompensaDto).getStatusCode());

        UsuarioService usuarioService = mock(UsuarioService.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        TokenService tokenService = mock(TokenService.class);
        Usuario usuario = mock(Usuario.class);
        when(manager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken(usuario, "senha"));
        when(tokenService.gerarToken(any())).thenReturn("token");
        UsuarioController usuarioController = new UsuarioController(usuarioService, manager, tokenService);
        assertEquals(HttpStatus.CREATED, usuarioController.criar(cadastroDto).getStatusCode());
        assertEquals(HttpStatus.CREATED, usuarioController.adicionarEndereco(id, enderecoDto).getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.atualizar(id, updateDto).getStatusCode());
        assertEquals(HttpStatus.OK, usuarioController.efetuarLogin(new AutenticacaoDTO("a@b.com", "senha")).getStatusCode());

        assertTrue(AssociacaoController.class.getDeclaredMethod("criar", AssociacaoRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
        assertTrue(AvaliacaoController.class.getDeclaredMethod("criar", AvaliacaoRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
        assertTrue(DoacaoController.class.getDeclaredMethod("cadastrar", DoacaoRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
        assertTrue(EnderecoController.class.getDeclaredMethod("criar", EnderecoRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
        assertTrue(RecompensaController.class.getDeclaredMethod("cadastrar", RecompensaRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
        assertTrue(UsuarioController.class.getDeclaredMethod("criar", CadastroRequestDTO.class).getParameters()[0].isAnnotationPresent(Valid.class));
    }
}
