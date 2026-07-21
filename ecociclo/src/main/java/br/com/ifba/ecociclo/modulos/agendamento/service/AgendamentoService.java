package br.com.ifba.ecociclo.modulos.agendamento.service;

import br.com.ifba.ecociclo.modulos.agendamento.dto.request.AgendamentoRequestDTO;
import br.com.ifba.ecociclo.modulos.agendamento.dto.response.AgendamentoResponseDTO;
import br.com.ifba.ecociclo.modulos.agendamento.enums.StatusAgendamento;
import br.com.ifba.ecociclo.modulos.agendamento.model.Agendamento;
import br.com.ifba.ecociclo.modulos.agendamento.repository.AgendamentoRepository;
import br.com.ifba.ecociclo.modulos.coletor.repository.ColetorRepository;
import br.com.ifba.ecociclo.modulos.coletor.model.Coletor;
import br.com.ifba.ecociclo.modulos.doacao.dto.response.DoacaoResponseDTO;
import br.com.ifba.ecociclo.modulos.doador.model.Doador;
import br.com.ifba.ecociclo.modulos.doador.repository.DoadorRepository;
import br.com.ifba.ecociclo.modulos.doacao.model.Doacao;
import br.com.ifba.ecociclo.modulos.doacao.repository.DoacaoRepository;
import br.com.ifba.ecociclo.modulos.endereco.model.Endereco;
import br.com.ifba.ecociclo.modulos.endereco.repository.EnderecoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final DoadorRepository doadorRepository;
    private final EnderecoRepository enderecoRepository;
    private final DoacaoRepository doacaoRepository;
    private final ColetorRepository coletorRepository;

    @Transactional
    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO dto) {
        // Busca Doador e Endereco
        Doador doador = doadorRepository.findById(dto.doadorId())
                .orElseThrow(() -> new RuntimeException("Doador não encontrado"));
        Endereco endereco = enderecoRepository.findById(dto.enderecoId())
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        // Cria a Doação embarcada
        Doacao doacao = Doacao.builder()
                .nome(dto.doacao().nome())
                .quantidade(dto.doacao().quantidade())
                .imagem(dto.doacao().imagem())
                .peso(dto.doacao().peso())
                .build();
        doacaoRepository.save(doacao);
        // Monta Agendamento
        Agendamento agendamento = Agendamento.builder()
                .doador(doador)
                .endereco(endereco)
                .doacao(doacao)
                .dataColeta(dto.dataColeta())
                .dataCriacao(LocalDateTime.now())
                .observacoes(dto.observacoes())
                .status(StatusAgendamento.PENDENTE)
                .pontosGerados(0.0)
                .build();
        agendamentoRepository.save(agendamento);
        return mapToResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponseDTO aceitarColeta(UUID agendamentoId, UUID coletorId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            throw new RuntimeException("Só é possível aceitar agendamentos pendentes");
        }
        Coletor coletor = coletorRepository.findById(coletorId)
                .orElseThrow(() -> new RuntimeException("Coletor não encontrado"));
        agendamento.setColetor(coletor);
        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        agendamentoRepository.save(agendamento);
        return mapToResponse(agendamento);
    }

    @Transactional
    public AgendamentoResponseDTO concluirColeta(UUID agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        if (agendamento.getStatus() != StatusAgendamento.CONFIRMADO) {
            throw new RuntimeException("Agendamento deve estar CONFIRMADO para ser concluído");
        }
        // Calcula pontos
        double pontos = agendamento.getDoacao().getPeso() * 10.0;
        agendamento.setPontosGerados(pontos);
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        // Atualiza pontuação do Doador
        Doador doador = agendamento.getDoador();
        doador.setPontuacao(doador.getPontuacao() + pontos);
        // Persiste mudanças
        doadorRepository.save(doador);
        agendamentoRepository.save(agendamento);
        return mapToResponse(agendamento);
    }

    @Transactional
    public void cancelarAgendamento(UUID agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    @Transactional
    public List<AgendamentoResponseDTO> listarPendentes() {
        return listarAgendamentos(StatusAgendamento.PENDENTE, null);
    }

    @Transactional
    public List<AgendamentoResponseDTO> listarPorColetor(UUID coletorId) {
        if (coletorId == null) {
            throw new RuntimeException("Coletor não informado");
        }
        return agendamentoRepository.findByColetor_Id(coletorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AgendamentoResponseDTO> listarAgendamentos(StatusAgendamento status, UUID coletorId) {
        List<Agendamento> agendamentos;

        if (status != null && coletorId != null) {
            agendamentos = agendamentoRepository.findByStatusAndColetor_Id(status, coletorId);
        } else if (status != null) {
            agendamentos = agendamentoRepository.findByStatus(status);
        } else if (coletorId != null) {
            agendamentos = agendamentoRepository.findByColetor_Id(coletorId);
        } else {
            agendamentos = agendamentoRepository.findAll();
        }

        return agendamentos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AgendamentoResponseDTO mapToResponse(Agendamento agendamento) {
        Doacao doacao = agendamento.getDoacao();
        var doacaoDto = DoacaoResponseDTO.builder()
                .id(doacao.getId())
                .nome(doacao.getNome())
                .quantidade(doacao.getQuantidade())
                .imagem(doacao.getImagem())
                .peso(doacao.getPeso())
                .build();
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getDataColeta(),
                agendamento.getDataCriacao(),
                agendamento.getObservacoes(),
                agendamento.getPontosGerados(),
                agendamento.getStatus(),
                agendamento.getDoador().getId(),
                agendamento.getColetor() != null ? agendamento.getColetor().getId() : null,
                agendamento.getEndereco().getId(),
                doacaoDto
        );
    }
}
