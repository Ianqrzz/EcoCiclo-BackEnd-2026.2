package br.com.ifba.ecociclo.modulos.usuario.service;

import br.com.ifba.ecociclo.modulos.administrador.model.Administrador;
import br.com.ifba.ecociclo.modulos.administrador.repository.AdministradorRepository;
import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import br.com.ifba.ecociclo.modulos.coletor.repository.ColetorRepository;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.doador.repository.DoadorRepository;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.CadastroRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.UsuarioUpdateRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.response.UsuarioResponseDTO;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final DoadorRepository doadorRepository;
    private final ColetorRepository coletorRepository;
    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO salvar(CadastroRequestDTO dto) {
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario usuarioSalvo;

        switch (dto.perfil()) {
            case DOADOR:
                Doador doador = Doador.builder()
                    .nome(dto.nome())
                    .cpf(dto.cpf())
                    .telefone(dto.telefone())
                    .email(dto.email())
                    .senha(senhaCriptografada)
                    .perfil(dto.perfil())
                    .pontuacao(0.0)
                    .build();
                usuarioSalvo = doadorRepository.save(doador);
                break;

            case COLETOR:
                Coletor coletor = Coletor.builder()
                    .nome(dto.nome())
                    .cpf(dto.cpf())
                    .telefone(dto.telefone())
                    .email(dto.email())
                    .senha(senhaCriptografada)
                    .perfil(dto.perfil())
                    .build();
                usuarioSalvo = coletorRepository.save(coletor);
                break;

            case ADMINISTRADOR:
                Administrador admin = Administrador.builder()
                    .nome(dto.nome())
                    .cpf(dto.cpf())
                    .telefone(dto.telefone())
                    .email(dto.email())
                    .senha(senhaCriptografada)
                    .perfil(dto.perfil())
                    .build();
                usuarioSalvo = administradorRepository.save(admin);
                break;

            default:
                throw new IllegalArgumentException("Perfil inválido para cadastro: " + dto.perfil());
        }

        return mapToResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return mapToResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizar(UUID id, UsuarioUpdateRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        
        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome());
        }
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            usuario.setTelefone(dto.telefone());
        }

        return mapToResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioResponseDTO mapToResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .build();
    }
}
