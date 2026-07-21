package br.com.ifba.ecociclo.modulos.associacao.dto.response;

import java.util.UUID;

public record AssociacaoResumoDTO(
        UUID id,
        String nome,
        String cnpj
) {}
