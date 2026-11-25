package br.com.ifba.ecociclo.modulos.associacao.dto.request;

import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record AssociacaoRequestDTO(
        @Size(max = 120) String nome,
        @Size(max = 20) String cnpj,
        @Valid EnderecoRequestDTO endereco
) {}
