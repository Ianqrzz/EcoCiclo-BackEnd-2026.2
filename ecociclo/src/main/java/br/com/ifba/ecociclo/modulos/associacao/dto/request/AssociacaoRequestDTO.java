package br.com.ifba.ecociclo.modulos.associacao.dto.request;

import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;

public record AssociacaoRequestDTO(
        String nome,
        String cnpj,
        EnderecoRequestDTO endereco
) {}
