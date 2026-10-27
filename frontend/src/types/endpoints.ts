import type { Perfil } from './api';

export type MetodoHttp = 'GET' | 'POST' | 'PUT' | 'DELETE';

export type ChaveEntidade = 'projeto' | 'atividade' | 'equipe' | 'gestor' | 'dev';

export interface Campo {
  // Nome do parâmetro no Controller (@PathVariable ou @RequestParam)
  nome: string;
  rotulo: string;
  // path: vai no caminho ({id}); param: query string no GET, corpo form-urlencoded no POST/PUT
  local: 'path' | 'param';
  tipo: 'texto' | 'data' | 'senha' | 'opcoes' | 'registro';
  obrigatorio: boolean;
  // tipo 'opcoes': valores do enum
  opcoes?: readonly string[];
  // tipo 'registro': ID de uma entidade (sugere os registros existentes)
  entidade?: ChaveEntidade;
  // Atributo do registro usado para preencher este campo, quando o nome for diferente
  atributoRegistro?: string;
  // Valor inicial = ID do usuário logado (ex.: o desenvolvedor assumindo a atividade)
  padraoUsuarioLogado?: boolean;
}

export interface Operacao {
  // Usado na URL do frontend: /:entidade/:id
  id: string;
  entidade: ChaveEntidade;
  metodo: MetodoHttp;
  // Caminho real do endpoint, com {variaveis}
  caminho: string;
  titulo: string;
  descricao: string;
  campos: Campo[];
  // Perfis liberados no SecurityConfig do backend
  perfis: Perfil[];
  // Mensagem de confirmação antes de executar
  confirmar?: string;
  // Campo de ID que, ao ser escolhido, preenche os demais com os dados atuais do registro
  preencherPor?: string;
}

export interface Coluna {
  titulo: string;
  // Caminho do atributo no JSON, ex.: "equipe.nome"
  atributo: string;
}

export interface Entidade {
  chave: ChaveEntidade;
  nome: string;
  // Endpoint de listagem usado para tabelas e sugestões de ID
  caminhoListagem: string;
  // Atributo mostrado junto do ID nas sugestões
  atributoNome: string;
  colunas: Coluna[];
}

// Resultado de uma requisição, exibido no painel de resposta
export interface ResultadoRequisicao {
  metodo: MetodoHttp;
  caminho: string;
  corpoEnviado?: string;
  status?: number;
  sucessoHttp: boolean;
  mensagem: string;
  dados?: unknown;
  duracaoMs: number;
}
