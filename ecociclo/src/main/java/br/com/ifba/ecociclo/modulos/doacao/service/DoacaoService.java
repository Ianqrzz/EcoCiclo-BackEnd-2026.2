package br.com.ifba.ecociclo.modulos.doacao.service;

import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.doacao.dto.response.DoacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import br.com.ifba.ecociclo.modulos.doacao.repository.DoacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoacaoService {

    private final DoacaoRepository doacaoRepository;

    @Transactional
    public DoacaoResponseDTO salvar(DoacaoRequestDTO dto) {
        Doacao doacao = Doacao.builder()
                .nome(dto.nome())
                .quantidade(dto.quantidade())
                .imagem(dto.imagem())
                .peso(dto.peso())
                .build();
        return mapToResponse(doacaoRepository.save(doacao));
    }

    @Transactional(readOnly = true)
    public List<DoacaoResponseDTO> listarTodas() {
        return doacaoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DoacaoResponseDTO buscarPorId(UUID id) {
        Doacao doacao = doacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doação não encontrada com o ID: " + id));
        return mapToResponse(doacao);
    }

    private DoacaoResponseDTO mapToResponse(Doacao doacao) {
        return DoacaoResponseDTO.builder()
                .id(doacao.getId())
                .nome(doacao.getNome())
                .quantidade(doacao.getQuantidade())
                .imagem(doacao.getImagem())
                .peso(doacao.getPeso())
                .build();
    }
}
