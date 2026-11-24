package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.usuario.repository.UsuarioRepository; // Ajuste o import
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            var subject = tokenService.getSubject(tokenJWT); // Pega o email dentro do token

            if (!subject.isEmpty()) {
                UserDetails usuario = repository.findByEmail(subject); // Busca no banco

                if (usuario != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    // Força a autenticação no Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        return extrairToken(request.getHeader("Authorization"));
    }

    /**
     * Extrai o token de um cabeçalho no formato {@code Authorization: Bearer <token>}.
     *
     * <p>Retorna {@code null} quando o cabeçalho está ausente, usa outro esquema
     * (ex.: {@code Basic}), não tem o prefixo {@code Bearer} ou vem sem token.
     * O nome do esquema é comparado sem diferenciar maiúsculas (RFC 6750).</p>
     */
    static String extrairToken(String authorizationHeader) {
        if (authorizationHeader == null
                || !authorizationHeader.regionMatches(true, 0, PREFIXO_BEARER, 0, PREFIXO_BEARER.length())) {
            return null;
        }
        String token = authorizationHeader.substring(PREFIXO_BEARER.length()).trim();
        return token.isEmpty() ? null : token;
    }
}