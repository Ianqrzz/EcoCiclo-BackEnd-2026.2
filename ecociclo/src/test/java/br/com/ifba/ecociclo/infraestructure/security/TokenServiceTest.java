package br.com.ifba.ecociclo.infraestructure.security;

import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.usuario.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da issue #6: a expiração do JWT deve ser exatamente a duração configurada,
 * independentemente do fuso horário da JVM (máquina local x container Docker em UTC).
 */
class TokenServiceTest {

    private static final String SECRET = "segredo-de-teste-com-pelo-menos-32-bytes!!";
    private static final Instant AGORA = Instant.parse("2026-11-24T19:00:00Z");
    private final TimeZone fusoOriginal = TimeZone.getDefault();

    @AfterEach
    void restaurarFuso() {
        TimeZone.setDefault(fusoOriginal);
    }

    private Usuario usuario() {
        return Doador.builder().email("doador@ecociclo.com").build();
    }

    private DecodedJWT gerarEDecodificar(Duration expiracao) {
        Clock relogio = Clock.fixed(AGORA, ZoneOffset.UTC);
        TokenService service = new TokenService(SECRET, expiracao, relogio);
        return JWT.decode(service.gerarToken(usuario()));
    }

    @Test
    void deveExpirarExatamenteDuasHorasAposEmissao() {
        DecodedJWT jwt = gerarEDecodificar(Duration.ofHours(2));

        assertThat(jwt.getIssuedAtAsInstant()).isEqualTo(AGORA);
        assertThat(jwt.getExpiresAtAsInstant()).isEqualTo(AGORA.plus(Duration.ofHours(2)));
    }

    @Test
    void expiracaoNaoDependeDoFusoDaJvm() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC")));          // como no Docker
        Instant expDocker = gerarEDecodificar(Duration.ofHours(2)).getExpiresAtAsInstant();

        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("America/Bahia"))); // como na máquina local
        Instant expLocal = gerarEDecodificar(Duration.ofHours(2)).getExpiresAtAsInstant();

        assertThat(expDocker).isEqualTo(expLocal).isEqualTo(AGORA.plus(Duration.ofHours(2)));
    }

    @Test
    void deveRespeitarDuracaoConfigurada() {
        DecodedJWT jwt = gerarEDecodificar(Duration.ofMinutes(30));

        assertThat(Duration.between(jwt.getIssuedAtAsInstant(), jwt.getExpiresAtAsInstant()))
                .isEqualTo(Duration.ofMinutes(30));
    }

    @Test
    void tokenValidoRetornaEmailDoUsuario() {
        TokenService service = new TokenService(SECRET, Duration.ofHours(2), Clock.systemUTC());

        assertThat(service.getSubject(service.gerarToken(usuario()))).isEqualTo("doador@ecociclo.com");
    }

    @Test
    void tokenExpiradoRetornaSubjectVazio() {
        Clock ontem = Clock.fixed(Instant.now().minus(Duration.ofDays(1)), ZoneOffset.UTC);
        String tokenAntigo = new TokenService(SECRET, Duration.ofHours(2), ontem).gerarToken(usuario());

        TokenService service = new TokenService(SECRET, Duration.ofHours(2), Clock.systemUTC());
        assertThat(service.getSubject(tokenAntigo)).isEmpty();
    }

    // ---------- Issue #7: segredo obrigatório e forte ----------

    @Test
    void naoDeveIniciarSemSegredo() {
        assertThatThrownBy(() -> new TokenService("", Duration.ofHours(2), Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("API_SECURITY_TOKEN_SECRET");

        assertThatThrownBy(() -> new TokenService(null, Duration.ofHours(2), Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void naoDeveIniciarComSegredoFraco() {
        String antigaChavePadrao = "chave-secreta-padrao-ecociclo"; // 29 bytes

        assertThatThrownBy(() -> new TokenService(antigaChavePadrao, Duration.ofHours(2), Clock.systemUTC()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("fraco");
    }

    @Test
    void tokenAssinadoComOutroSegredoEhRejeitado() {
        TokenService atacante = new TokenService("outro-segredo-qualquer-com-32-bytes-ou-mais", Duration.ofHours(2), Clock.systemUTC());
        String tokenForjado = atacante.gerarToken(usuario());

        TokenService api = new TokenService(SECRET, Duration.ofHours(2), Clock.systemUTC());
        assertThat(api.getSubject(tokenForjado)).isEmpty();
    }
}