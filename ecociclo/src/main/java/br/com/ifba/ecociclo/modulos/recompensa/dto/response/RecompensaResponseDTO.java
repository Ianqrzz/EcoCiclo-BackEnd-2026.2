package br.com.ifba.ecociclo.modulos.recompensa.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RecompensaResponseDTO(
        UUID id,
        String nome,
        Integer quantidade,
        Integer bloqueado,
        Integer quantidadeDisponivel,
        String imagem,
        String descricao,
        Double custoPontos,
        boolean disponivel
) {}
