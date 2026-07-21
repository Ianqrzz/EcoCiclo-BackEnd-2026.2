package br.com.ifba.ecociclo.modulos.avaliacao.service;

import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import br.com.ifba.ecociclo.modulos.agendamento.repository.AgendamentoRepository;
import br.com.ifba.ecociclo.modulos.avaliacao.dto.request.AvaliacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.avaliacao.dto.response.AvaliacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.avaliacao.model.Avaliacao;
import br.com.ifba.ecociclo.modulos.avaliacao.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Transactional
    public AvaliacaoResponseDTO salvar(AvaliacaoRequestDTO dto) {
        validarNota(dto.nota());

        Agendamento agendamento = buscarAgendamento(dto.agendamentoId());
        validarAgendamentoComColetor(agendamento);

        if (avaliacaoRepository.existsByAgendamento_Id(dto.agendamentoId())) {
            throw new RuntimeException("Já existe uma avaliação para este agendamento.");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(dto.nota())
                .comentario(dto.comentario())
                .data(LocalDateTime.now())
                .agendamento(agendamento)
                .build();

        return toResponse(avaliacaoRepository.save(avaliacao));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listarTodas() {
        return avaliacaoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO buscarPorId(UUID id) {
        return toResponse(buscarEntidadePorId(id));
    }

    @Transactional
    public AvaliacaoResponseDTO atualizar(UUID id, AvaliacaoRequestDTO dto) {
        Avaliacao avaliacao = buscarEntidadePorId(id);

        if (dto.nota() != null) {
            validarNota(dto.nota());
            avaliacao.setNota(dto.nota());
        }
        if (dto.comentario() != null) {
            avaliacao.setComentario(dto.comentario());
        }
        if (dto.agendamentoId() != null && !dto.agendamentoId().equals(avaliacao.getAgendamento().getId())) {
            Agendamento agendamento = buscarAgendamento(dto.agendamentoId());
            validarAgendamentoComColetor(agendamento);
            if (avaliacaoRepository.existsByAgendamento_Id(dto.agendamentoId())) {
                throw new RuntimeException("Já existe uma avaliação para este agendamento.");
            }
            avaliacao.setAgendamento(agendamento);
        }

        return toResponse(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public void deletar(UUID id) {
        if (!avaliacaoRepository.existsById(id)) {
            throw new RuntimeException("Avaliação não encontrada.");
        }
        avaliacaoRepository.deleteById(id);
    }

    private Avaliacao buscarEntidadePorId(UUID id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada."));
    }

    private Agendamento buscarAgendamento(UUID agendamentoId) {
        if (agendamentoId == null) {
            throw new RuntimeException("Agendamento não informado.");
        }
        return agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));
    }

    private void validarAgendamentoComColetor(Agendamento agendamento) {
        if (agendamento.getDoador() == null) {
            throw new RuntimeException("O agendamento não possui doador vinculado.");
        }
        if (agendamento.getColetor() == null) {
            throw new RuntimeException("Só é possível avaliar um coletor após o agendamento ter um coletor vinculado.");
        }
    }

    private void validarNota(Integer nota) {
        if (nota == null || nota < 1 || nota > 5) {
            throw new RuntimeException("A nota deve estar entre 1 e 5.");
        }
    }

    private AvaliacaoResponseDTO toResponse(Avaliacao avaliacao) {
        var agendamento = avaliacao.getAgendamento();
        return AvaliacaoResponseDTO.builder()
                .id(avaliacao.getId())
                .nota(avaliacao.getNota())
                .comentario(avaliacao.getComentario())
                .data(avaliacao.getData())
                .agendamentoId(agendamento != null ? agendamento.getId() : null)
                .doadorId(agendamento != null && agendamento.getDoador() != null ? agendamento.getDoador().getId() : null)
                .coletorId(agendamento != null && agendamento.getColetor() != null ? agendamento.getColetor().getId() : null)
                .build();
    }
}
