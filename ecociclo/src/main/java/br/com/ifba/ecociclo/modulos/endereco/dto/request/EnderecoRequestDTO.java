package br.com.ifba.ecociclo.modulos.endereco.dto.request;

import java.util.UUID;

public record EnderecoRequestDTO(
    String logradouro,
    String bairro,
    String cidade,
    String estado,
    String cep,
    UUID usuarioId
) {}
