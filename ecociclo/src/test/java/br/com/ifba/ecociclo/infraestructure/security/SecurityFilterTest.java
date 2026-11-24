package br.com.ifba.ecociclo.infraestructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes da issue #17: o filtro só deve aceitar o cabeçalho
 * {@code Authorization: Bearer <token>}.
 */
class SecurityFilterTest {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.payload.assinatura";

    @Test
    void deveExtrairTokenComPrefixoBearer() {
        assertThat(SecurityFilter.extrairToken("Bearer " + TOKEN)).isEqualTo(TOKEN);
    }

    @Test
    void prefixoBearerNaoDiferenciaMaiusculas() {
        assertThat(SecurityFilter.extrairToken("bearer " + TOKEN)).isEqualTo(TOKEN);
        assertThat(SecurityFilter.extrairToken("BEARER " + TOKEN)).isEqualTo(TOKEN);
    }

    @Test
    void deveIgnorarEspacosExtrasAposOToken() {
        assertThat(SecurityFilter.extrairToken("Bearer " + TOKEN + "   ")).isEqualTo(TOKEN);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            TOKEN,                    // sem prefixo (comportamento antigo aceitava)
            "Basic dXNlcjpzZW5oYQ==", // outro esquema de autenticação
            "Bearer",                 // prefixo sem token
            "Bearer    ",             // prefixo só com espaços
            "Token " + TOKEN,         // prefixo errado
            "xBearer " + TOKEN        // "Bearer" fora do início
    })
    void deveRejeitarCabecalhoForaDoFormatoBearer(String cabecalho) {
        assertThat(SecurityFilter.extrairToken(cabecalho)).isNull();
    }
}