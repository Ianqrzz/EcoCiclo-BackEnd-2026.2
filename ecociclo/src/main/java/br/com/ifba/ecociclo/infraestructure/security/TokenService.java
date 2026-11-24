package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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
 *
 * <p>O segredo de assinatura é obrigatório e deve vir da variável de ambiente
 * {@code API_SECURITY_TOKEN_SECRET}. Não existe valor padrão no código: se o
 * segredo estiver ausente ou fraco, a aplicação não inicia.</p>
 */
@Service
public class TokenService {

    static final String ISSUER = "EcoCiclo_API";

    /** O HMAC-SHA256 recomenda chave de no mínimo 256 bits (32 bytes). */
    static final int TAMANHO_MINIMO_SEGREDO_BYTES = 32;

    private final Algorithm algorithm;
    private final Duration expiracao;
    private final Clock clock;

    /**
     * Construtor usado pelo Spring.
     *
     * @param secret    chave de assinatura HMAC, lida de {@code API_SECURITY_TOKEN_SECRET}
     * @param expiracao validade do token; aceita formatos como {@code 2h}, {@code 30m} ou {@code PT2H}
     */
    @Autowired
    public TokenService(
            @Value("${api.security.token.secret:}") String secret,
            @Value("${api.security.token.expiration:2h}") Duration expiracao) {
        this(secret, expiracao, Clock.systemUTC());
    }

    /**
     * Construtor para testes: permite fixar o relógio e verificar as datas com precisão.
     */
    TokenService(String secret, Duration expiracao, Clock clock) {
        validarSegredo(secret);
        this.algorithm = Algorithm.HMAC256(secret); // criado uma única vez
        this.expiracao = expiracao;
        this.clock = clock;
    }

    /**
     * Impede a aplicação de subir com segredo ausente ou fraco (fail-fast).
     */
    private static void validarSegredo(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "Segredo JWT não configurado. Defina a variável de ambiente API_SECURITY_TOKEN_SECRET.");
        }
        int tamanho = secret.getBytes(StandardCharsets.UTF_8).length;
        if (tamanho < TAMANHO_MINIMO_SEGREDO_BYTES) {
            throw new IllegalStateException(
                    "Segredo JWT fraco: possui " + tamanho + " bytes, o mínimo é "
                            + TAMANHO_MINIMO_SEGREDO_BYTES + ". Gere um com: openssl rand -base64 32");
        }
    }

    public String gerarToken(Usuario usuario) {
        try {
            Instant agora = Instant.now(clock);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withIssuedAt(agora)
                    .withExpiresAt(agora.plus(expiracao))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token jwt", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return ""; // Token inválido ou expirado (tratamento será padronizado nas issues de exceções)
        }
    }
}