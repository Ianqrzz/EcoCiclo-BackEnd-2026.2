package br.com.ifba.ecociclo.modulos.recompensa.dto.request;

public record RecompensaRequestDTO(
        String nome,
        Integer quantidade,
        String imagem,
        String descricao,
        Double custoPontos
) {}
