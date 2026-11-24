package br.com.ifba.ecociclo.modulos.endereco.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        @Size(max = 180) String logradouro,
        @Size(max = 120) String bairro,
        @Size(max = 120) String cidade,
        @Size(max = 100) String estado,
        @Size(max = 20) String cep,
        UUID usuarioId
) {}
