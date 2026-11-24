package br.com.ifba.ecociclo;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste da issue #15: o hash BCrypt do administrador em {@code data.sql}
 * deve corresponder à senha documentada no comentário do arquivo (Admin@123).
 */
class DataSqlSenhaAdminTest {

    private static final String SENHA_DOCUMENTADA = "Admin@123";
    private static final Pattern HASH_BCRYPT = Pattern.compile("'(\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53})'");

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String hashDoAdmin() throws IOException {
        String sql = new ClassPathResource("data.sql").getContentAsString(StandardCharsets.UTF_8);
        Matcher matcher = HASH_BCRYPT.matcher(sql);
        assertThat(matcher.find()).as("data.sql deve conter um hash BCrypt").isTrue();
        return matcher.group(1);
    }

    @Test
    void hashDoAdminCorrespondeASenhaDocumentada() throws IOException {
        assertThat(encoder.matches(SENHA_DOCUMENTADA, hashDoAdmin())).isTrue();
    }

    @Test
    void hashDoAdminNaoEhOExemploPublicoDaSenhaPassword() throws IOException {
        assertThat(encoder.matches("password", hashDoAdmin())).isFalse();
    }
}