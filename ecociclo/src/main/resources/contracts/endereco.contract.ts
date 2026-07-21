export interface EnderecoRequest {
  logradouro: string;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
  usuarioId?: string;
}

export interface EnderecoResponse {
  id: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  estado: string;
  cep: string;
  usuarioId?: string;
}

export interface EnderecoEndpoints {
  listar: string;
  buscarPorId: (id: string) => string;
  criar: string;
  criarParaUsuario: (usuarioId: string) => string;
  atualizar: (id: string) => string;
  deletar: (id: string) => string;
}

export const enderecoEndpoints: EnderecoEndpoints = {
  listar: "/api/enderecos",
  buscarPorId: (id) => `/api/enderecos/${id}`,
  criar: "/api/enderecos",
  criarParaUsuario: (usuarioId) => `/api/usuarios/${usuarioId}/enderecos`,
  atualizar: (id) => `/api/enderecos/${id}`,
  deletar: (id) => `/api/enderecos/${id}`,
};
