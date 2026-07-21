package br.com.ifba.ecociclo.modulos.associacao.dto.response;

import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AssociacaoResponseDTO(
        UUID id,
        String nome,
        String cnpj,
        EnderecoResponseDTO endereco,
        long totalColetores
) {}
