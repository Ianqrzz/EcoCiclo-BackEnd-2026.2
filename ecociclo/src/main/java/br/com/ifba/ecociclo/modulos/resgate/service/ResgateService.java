package br.com.ifba.ecociclo.modulos.resgate.service;

import br.com.ifba.ecociclo.modulos.resgate.dto.response.ResgateResponseDTO;
import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import br.com.ifba.ecociclo.modulos.resgate.model.Resgate;
import br.com.ifba.ecociclo.modulos.resgate.repository.ResgateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResgateService {

    private final ResgateRepository resgateRepository;

    @Transactional(readOnly = true)
    public List<ResgateResponseDTO> listarTodos() {
        return resgateRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResgateResponseDTO buscarPorId(UUID id) {
        Resgate resgate = resgateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado com o ID: " + id));
        return mapToResponse(resgate);
    }

    @Transactional(readOnly = true)
    public List<ResgateResponseDTO> listarPorDoador(UUID doadorId) {
        return resgateRepository.findByDoador_Id(doadorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResgateResponseDTO> listarPorStatus(StatusResgate status) {
        return resgateRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResgateResponseDTO> listarPorDoadorEStatus(UUID doadorId, StatusResgate status) {
        return resgateRepository.findByDoador_IdAndStatus(doadorId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResgateResponseDTO> listarPendentes() {
        return listarPorStatus(StatusResgate.PENDENTE);
    }

    private ResgateResponseDTO mapToResponse(Resgate resgate) {
        return ResgateResponseDTO.builder()
                .id(resgate.getId())
                .data(resgate.getData())
                .pontosGastos(resgate.getPontosGastos())
                .status(resgate.getStatus())
                .doadorId(resgate.getDoador().getId())
                .doadorNome(resgate.getDoador().getNome())
                .recompensaId(resgate.getRecompensa().getId())
                .recompensaNome(resgate.getRecompensa().getNome())
                .build();
    }
}
