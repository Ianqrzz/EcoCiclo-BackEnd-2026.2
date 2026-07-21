package br.com.ifba.ecociclo.modulos.avaliacao.dto.request;

public record AvaliacaoRequestDTO(
        Integer nota,
        String comentario,
        java.util.UUID agendamentoId
) {}
