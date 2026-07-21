package br.com.ifba.ecociclo.modulos.coletor.repository;

import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ColetorRepository extends JpaRepository<Coletor, UUID> {
    List<Coletor> findByAssociacao_Id(UUID associacaoId);
}
