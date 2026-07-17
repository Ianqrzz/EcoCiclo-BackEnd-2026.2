package br.com.ifba.ecociclo.modulos.agendamento.repository;

import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
}
