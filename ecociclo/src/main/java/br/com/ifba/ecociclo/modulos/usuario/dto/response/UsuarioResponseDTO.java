package br.com.ifba.ecociclo.modulos.usuario.dto.response;

import br.com.ifba.ecociclo.modulos.associacao.dto.response.AssociacaoResumoDTO;
import br.com.ifba.ecociclo.modulos.usuario.enums.TipoPerfil;
import br.com.ifba.ecociclo.modulos.endereco.dto.response.EnderecoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private TipoPerfil perfil;
    private Double pontuacao;
    private EnderecoResponseDTO endereco;
    private List<EnderecoResponseDTO> enderecos;
    private AssociacaoResumoDTO associacao;
}
