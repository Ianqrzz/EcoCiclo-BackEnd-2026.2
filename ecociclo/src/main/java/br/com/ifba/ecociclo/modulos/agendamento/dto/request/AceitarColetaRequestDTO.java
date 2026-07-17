package br.com.ifba.ecociclo.modulos.agendamento.dto.request;

import java.util.UUID;

public record AceitarColetaRequestDTO(
        UUID coletorId
) {}
