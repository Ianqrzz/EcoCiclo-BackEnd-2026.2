package br.com.ifba.ecociclo.modulos.usuario.dto.request;

import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;

public record CadastroRequestDTO(
        String nome,
        String cpf,
        String telefone,
        String email,
        String senha,
        TipoPerfil perfil,
        EnderecoRequestDTO endereco
) {}