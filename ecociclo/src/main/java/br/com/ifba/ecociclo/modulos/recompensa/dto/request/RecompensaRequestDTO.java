package br.com.ifba.ecociclo.modulos.recompensa.dto.request;

import jakarta.validation.constraints.Size;

public record RecompensaRequestDTO(
        @Size(max = 120) String nome,
        Integer quantidade,
        @Size(max = 2048) String imagem,
        @Size(max = 2000) String descricao,
        Double custoPontos
) {}
