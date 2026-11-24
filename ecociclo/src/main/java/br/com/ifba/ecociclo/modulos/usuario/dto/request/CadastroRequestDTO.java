package br.com.ifba.ecociclo.modulos.usuario.dto.request;

import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroRequestDTO(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 20) String cpf,
        @Size(max = 30) String telefone,
        @NotBlank @Size(max = 180) String email,
        @NotBlank @Size(min = 8, max = 100) String senha,
        TipoPerfil perfil,
        @Valid EnderecoRequestDTO endereco
) {}
