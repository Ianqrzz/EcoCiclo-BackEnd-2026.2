package br.com.ifba.ecociclo.modulos.endereco.service;

import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import br.com.ifba.ecociclo.modulos.endereco.repository.EnderecoRepository;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import br.com.ifba.ecociclo.modulos.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public EnderecoResponseDTO salvar(EnderecoRequestDTO dto) {
        Endereco endereco = Endereco.builder()
                .logradouro(dto.logradouro())
                .bairro(dto.bairro())
                .cidade(dto.cidade())
                .estado(dto.estado())
                .cep(dto.cep())
                .build();

        if (dto.usuarioId() == null) {
            throw new RuntimeException("usuarioId é obrigatório para vincular o endereço ao perfil.");
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        usuario.adicionarEndereco(endereco);
        usuarioRepository.save(usuario);
        return mapToResponse(endereco);
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponseDTO> listarTodos() {
        return enderecoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnderecoResponseDTO buscarPorId(UUID id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));
        return mapToResponse(endereco);
    }

    @Transactional
    public EnderecoResponseDTO atualizar(UUID id, EnderecoRequestDTO dto) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado."));
        
        if (dto.logradouro() != null && !dto.logradouro().isBlank()) endereco.setLogradouro(dto.logradouro());
        if (dto.bairro() != null && !dto.bairro().isBlank()) endereco.setBairro(dto.bairro());
        if (dto.cidade() != null && !dto.cidade().isBlank()) endereco.setCidade(dto.cidade());
        if (dto.estado() != null && !dto.estado().isBlank()) endereco.setEstado(dto.estado());
        if (dto.cep() != null && !dto.cep().isBlank()) endereco.setCep(dto.cep());

        return mapToResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!enderecoRepository.existsById(id)) {
            throw new RuntimeException("Endereço não encontrado.");
        }
        enderecoRepository.deleteById(id);
    }

    private EnderecoResponseDTO mapToResponse(Endereco endereco) {
        return EnderecoResponseDTO.builder()
                .id(endereco.getId())
                .logradouro(endereco.getLogradouro())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .estado(endereco.getEstado())
                .cep(endereco.getCep())
                .usuarioId(endereco.getUsuario() != null ? endereco.getUsuario().getId() : null)
                .build();
    }
}
