package br.com.ifba.ecociclo.modulos.associacao.repository;

import br.com.ifba.ecociclo.modulos.associacao.model.Associacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssociacaoRepository extends JpaRepository<Associacao, UUID> {
}
