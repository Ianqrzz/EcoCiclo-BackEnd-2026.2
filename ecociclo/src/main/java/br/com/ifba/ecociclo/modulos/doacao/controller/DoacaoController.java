package br.com.ifba.ecociclo.modulos.doacao.controller;

import br.com.ifba.ecociclo.modulos.doacao.dto.request.DoacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.doacao.dto.response.DoacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.doacao.service.DoacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doacoes")
@RequiredArgsConstructor
public class DoacaoController {

    private final DoacaoService doacaoService;

    @PostMapping
    public ResponseEntity<DoacaoResponseDTO> cadastrar(@RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doacaoService.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<DoacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(doacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(doacaoService.buscarPorId(id));
    }
}
