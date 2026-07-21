package br.com.ifba.ecociclo.modulos.recompensa.service;

import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.doador.repository.DoadorRepository;
import br.com.ifba.ecociclo.modulos.recompensa.dto.request.RecompensaRequestDTO;
import br.com.ifba.ecociclo.modulos.recompensa.dto.response.RecompensaResponseDTO;
import br.com.ifba.ecociclo.modulos.recompensa.model.Recompensa;
import br.com.ifba.ecociclo.modulos.recompensa.repository.RecompensaRepository;
import br.com.ifba.ecociclo.modulos.resgate.dto.response.ResgateResponseDTO;
import br.com.ifba.ecociclo.modulos.resgate.enums.StatusResgate;
import br.com.ifba.ecociclo.modulos.resgate.model.Resgate;
import br.com.ifba.ecociclo.modulos.resgate.repository.ResgateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecompensaService {

    private final RecompensaRepository recompensaRepository;
    private final DoadorRepository doadorRepository;
    private final ResgateRepository resgateRepository;

    @Transactional
    public RecompensaResponseDTO salvar(RecompensaRequestDTO dto) {
        validarCamposObrigatorios(dto.nome(), dto.quantidade(), dto.custoPontos());

        Recompensa recompensa = Recompensa.builder()
                .nome(dto.nome().trim())
                .quantidade(dto.quantidade())
                .imagem(dto.imagem())
                .descricao(dto.descricao())
                .custoPontos(dto.custoPontos())
                .bloqueado(0)
                .disponivel(dto.quantidade() > 0)
                .build();

        return mapToResponse(recompensaRepository.save(recompensa));
    }

    @Transactional(readOnly = true)
    public List<RecompensaResponseDTO> listarTodas() {
        return recompensaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecompensaResponseDTO buscarPorId(UUID id) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada com o ID: " + id));
        return mapToResponse(recompensa);
    }

    @Transactional
    public RecompensaResponseDTO atualizar(UUID id, RecompensaRequestDTO dto) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada com o ID: " + id));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            recompensa.setNome(dto.nome().trim());
        }
        if (dto.quantidade() != null) {
            if (dto.quantidade() < recompensa.getBloqueado()) {
                throw new RuntimeException("A quantidade não pode ser menor que o número de recompensas bloqueadas.");
            }
            recompensa.setQuantidade(dto.quantidade());
        }
        if (dto.imagem() != null) {
            recompensa.setImagem(dto.imagem());
        }
        if (dto.descricao() != null) {
            recompensa.setDescricao(dto.descricao());
        }
        if (dto.custoPontos() != null) {
            if (dto.custoPontos() <= 0) {
                throw new RuntimeException("O custo em pontos deve ser maior que zero.");
            }
            recompensa.setCustoPontos(dto.custoPontos());
        }

        normalizarDisponibilidade(recompensa);
        return mapToResponse(recompensaRepository.save(recompensa));
    }

    @Transactional
    public void deletar(UUID id) {
        Recompensa recompensa = recompensaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada com o ID: " + id));

        if (resgateRepository.existsByRecompensaId(id)) {
            throw new RuntimeException("Não é possível excluir uma recompensa que já possui resgates vinculados.");
        }
        if (recompensa.getBloqueado() > 0) {
            throw new RuntimeException("Não é possível excluir uma recompensa com unidades bloqueadas.");
        }

        recompensaRepository.delete(recompensa);
    }

    @Transactional
    public ResgateResponseDTO resgatar(UUID recompensaId, UUID doadorId) {
        Recompensa recompensa = recompensaRepository.findById(recompensaId)
                .orElseThrow(() -> new RuntimeException("Recompensa não encontrada com o ID: " + recompensaId));
        Doador doador = doadorRepository.findById(doadorId)
                .orElseThrow(() -> new RuntimeException("Doador não encontrado com o ID: " + doadorId));

        int quantidadeDisponivel = recompensa.getQuantidade() - recompensa.getBloqueado();
        if (quantidadeDisponivel <= 0) {
            throw new RuntimeException("Não há unidades disponíveis desta recompensa.");
        }
        if (doador.getPontuacao() < recompensa.getCustoPontos()) {
            throw new RuntimeException("O doador não possui pontos suficientes para resgatar esta recompensa.");
        }

        doador.setPontuacao(doador.getPontuacao() - recompensa.getCustoPontos());
        recompensa.setBloqueado(recompensa.getBloqueado() + 1);
        normalizarDisponibilidade(recompensa);

        Resgate resgate = Resgate.builder()
                .data(LocalDateTime.now())
                .pontosGastos(recompensa.getCustoPontos())
                .status(StatusResgate.PENDENTE)
                .doador(doador)
                .recompensa(recompensa)
                .build();

        doadorRepository.save(doador);
        recompensaRepository.save(recompensa);
        Resgate resgateSalvo = resgateRepository.save(resgate);

        return mapToResponse(resgateSalvo);
    }

    @Transactional
    public ResgateResponseDTO confirmarRetirada(UUID resgateId) {
        Resgate resgate = resgateRepository.findById(resgateId)
                .orElseThrow(() -> new RuntimeException("Resgate não encontrado com o ID: " + resgateId));

        if (resgate.getStatus() != StatusResgate.PENDENTE) {
            throw new RuntimeException("Só é possível confirmar retiradas de resgates pendentes.");
        }

        Recompensa recompensa = resgate.getRecompensa();
        if (recompensa.getBloqueado() <= 0) {
            throw new RuntimeException("A recompensa está em um estado inconsistente: não há unidades bloqueadas.");
        }
        if (recompensa.getQuantidade() <= 0) {
            throw new RuntimeException("A recompensa está sem estoque para concluir a retirada.");
        }

        recompensa.setBloqueado(recompensa.getBloqueado() - 1);
        recompensa.setQuantidade(recompensa.getQuantidade() - 1);
        normalizarDisponibilidade(recompensa);

        resgate.setStatus(StatusResgate.CONCLUIDO);

        recompensaRepository.save(recompensa);
        Resgate resgateAtualizado = resgateRepository.save(resgate);

        return mapToResponse(resgateAtualizado);
    }

    private void validarCamposObrigatorios(String nome, Integer quantidade, Double custoPontos) {
        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("O nome da recompensa é obrigatório.");
        }
        if (quantidade == null || quantidade < 0) {
            throw new RuntimeException("A quantidade da recompensa deve ser maior ou igual a zero.");
        }
        if (custoPontos == null || custoPontos <= 0) {
            throw new RuntimeException("O custo em pontos deve ser maior que zero.");
        }
    }

    private void normalizarDisponibilidade(Recompensa recompensa) {
        recompensa.setDisponivel(recompensa.getQuantidade() > recompensa.getBloqueado());
    }

    private RecompensaResponseDTO mapToResponse(Recompensa recompensa) {
        return RecompensaResponseDTO.builder()
                .id(recompensa.getId())
                .nome(recompensa.getNome())
                .quantidade(recompensa.getQuantidade())
                .bloqueado(recompensa.getBloqueado())
                .quantidadeDisponivel(Math.max(recompensa.getQuantidade() - recompensa.getBloqueado(), 0))
                .imagem(recompensa.getImagem())
                .descricao(recompensa.getDescricao())
                .custoPontos(recompensa.getCustoPontos())
                .disponivel(recompensa.isDisponivel())
                .build();
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
