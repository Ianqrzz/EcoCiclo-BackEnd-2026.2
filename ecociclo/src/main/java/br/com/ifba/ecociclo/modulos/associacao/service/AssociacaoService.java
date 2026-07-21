package br.com.ifba.ecociclo.modulos.associacao.service;

import br.com.ifba.ecociclo.modulos.associacao.dto.request.AssociacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.associacao.dto.response.AssociacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.associacao.model.Associacao;
import br.com.ifba.ecociclo.modulos.associacao.repository.AssociacaoRepository;
import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import br.com.ifba.ecociclo.modulos.coletor.repository.ColetorRepository;
import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.modulos.usuario.dto.response.UsuarioResponseDTO;
import br.com.ifba.ecociclo.modulos.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssociacaoService {

    private final AssociacaoRepository associacaoRepository;
    private final ColetorRepository coletorRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public AssociacaoResponseDTO salvar(AssociacaoRequestDTO dto) {
        Associacao associacao = Associacao.builder()
                .nome(dto.nome())
                .cnpj(dto.cnpj())
                .build();
        vincularEndereco(associacao, dto);
        return toResponse(associacaoRepository.save(associacao));
    }

    @Transactional(readOnly = true)
    public List<AssociacaoResponseDTO> listarTodas() {
        return associacaoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssociacaoResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public AssociacaoResponseDTO atualizar(UUID id, AssociacaoRequestDTO dto) {
        Associacao associacao = buscarEntidadePorId(id);

        if (dto.nome() != null && !dto.nome().isBlank()) {
            associacao.setNome(dto.nome());
        }
        if (dto.cnpj() != null && !dto.cnpj().isBlank()) {
            associacao.setCnpj(dto.cnpj());
        }
        if (dto.endereco() != null) {
            atualizarEndereco(associacao, dto);
        }

        return toResponse(associacaoRepository.save(associacao));
    }

    @Transactional
    public void deletar(UUID id) {
        Associacao associacao = buscarEntidadePorId(id);
        coletorRepository.findByAssociacao_Id(id).forEach(coletor -> {
            coletor.setAssociacao(null);
            coletorRepository.save(coletor);
        });
        associacaoRepository.delete(associacao);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarColetores() {
        return coletorRepository.findAll().stream()
                .map(usuarioService::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO atribuirColetor(UUID associacaoId, UUID coletorId) {
        Associacao associacao = buscarEntidadePorId(associacaoId);
        Coletor coletor = coletorRepository.findById(coletorId)
                .orElseThrow(() -> new RuntimeException("Coletor não encontrado."));

        coletor.setAssociacao(associacao);
        coletorRepository.save(coletor);
        return usuarioService.converterParaResponse(coletor);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarMembros(UUID associacaoId) {
        buscarEntidadePorId(associacaoId);
        return coletorRepository.findByAssociacao_Id(associacaoId).stream()
                .map(usuarioService::converterParaResponse)
                .collect(Collectors.toList());
    }

    private Associacao buscarEntidadePorId(UUID id) {
        return associacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Associação não encontrada."));
    }

    private void vincularEndereco(Associacao associacao, AssociacaoRequestDTO dto) {
        if (dto.endereco() == null) {
            return;
        }

        associacao.setEndereco(novoEndereco(dto.endereco()));
    }

    private void atualizarEndereco(Associacao associacao, AssociacaoRequestDTO dto) {
        if (associacao.getEndereco() == null) {
            associacao.setEndereco(novoEndereco(dto.endereco()));
            return;
        }

        associacao.getEndereco().setLogradouro(dto.endereco().logradouro());
        associacao.getEndereco().setBairro(dto.endereco().bairro());
        associacao.getEndereco().setCidade(dto.endereco().cidade());
        associacao.getEndereco().setEstado(dto.endereco().estado());
        associacao.getEndereco().setCep(dto.endereco().cep());
    }

    private Endereco novoEndereco(br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO dto) {
        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.logradouro());
        endereco.setBairro(dto.bairro());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());
        endereco.setCep(dto.cep());
        return endereco;
    }

    private AssociacaoResponseDTO toResponse(Associacao associacao) {
        EnderecoResponseDTO enderecoDto = null;
        if (associacao.getEndereco() != null) {
            Endereco e = associacao.getEndereco();
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

        long totalColetores = coletorRepository.findByAssociacao_Id(associacao.getId()).size();

        return AssociacaoResponseDTO.builder()
                .id(associacao.getId())
                .nome(associacao.getNome())
                .cnpj(associacao.getCnpj())
                .endereco(enderecoDto)
                .totalColetores(totalColetores)
                .build();
    }

}
