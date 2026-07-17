package br.com.ifba.ecociclo.modulos.doacao.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record DoacaoResponseDTO(
    UUID id,
    String nome,
    int quantidade,
    String imagem,
    float peso
) {}
