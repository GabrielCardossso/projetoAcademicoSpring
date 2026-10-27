// Contratos das respostas do backend (espelham as Entities e Enums do Java)

export const PRIORIDADES = ['BAIXA', 'MEDIA', 'ALTA'] as const;
export const SITUACOES_ATIVIDADE = ['PENDENTE', 'EM_ANDAMENTO', 'CONCLUIDA'] as const;
export const STATUS_PROJETO = ['EM_ANDAMENTO', 'CONCLUIDO'] as const;

export type Prioridade = (typeof PRIORIDADES)[number];
export type SituacaoAtividade = (typeof SITUACOES_ATIVIDADE)[number];
export type StatusProjeto = (typeof STATUS_PROJETO)[number];

// Perfis do Spring Security (ROLE_GESTOR / ROLE_DESENVOLVEDOR)
export type Perfil = 'GESTOR' | 'DESENVOLVEDOR';

// Usuario (abstrata): GestorEquipe e Desenvolvedor herdam estes campos. A senha nunca vem no JSON.
export interface Usuario {
  id: number;
  nome: string;
  cpf: string;
  email: string;
}

export type GestorEquipe = Usuario;

export interface Equipe {
  id: number;
  nome: string;
  gestor: GestorEquipe | null;
}

export interface Projeto {
  id: number;
  nome: string;
  descricao: string;
  dataInicio: string; // yyyy-MM-dd
  dataFim: string;
  status: StatusProjeto;
  ativo: boolean;
  equipe: Equipe | null;
}

export interface Desenvolvedor extends Usuario {
  equipe: Equipe | null;
  projetoAtribuido: Projeto | null;
}

export interface Atividade {
  id: number;
  nome: string;
  descricao: string;
  situacaoAtual: SituacaoAtividade;
  prioridade: Prioridade;
  dataInicio: string;
  dataFim: string;
  ativo: boolean;
  projeto: Projeto;
  desenvolvedor: Desenvolvedor | null;
}

// Qualquer registro devolvido pelos endpoints de listagem
export type Registro = Projeto | Atividade | Equipe | GestorEquipe | Desenvolvedor;

// GET /auth/me
export interface UsuarioLogado {
  id: number;
  nome: string;
  email: string;
  perfil: Perfil;
}
