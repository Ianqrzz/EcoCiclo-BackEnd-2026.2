package br.com.ifba.ecociclo.modulos.usuario.dto.request;

import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;

public record CadastroRequestDTO(
    String nome,
    String cpf,
    String telefone,
    String email,
    String senha,
    TipoPerfil perfil
) {}
