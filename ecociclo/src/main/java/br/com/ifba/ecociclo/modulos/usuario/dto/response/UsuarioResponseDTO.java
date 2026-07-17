package br.com.ifba.ecociclo.modulos.usuario.dto.response;

import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import lombok.Builder;
import java.util.UUID;

@Builder
public record UsuarioResponseDTO(
    UUID id,
    String nome,
    String cpf,
    String telefone,
    String email,
    TipoPerfil perfil
) {}
