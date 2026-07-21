package br.com.ifba.ecociclo.modulos.recompensa.controller;

import br.com.ifba.ecociclo.modulos.recompensa.dto.request.RecompensaRequestDTO;
import br.com.ifba.ecociclo.modulos.recompensa.dto.request.ResgatarRecompensaRequestDTO;
import br.com.ifba.ecociclo.modulos.recompensa.dto.response.RecompensaResponseDTO;
import br.com.ifba.ecociclo.modulos.recompensa.service.RecompensaService;
import br.com.ifba.ecociclo.modulos.resgate.dto.response.ResgateResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recompensas")
@RequiredArgsConstructor
public class RecompensaController {

    private final RecompensaService recompensaService;

    @PostMapping
    public ResponseEntity<RecompensaResponseDTO> cadastrar(@RequestBody RecompensaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recompensaService.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<RecompensaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(recompensaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecompensaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(recompensaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecompensaResponseDTO> atualizar(@PathVariable UUID id, @RequestBody RecompensaRequestDTO dto) {
        return ResponseEntity.ok(recompensaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        recompensaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/resgatar")
    public ResponseEntity<ResgateResponseDTO> resgatar(@PathVariable UUID id,
                                                       @RequestBody ResgatarRecompensaRequestDTO dto) {
        return ResponseEntity.ok(recompensaService.resgatar(id, dto.doadorId()));
    }

    @PatchMapping("/resgates/{resgateId}/confirmar-retirada")
    public ResponseEntity<ResgateResponseDTO> confirmarRetirada(@PathVariable UUID resgateId) {
        return ResponseEntity.ok(recompensaService.confirmarRetirada(resgateId));
    }
}
