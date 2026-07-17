package br.com.ifba.ecociclo.modulos.agendamento.controller;

import br.com.ifba.ecociclo.modulos.agendamento.dto.request.AgendamentoRequestDTO;
import br.com.ifba.ecociclo.modulos.agendamento.dto.request.AceitarColetaRequestDTO;
import br.com.ifba.ecociclo.modulos.agendamento.dto.response.AgendamentoResponseDTO;
import br.com.ifba.ecociclo.modulos.agendamento.service.AgendamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO dto) {
        AgendamentoResponseDTO response = agendamentoService.criarAgendamento(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<AgendamentoResponseDTO> aceitar(@PathVariable("id") UUID agendamentoId,
                                                          @Valid @RequestBody AceitarColetaRequestDTO request) {
        AgendamentoResponseDTO response = agendamentoService.aceitarColeta(agendamentoId, request.coletorId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/concluir")
    public ResponseEntity<AgendamentoResponseDTO> concluir(@PathVariable("id") UUID agendamentoId) {
        AgendamentoResponseDTO response = agendamentoService.concluirColeta(agendamentoId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable("id") UUID agendamentoId) {
        agendamentoService.cancelarAgendamento(agendamentoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPendentes() {
        List<AgendamentoResponseDTO> lista = agendamentoService.listarPendentes();
        return ResponseEntity.ok(lista);
    }
}
