package br.com.ifba.ecociclo.modulos.avaliacao.dto.request;

import jakarta.validation.constraints.Size;

public record AvaliacaoRequestDTO(
        Integer nota,
        @Size(max = 2000) String comentario,
        java.util.UUID agendamentoId
) {}
