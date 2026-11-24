package br.com.ifba.ecociclo.infraestructure.model;

import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceEntityTest {

    @Test
    void entidadesComMesmoIdDevemSerIguais() {
        UUID id = UUID.randomUUID();
        Doacao primeira = Doacao.builder().id(id).nome("Papel").build();
        Doacao segunda = Doacao.builder().id(id).nome("Papel").build();

        assertEquals(primeira, segunda);
    }

    @Test
    void usuariosComMesmoIdDevemSerIguaisMesmoComDadosDiferentes() {
        UUID id = UUID.randomUUID();
        Doador primeiro = Doador.builder().id(id).nome("Ana").build();
        Doador segundo = Doador.builder().id(id).nome("Ana Maria").build();

        assertEquals(primeiro, segundo);
        assertEquals(primeiro.hashCode(), segundo.hashCode());
    }

    @Test
    void hashSetDeveEncontrarEntidadeDepoisDeAlterada() {
        Doacao doacao = Doacao.builder().id(UUID.randomUUID()).nome("Papel").build();
        Set<Doacao> doacoes = new HashSet<>();
        doacoes.add(doacao);

        doacao.setNome("Vidro");

        assertTrue(doacoes.contains(doacao));
    }

    @Test
    void builderDeveIniciarListaDeEnderecosVazia() {
        Doador doador = Doador.builder().build();

        assertNotNull(doador.getEnderecos());
        assertTrue(doador.getEnderecos().isEmpty());
    }
}
