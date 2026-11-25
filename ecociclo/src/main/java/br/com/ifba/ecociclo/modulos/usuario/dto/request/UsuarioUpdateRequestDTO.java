package br.com.ifba.ecociclo.modulos.usuario.dto.request;

import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequestDTO(
        @Size(max = 120) String nome,
        @Size(max = 30) String telefone
) {}
