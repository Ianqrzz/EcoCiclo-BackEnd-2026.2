package br.com.ifba.ecociclo.modulos.endereco.controller;

import br.com.ifba.ecociclo.modulos.endereco.dto.request.EnderecoRequestDTO;
import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import br.com.ifba.ecociclo.modulos.endereco.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping
    public ResponseEntity<EnderecoResponseDTO> criar(@RequestBody EnderecoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enderecoService.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(enderecoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(enderecoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponseDTO> atualizar(@PathVariable UUID id, @RequestBody EnderecoRequestDTO dto) {
        return ResponseEntity.ok(enderecoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
