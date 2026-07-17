package br.com.ifba.ecociclo.modulos.agendamento.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.ifba.ecociclo.modulos.agendamento.enums.StatusAgendamento;
import br.com.ifba.ecociclo.modulos.doacao.dto.response.DoacaoResponseDTO;

public record AgendamentoResponseDTO(
        UUID id,
        LocalDateTime dataColeta,
        LocalDateTime dataCriacao,
        String observacoes,
        double pontosGerados,
        StatusAgendamento status,
        UUID doadorId,
        UUID coletorId,
        UUID enderecoId,
        DoacaoResponseDTO doacao) {}
