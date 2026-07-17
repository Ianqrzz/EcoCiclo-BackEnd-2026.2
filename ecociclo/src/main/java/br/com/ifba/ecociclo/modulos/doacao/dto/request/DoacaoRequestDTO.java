package br.com.ifba.ecociclo.modulos.doacao.dto.request;

public record DoacaoRequestDTO(
    String nome,
    int quantidade,
    String imagem,
    float peso
) {}
