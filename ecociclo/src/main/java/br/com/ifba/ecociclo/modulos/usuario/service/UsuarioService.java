package br.com.ifba.ecociclo.modulos.usuario.service;

import br.com.ifba.ecociclo.modulos.administrador.model.Administrador;
import br.com.ifba.ecociclo.modulos.administrador.repository.AdministradorRepository;
import br.com.ifba.ecociclo.modulos.associacao.dto.response.AssociacaoResumoDTO;
import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import br.com.ifba.ecociclo.modulos.coletor.repository.ColetorRepository;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.doador.repository.DoadorRepository;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.CadastroRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.request.UsuarioUpdateRequestDTO;
import br.com.ifba.ecociclo.modulos.usuario.dto.response.UsuarioResponseDTO;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.repository.UsuarioRepository;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
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
                vincularEndereco(doador, dto.endereco());
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
                vincularEndereco(coletor, dto.endereco());
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
                vincularEndereco(admin, dto.endereco());
                usuarioSalvo = administradorRepository.save(admin);
                break;

            default:
                throw new IllegalArgumentException("Perfil inválido para cadastro: " + dto.perfil());
        }

        return converterParaResponse(usuarioSalvo);
    }

    private void vincularEndereco(Usuario usuario, EnderecoRequestDTO enderecoDto) {
        if (enderecoDto != null) {
            Endereco endereco = new Endereco();
            endereco.setLogradouro(enderecoDto.logradouro());
            endereco.setBairro(enderecoDto.bairro());
            endereco.setCidade(enderecoDto.cidade());
            endereco.setEstado(enderecoDto.estado());
            endereco.setCep(enderecoDto.cep());
            usuario.adicionarEndereco(endereco);
        }
    }

    @Transactional
    public UsuarioResponseDTO adicionarEndereco(UUID usuarioId, EnderecoRequestDTO enderecoDto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (enderecoDto == null) {
            throw new IllegalArgumentException("Os dados do endereço são obrigatórios.");
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(enderecoDto.logradouro());
        endereco.setBairro(enderecoDto.bairro());
        endereco.setCidade(enderecoDto.cidade());
        endereco.setEstado(enderecoDto.estado());
        endereco.setCep(enderecoDto.cep());
        usuario.adicionarEndereco(endereco);

        usuarioRepository.save(usuario);
        return converterParaResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        return converterParaResponse(usuario);
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

        return converterParaResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        usuarioRepository.deleteById(id);
    }

    public UsuarioResponseDTO converterParaResponse(Usuario usuario) {
        EnderecoResponseDTO enderecoDto = null;
        List<EnderecoResponseDTO> enderecosDto = null;
        if (usuario.getEnderecos() != null && !usuario.getEnderecos().isEmpty()) {
            enderecosDto = usuario.getEnderecos().stream()
                    .map(e -> new EnderecoResponseDTO(
                            e.getId(),
                            e.getLogradouro(),
                            e.getBairro(),
                            e.getCidade(),
                            e.getEstado(),
                            e.getCep(),
                            e.getUsuario() != null ? e.getUsuario().getId() : null
                    ))
                    .toList();

            Endereco e = usuario.getEnderecos().get(0);
            enderecoDto = new EnderecoResponseDTO(
                    e.getId(),
                    e.getLogradouro(),
                    e.getBairro(),
                    e.getCidade(),
                    e.getEstado(),
                    e.getCep(),
                    e.getUsuario() != null ? e.getUsuario().getId() : null
            );
        }
        Double pontuacao = usuario instanceof Doador doador ? doador.getPontuacao() : null;
        AssociacaoResumoDTO associacaoDto = null;
        if (usuario instanceof Coletor coletor && coletor.getAssociacao() != null) {
            var associacao = coletor.getAssociacao();
            associacaoDto = new AssociacaoResumoDTO(
                    associacao.getId(),
                    associacao.getNome(),
                    associacao.getCnpj()
            );
        }

        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .telefone(usuario.getTelefone())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .pontuacao(pontuacao)
                .endereco(enderecoDto)
                .enderecos(enderecosDto)
                .associacao(associacaoDto)
                .build();
    }
}
