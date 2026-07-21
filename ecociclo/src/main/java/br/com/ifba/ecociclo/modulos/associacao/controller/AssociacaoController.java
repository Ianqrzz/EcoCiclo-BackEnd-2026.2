package br.com.ifba.ecociclo.modulos.associacao.controller;

import br.com.ifba.ecociclo.modulos.associacao.dto.request.AssociacaoRequestDTO;
import br.com.ifba.ecociclo.modulos.associacao.dto.response.AssociacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.associacao.service.AssociacaoService;
import br.com.ifba.ecociclo.modulos.usuario.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/associacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssociacaoController {

    private final AssociacaoService associacaoService;

    @PostMapping
    public ResponseEntity<AssociacaoResponseDTO> criar(@RequestBody AssociacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(associacaoService.salvar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AssociacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(associacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociacaoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(associacaoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociacaoResponseDTO> atualizar(@PathVariable UUID id,
                                                           @RequestBody AssociacaoRequestDTO dto) {
        return ResponseEntity.ok(associacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        associacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/coletores")
    public ResponseEntity<List<UsuarioResponseDTO>> listarColetores() {
        return ResponseEntity.ok(associacaoService.listarColetores());
    }

    @PutMapping("/{associacaoId}/coletores/{coletorId}")
    public ResponseEntity<UsuarioResponseDTO> atribuirColetor(@PathVariable UUID associacaoId,
                                                              @PathVariable UUID coletorId) {
        return ResponseEntity.ok(associacaoService.atribuirColetor(associacaoId, coletorId));
    }

    @GetMapping("/{associacaoId}/membros")
    public ResponseEntity<List<UsuarioResponseDTO>> listarMembros(@PathVariable UUID associacaoId) {
        return ResponseEntity.ok(associacaoService.listarMembros(associacaoId));
    }
}
