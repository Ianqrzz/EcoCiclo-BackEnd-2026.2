export interface RecompensaRequest {
  nome: string;
  quantidade: number;
  imagem?: string;
  descricao?: string;
  custoPontos: number;
}

export interface RecompensaResponse {
  id: string;
  nome: string;
  quantidade: number;
  bloqueado: number;
  quantidadeDisponivel: number;
  imagem?: string;
  descricao?: string;
  custoPontos: number;
  disponivel: boolean;
}

export interface ResgatarRecompensaRequest {
  doadorId: string;
}

export interface RecompensaEndpoints {
  listar: string;
  buscarPorId: (id: string) => string;
  criar: string;
  atualizar: (id: string) => string;
  deletar: (id: string) => string;
  resgatar: (id: string) => string;
  confirmarRetirada: (resgateId: string) => string;
}

export const recompensaEndpoints: RecompensaEndpoints = {
  listar: "/api/recompensas",
  buscarPorId: (id) => `/api/recompensas/${id}`,
  criar: "/api/recompensas",
  atualizar: (id) => `/api/recompensas/${id}`,
  deletar: (id) => `/api/recompensas/${id}`,
  resgatar: (id) => `/api/recompensas/${id}/resgatar`,
  confirmarRetirada: (resgateId) => `/api/recompensas/resgates/${resgateId}/confirmar-retirada`,
};
