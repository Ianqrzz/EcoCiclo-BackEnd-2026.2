package br.com.ifba.ecociclo.modulos.doacao.dto.request;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;

public record DoacaoRequestDTO(
        @NotBlank @Size(max = 120) String nome,
        @PositiveOrZero int quantidade,
        @Size(max = 2048) String imagem,
        @Positive float peso
) {}
