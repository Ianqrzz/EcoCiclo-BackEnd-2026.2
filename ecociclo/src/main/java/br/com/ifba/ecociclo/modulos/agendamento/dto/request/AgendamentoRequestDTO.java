package br.com.ifba.ecociclo.modulos.agendamento.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;

public record AgendamentoRequestDTO(
        UUID doadorId,
        UUID enderecoId,
        LocalDateTime dataColeta,
        String observacoes,
        DoacaoRequestDTO doacao
) {}
