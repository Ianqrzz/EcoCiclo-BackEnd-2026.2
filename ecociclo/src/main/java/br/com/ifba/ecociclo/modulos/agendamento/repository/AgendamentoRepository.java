package br.com.ifba.ecociclo.modulos.agendamento.repository;

import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import br.com.ifba.ecociclo.modulos.agendamento.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {

    List<Agendamento> findByColetor_Id(UUID coletorId);

    List<Agendamento> findByStatus(StatusAgendamento status);

    List<Agendamento> findByStatusAndColetor_Id(StatusAgendamento status, UUID coletorId);
}
