package br.com.ifba.ecociclo.modulos.resgate.dto.response;

import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ResgateResponseDTO(
        UUID id,
        LocalDateTime data,
        double pontosGastos,
        StatusResgate status,
        UUID doadorId,
        String doadorNome,
        UUID recompensaId,
        String recompensaNome
) {}
