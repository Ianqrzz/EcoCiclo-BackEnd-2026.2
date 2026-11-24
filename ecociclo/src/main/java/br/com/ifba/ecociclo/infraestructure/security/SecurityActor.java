package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/** Ajuda a conferir se o usuário pode acessar cada recurso. */
public final class SecurityActor {
    private SecurityActor() {}

    public static Usuario current() {
        Object principal = SecurityContextHolder.getContext().getAuthentication() == null
                ? null : SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação obrigatória.");
        }
        return usuario;
    }

    public static void requireSelfOrAdmin(UUID ownerId) {
        Usuario actor = current();
        if (!actor.getId().equals(ownerId) && !actor.getPerfil().name().equals("ADMINISTRADOR")) {
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    public static void requireAdmin() {
        if (!current().getPerfil().name().equals("ADMINISTRADOR")) {
            throw new AccessDeniedException("Somente administradores podem executar esta operação.");
        }
    }
}
