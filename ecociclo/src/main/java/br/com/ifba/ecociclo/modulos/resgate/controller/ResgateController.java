package br.com.ifba.ecociclo.modulos.resgate.controller;

import br.com.ifba.ecociclo.modulos.resgate.dto.response.ResgateResponseDTO;
import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import br.com.ifba.ecociclo.modulos.resgate.service.ResgateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resgates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResgateController {

    private final ResgateService resgateService;

    @GetMapping
    public ResponseEntity<List<ResgateResponseDTO>> listar(
            @RequestParam(name = "doadorId", required = false) UUID doadorId,
            @RequestParam(name = "status", required = false) StatusResgate status) {

        if (doadorId != null && status != null) {
            return ResponseEntity.ok(resgateService.listarPorDoadorEStatus(doadorId, status));
        }
        if (doadorId != null) {
            return ResponseEntity.ok(resgateService.listarPorDoador(doadorId));
        }
        if (status != null) {
            return ResponseEntity.ok(resgateService.listarPorStatus(status));
        }
        return ResponseEntity.ok(resgateService.listarTodos());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ResgateResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(resgateService.listarPendentes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResgateResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(resgateService.buscarPorId(id));
    }
}
