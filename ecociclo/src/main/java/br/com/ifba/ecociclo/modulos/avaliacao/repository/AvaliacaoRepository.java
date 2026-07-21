package br.com.ifba.ecociclo.modulos.avaliacao.repository;

import br.com.ifba.ecociclo.modulos.avaliacao.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    boolean existsByAgendamento_Id(UUID agendamentoId);
}
