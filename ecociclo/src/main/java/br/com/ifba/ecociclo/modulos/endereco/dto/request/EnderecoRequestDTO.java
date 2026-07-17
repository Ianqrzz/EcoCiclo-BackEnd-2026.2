package br.com.ifba.ecociclo.modulos.endereco.dto.request;

public record EnderecoRequestDTO(
    String logradouro,
    String bairro,
    String cidade,
    String estado,
    String cep
) {}
