package br.com.ifba.ecociclo.modulos.avaliacao.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AvaliacaoResponseDTO(
        UUID id,
        Integer nota,
        String comentario,
        LocalDateTime data,
        UUID agendamentoId,
        UUID doadorId,
        UUID coletorId
) {}
