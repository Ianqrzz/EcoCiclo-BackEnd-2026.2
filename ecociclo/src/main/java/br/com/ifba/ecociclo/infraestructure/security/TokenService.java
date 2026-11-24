package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Gera e valida os tokens JWT da API.
 *
 * <p>As datas de emissão ({@code iat}) e expiração ({@code exp}) são calculadas
 * com {@link Instant}, que representa um ponto absoluto na linha do tempo (UTC).
 * Assim o resultado é o mesmo na máquina do desenvolvedor (America/Bahia) e no
 * container Docker (UTC), sem depender do fuso horário da JVM.</p>
 */
@Service
public class TokenService {

    static final String ISSUER = "EcoCiclo_API";

    private final String secret;
    private final Duration expiracao;
    private final Clock clock;

    /**
     * Construtor usado pelo Spring.
     *
     * @param secret    chave de assinatura HMAC (a chave padrão será removida na issue #7)
     * @param expiracao validade do token; aceita formatos como {@code 2h}, {@code 30m} ou {@code PT2H}
     */
    @Autowired
    public TokenService(
            @Value("${api.security.token.secret:chave-secreta-padrao-ecociclo}") String secret,
            @Value("${api.security.token.expiration:2h}") Duration expiracao) {
        this(secret, expiracao, Clock.systemUTC());
    }

    /**
     * Construtor para testes: permite fixar o relógio e verificar as datas com precisão.
     */
    TokenService(String secret, Duration expiracao, Clock clock) {
        this.secret = secret;
        this.expiracao = expiracao;
        this.clock = clock;
    }

    public String gerarToken(Usuario usuario) {
        try {
            Instant agora = Instant.now(clock);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withIssuedAt(agora)
                    .withExpiresAt(agora.plus(expiracao))
                    .sign(algoritmo());
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            return JWT.require(algoritmo())
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return ""; // Token inválido ou expirado (tratamento será padronizado nas issues de exceções)
        }
    }

    private Algorithm algoritmo() {
        return Algorithm.HMAC256(secret);
    }
}