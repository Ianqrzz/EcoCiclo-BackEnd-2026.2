package br.com.ifba.ecociclo.modulos.usuario.controller;

import br.com.ifba.ecociclo.infraestructure.security.TokenService;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.CadastroRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.UsuarioUpdateRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.response.UsuarioResponseDTO;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager manager;
    private final TokenService tokenService;

    // --- NOVO ENDPOINT DE LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> efetuarLogin(@RequestBody AutenticacaoDTO dados) {
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
            var authentication = manager.authenticate(authenticationToken);

            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

            return ResponseEntity.ok(new TokenResponseDTO(tokenJWT));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou senha incorretos");
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody CadastroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvar(dto));
    }

    @PostMapping("/{id}/enderecos")
    public ResponseEntity<UsuarioResponseDTO> adicionarEndereco(@PathVariable UUID id,
                                                                @RequestBody EnderecoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.adicionarEndereco(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }



    @GetMapping("/me")
    public ResponseEntity<?> obterUsuarioLogado() {
        // Pega as informações do usuário que o Spring Security validou através do Token
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            var usuarioLogado = (Usuario) authentication.getPrincipal();
            // Usa o serviço que você já tem para buscar o usuário pelo ID e retornar como DTO
            return ResponseEntity.ok(usuarioService.buscarPorId(usuarioLogado.getId()));
        }

        // Retorna 401 em vez de estourar Erro 500 no servidor
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sessão inválida ou não autenticada.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable UUID id, @RequestBody UsuarioUpdateRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

// --- DTOs auxiliares para o Login ---
// Se preferir, você pode criar essas classes/records dentro do seu pacote 'dto' depois.
record AutenticacaoDTO(String email, String senha) {}
record TokenResponseDTO(String token) {}
