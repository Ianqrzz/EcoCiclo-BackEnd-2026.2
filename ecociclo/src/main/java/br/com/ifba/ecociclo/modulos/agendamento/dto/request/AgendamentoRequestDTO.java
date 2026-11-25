package br.com.ifba.ecociclo.modulos.agendamento.dto.request;

import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendamentoRequestDTO(
        UUID doadorId,
        UUID enderecoId,
        LocalDateTime dataColeta,
        @Size(max = 1000) String observacoes,
        @Valid DoacaoRequestDTO doacao
) {}
