package br.com.ifba.ecociclo.modulos.doacao.repository;

import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {
}
