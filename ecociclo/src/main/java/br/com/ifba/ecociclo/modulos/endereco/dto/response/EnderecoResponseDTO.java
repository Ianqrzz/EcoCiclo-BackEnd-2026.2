package br.com.ifba.ecociclo.modulos.endereco.dto.response;

import lombok.Builder;
import java.util.UUID;

@Builder
public record EnderecoResponseDTO(
    UUID id,
    String logradouro,
    String bairro,
    String cidade,
    String estado,
    String cep,
    UUID usuarioId
) {}
