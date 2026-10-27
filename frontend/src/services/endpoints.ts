// Mapa dos endpoints REST do backend, tirado dos Controllers do Spring Boot.
// "perfis" repete as regras do SecurityConfig apenas para esconder o que o usuário não pode usar;
// quem realmente bloqueia é o backend (responde 403).

import { PRIORIDADES, SITUACOES_ATIVIDADE, STATUS_PROJETO } from '../types/api';
import type { Perfil } from '../types/api';
import type { Campo, ChaveEntidade, Entidade, Operacao } from '../types/endpoints';

const TODOS: Perfil[] = ['GESTOR', 'DESENVOLVEDOR'];
const GESTOR: Perfil[] = ['GESTOR'];

// ---------- campos reutilizados ----------

const idDe = (entidade: ChaveEntidade, nome = 'id', rotulo = 'ID'): Campo => ({
  nome, rotulo, local: 'path', tipo: 'registro', entidade, obrigatorio: true,
});
const paramIdDe = (entidade: ChaveEntidade, nome: string, rotulo: string, obrigatorio = true): Campo => ({
  nome, rotulo, local: 'param', tipo: 'registro', entidade, obrigatorio,
});
const texto = (nome: string, rotulo: string): Campo => ({
  nome, rotulo, local: 'param', tipo: 'texto', obrigatorio: true,
});
const data = (nome: string, rotulo: string): Campo => ({
  nome, rotulo, local: 'param', tipo: 'data', obrigatorio: true,
});

const camposUsuario: Campo[] = [
  texto('nome', 'Nome'),
  texto('cpf', 'CPF'),
  texto('email', 'E-mail (login)'),
  { nome: 'senha', rotulo: 'Senha', local: 'param', tipo: 'senha', obrigatorio: true },
];

// ---------- entidades ----------

export const ENTIDADES: Entidade[] = [
  {
    chave: 'projeto',
    nome: 'Projeto',
    caminhoListagem: '/projeto/listar',
    atributoNome: 'nome',
    colunas: [
      { titulo: 'ID', atributo: 'id' },
      { titulo: 'Nome', atributo: 'nome' },
      { titulo: 'Descrição', atributo: 'descricao' },
      { titulo: 'Início', atributo: 'dataInicio' },
      { titulo: 'Fim', atributo: 'dataFim' },
      { titulo: 'Status', atributo: 'status' },
      { titulo: 'Ativo', atributo: 'ativo' },
      { titulo: 'Equipe', atributo: 'equipe.nome' },
    ],
  },
  {
    chave: 'atividade',
    nome: 'Atividade',
    caminhoListagem: '/atividade/listar',
    atributoNome: 'nome',
    colunas: [
      { titulo: 'ID', atributo: 'id' },
      { titulo: 'Nome', atributo: 'nome' },
      { titulo: 'Descrição', atributo: 'descricao' },
      { titulo: 'Situação', atributo: 'situacaoAtual' },
      { titulo: 'Prioridade', atributo: 'prioridade' },
      { titulo: 'Início', atributo: 'dataInicio' },
      { titulo: 'Fim', atributo: 'dataFim' },
      { titulo: 'Projeto', atributo: 'projeto.nome' },
      { titulo: 'Responsável', atributo: 'desenvolvedor.nome' },
      { titulo: 'Ativa', atributo: 'ativo' },
    ],
  },
  {
    chave: 'equipe',
    nome: 'Equipe',
    caminhoListagem: '/equipe/listar',
    atributoNome: 'nome',
    colunas: [
      { titulo: 'ID', atributo: 'id' },
      { titulo: 'Nome', atributo: 'nome' },
      { titulo: 'Gestor', atributo: 'gestor.nome' },
    ],
  },
  {
    chave: 'gestor',
    nome: 'Gestor de Equipe',
    caminhoListagem: '/gestor/listar',
    atributoNome: 'nome',
    colunas: [
      { titulo: 'ID', atributo: 'id' },
      { titulo: 'Nome', atributo: 'nome' },
      { titulo: 'CPF', atributo: 'cpf' },
      { titulo: 'E-mail', atributo: 'email' },
    ],
  },
  {
    chave: 'dev',
    nome: 'Desenvolvedor',
    caminhoListagem: '/dev/listar',
    atributoNome: 'nome',
    colunas: [
      { titulo: 'ID', atributo: 'id' },
      { titulo: 'Nome', atributo: 'nome' },
      { titulo: 'CPF', atributo: 'cpf' },
      { titulo: 'E-mail', atributo: 'email' },
      { titulo: 'Equipe', atributo: 'equipe.nome' },
      { titulo: 'Projeto atribuído', atributo: 'projetoAtribuido.nome' },
    ],
  },
];

// ---------- operações ----------

export const OPERACOES: Operacao[] = [
  // ProjetoController
  {
    id: 'listar', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/listar',
    titulo: 'Listar projetos', descricao: 'Lista os projetos ativos.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'mostrar', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/mostrar/{id}',
    titulo: 'Consultar projeto', descricao: 'Mostra os detalhes de um projeto, com as atividades ativas.',
    campos: [idDe('projeto')], perfis: TODOS,
  },
  {
    id: 'listarPorStatus', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/listarPorStatus/{status}',
    titulo: 'Listar por status', descricao: 'Projetos ativos com o status informado.',
    campos: [{ nome: 'status', rotulo: 'Status', local: 'path', tipo: 'opcoes', opcoes: STATUS_PROJETO, obrigatorio: true }],
    perfis: TODOS,
  },
  {
    id: 'buscarPorNome', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/buscarPorNome',
    titulo: 'Buscar por nome', descricao: 'Projetos ativos cujo nome contém o texto (sem diferenciar maiúsculas).',
    campos: [texto('nome', 'Nome contém')], perfis: TODOS,
  },
  {
    id: 'listarPorEquipe', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/listarPorEquipe/{equipeId}',
    titulo: 'Listar por equipe', descricao: 'Projetos ativos de uma equipe.',
    campos: [idDe('equipe', 'equipeId', 'ID da equipe')], perfis: TODOS,
  },
  {
    id: 'comPendencias', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/comPendencias',
    titulo: 'Projetos com pendências', descricao: 'Projetos ativos que ainda têm atividades não concluídas.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'progresso', entidade: 'projeto', metodo: 'GET', caminho: '/projeto/progresso/{id}',
    titulo: 'Progresso do projeto', descricao: 'Atividades concluídas em relação ao total.',
    campos: [idDe('projeto')], perfis: TODOS,
  },
  {
    id: 'salvar', entidade: 'projeto', metodo: 'POST', caminho: '/projeto/salvar',
    titulo: 'Cadastrar projeto', descricao: 'Cria um projeto. A equipe é opcional e a data de fim não pode estar no passado.',
    campos: [
      texto('nome', 'Nome'),
      texto('descricao', 'Descrição'),
      data('dataInicio', 'Data de início'),
      data('dataFim', 'Data de fim'),
      paramIdDe('equipe', 'equipeId', 'ID da equipe (opcional)', false),
    ],
    perfis: GESTOR,
  },
  {
    id: 'atualizar', entidade: 'projeto', metodo: 'PUT', caminho: '/projeto/atualizar/{id}',
    titulo: 'Atualizar projeto', descricao: 'Altera nome, descrição e datas. O novo período precisa comportar as atividades.',
    campos: [
      idDe('projeto'),
      texto('nome', 'Nome'),
      texto('descricao', 'Descrição'),
      data('dataInicio', 'Data de início'),
      data('dataFim', 'Data de fim'),
    ],
    perfis: GESTOR, preencherPor: 'id',
  },
  {
    id: 'atualizarStatus', entidade: 'projeto', metodo: 'PUT', caminho: '/projeto/atualizarStatus/{id}',
    titulo: 'Atualizar status', descricao: 'Só conclui o projeto se todas as atividades estiverem concluídas.',
    campos: [
      idDe('projeto'),
      { nome: 'status', rotulo: 'Status', local: 'param', tipo: 'opcoes', opcoes: STATUS_PROJETO, obrigatorio: true },
    ],
    perfis: GESTOR, preencherPor: 'id',
  },
  {
    id: 'atribuirEquipe', entidade: 'projeto', metodo: 'PUT', caminho: '/projeto/atribuirEquipe',
    titulo: 'Atribuir equipe', descricao: 'Define a equipe responsável pelo projeto.',
    campos: [paramIdDe('projeto', 'projetoId', 'ID do projeto'), paramIdDe('equipe', 'equipeId', 'ID da equipe')],
    perfis: GESTOR,
  },
  {
    id: 'setStatus', entidade: 'projeto', metodo: 'PUT', caminho: '/projeto/setStatus/{id}',
    titulo: 'Ativar / inativar', descricao: 'Exclusão lógica: alterna o projeto entre ativo e inativo. Não existe DELETE no backend. A listagem só mostra os ativos; para reativar, digite o ID.',
    campos: [idDe('projeto')], perfis: GESTOR,
    confirmar: 'Alternar este projeto entre ativo e inativo?',
  },

  // AtividadeController
  {
    id: 'listar', entidade: 'atividade', metodo: 'GET', caminho: '/atividade/listar',
    titulo: 'Listar atividades', descricao: 'Lista as atividades ativas.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'mostrar', entidade: 'atividade', metodo: 'GET', caminho: '/atividade/mostrar/{id}',
    titulo: 'Consultar atividade', descricao: 'Mostra os detalhes de uma atividade.',
    campos: [idDe('atividade')], perfis: TODOS,
  },
  {
    id: 'atrasadas', entidade: 'atividade', metodo: 'GET', caminho: '/atividade/atrasadas',
    titulo: 'Atividades atrasadas', descricao: 'Prazo vencido e não concluídas, da maior prioridade para a menor.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'listarPorDesenvolvedor', entidade: 'atividade', metodo: 'GET', caminho: '/atividade/listarPorDesenvolvedor/{devId}',
    titulo: 'Listar por desenvolvedor', descricao: 'Atividades ativas sob responsabilidade de um desenvolvedor.',
    campos: [idDe('dev', 'devId', 'ID do desenvolvedor')], perfis: TODOS,
  },
  {
    id: 'listarPorProjeto', entidade: 'atividade', metodo: 'GET', caminho: '/atividade/listarPorProjeto/{projetoId}',
    titulo: 'Listar por projeto', descricao: 'Atividades ativas de um projeto.',
    campos: [idDe('projeto', 'projetoId', 'ID do projeto')], perfis: TODOS,
  },
  {
    id: 'listarPorSituacao', entidade: 'atividade', metodo: 'GET',
    caminho: '/atividade/listarPorProjeto/{projetoId}/situacao/{situacao}',
    titulo: 'Listar por projeto e situação', descricao: 'Atividades de um projeto filtradas pela situação.',
    campos: [
      idDe('projeto', 'projetoId', 'ID do projeto'),
      { nome: 'situacao', rotulo: 'Situação', local: 'path', tipo: 'opcoes', opcoes: SITUACOES_ATIVIDADE, obrigatorio: true },
    ],
    perfis: TODOS,
  },
  {
    id: 'listarPorPrioridade', entidade: 'atividade', metodo: 'GET',
    caminho: '/atividade/listarPorProjeto/{projetoId}/prioridade/{prioridade}',
    titulo: 'Listar por projeto e prioridade', descricao: 'Atividades de um projeto filtradas pela prioridade.',
    campos: [
      idDe('projeto', 'projetoId', 'ID do projeto'),
      { nome: 'prioridade', rotulo: 'Prioridade', local: 'path', tipo: 'opcoes', opcoes: PRIORIDADES, obrigatorio: true },
    ],
    perfis: TODOS,
  },
  {
    id: 'salvar', entidade: 'atividade', metodo: 'POST', caminho: '/atividade/salvar',
    titulo: 'Cadastrar atividade', descricao: 'Cria uma atividade dentro de um projeto ativo, com datas dentro do período do projeto.',
    campos: [
      paramIdDe('projeto', 'projetoId', 'ID do projeto'),
      texto('nome', 'Nome'),
      texto('descricao', 'Descrição'),
      { nome: 'prioridade', rotulo: 'Prioridade', local: 'param', tipo: 'opcoes', opcoes: PRIORIDADES, obrigatorio: true },
      data('dataInicio', 'Data de início'),
      data('dataFim', 'Data de fim'),
    ],
    perfis: GESTOR,
  },
  {
    id: 'atualizar', entidade: 'atividade', metodo: 'PUT', caminho: '/atividade/atualizar/{id}',
    titulo: 'Atualizar atividade', descricao: 'Altera os dados de uma atividade ativa.',
    campos: [
      idDe('atividade'),
      texto('nome', 'Nome'),
      texto('descricao', 'Descrição'),
      { nome: 'prioridade', rotulo: 'Prioridade', local: 'param', tipo: 'opcoes', opcoes: PRIORIDADES, obrigatorio: true },
      data('dataInicio', 'Data de início'),
      data('dataFim', 'Data de fim'),
    ],
    perfis: GESTOR, preencherPor: 'id',
  },
  {
    id: 'atualizarStatus', entidade: 'atividade', metodo: 'PUT', caminho: '/atividade/atualizarStatus/{id}',
    titulo: 'Atualizar situação', descricao: 'Muda a situação da atividade (pendente, em andamento, concluída).',
    campos: [
      idDe('atividade'),
      {
        nome: 'situacao', rotulo: 'Situação', local: 'param', tipo: 'opcoes', opcoes: SITUACOES_ATIVIDADE,
        obrigatorio: true, atributoRegistro: 'situacaoAtual',
      },
    ],
    perfis: TODOS, preencherPor: 'id',
  },
  {
    id: 'atribuirDesenvolvedor', entidade: 'atividade', metodo: 'PUT', caminho: '/atividade/atribuirDesenvolvedor',
    titulo: 'Atribuir desenvolvedor', descricao: 'Define o responsável. O desenvolvedor precisa ser da equipe do projeto e pode assumir a atividade com o próprio ID.',
    campos: [
      paramIdDe('atividade', 'atividadeId', 'ID da atividade'),
      { ...paramIdDe('dev', 'devId', 'ID do desenvolvedor'), padraoUsuarioLogado: true },
    ],
    perfis: TODOS,
  },
  {
    id: 'setStatus', entidade: 'atividade', metodo: 'PUT', caminho: '/atividade/setStatus/{id}',
    titulo: 'Ativar / inativar', descricao: 'Exclusão lógica: alterna a atividade entre ativa e inativa. Não existe DELETE no backend. A listagem só mostra as ativas; para reativar, digite o ID.',
    campos: [idDe('atividade')], perfis: GESTOR,
    confirmar: 'Alternar esta atividade entre ativa e inativa?',
  },

  // EquipeController
  {
    id: 'listar', entidade: 'equipe', metodo: 'GET', caminho: '/equipe/listar',
    titulo: 'Listar equipes', descricao: 'Lista todas as equipes.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'mostrar', entidade: 'equipe', metodo: 'GET', caminho: '/equipe/mostrar/{id}',
    titulo: 'Consultar equipe', descricao: 'Mostra os detalhes de uma equipe.',
    campos: [idDe('equipe')], perfis: TODOS,
  },
  {
    id: 'salvar', entidade: 'equipe', metodo: 'POST', caminho: '/equipe/salvar',
    titulo: 'Cadastrar equipe', descricao: 'Cria uma equipe.',
    campos: [texto('nome', 'Nome')], perfis: GESTOR,
  },
  {
    id: 'atribuirGestor', entidade: 'equipe', metodo: 'PUT', caminho: '/equipe/atribuirGestor',
    titulo: 'Atribuir gestor', descricao: 'Define o gestor da equipe. Um gestor cuida de uma equipe só.',
    campos: [paramIdDe('equipe', 'equipeId', 'ID da equipe'), paramIdDe('gestor', 'gestorId', 'ID do gestor')],
    perfis: GESTOR,
  },

  // GestorEquipeController
  {
    id: 'listar', entidade: 'gestor', metodo: 'GET', caminho: '/gestor/listar',
    titulo: 'Listar gestores', descricao: 'Lista todos os gestores.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'mostrar', entidade: 'gestor', metodo: 'GET', caminho: '/gestor/mostrar/{id}',
    titulo: 'Consultar gestor', descricao: 'Mostra os dados de um gestor.',
    campos: [idDe('gestor')], perfis: TODOS,
  },
  {
    id: 'salvar', entidade: 'gestor', metodo: 'POST', caminho: '/gestor/salvar',
    titulo: 'Cadastrar gestor', descricao: 'Cria um gestor. O e-mail e a senha passam a valer para o login.',
    campos: camposUsuario, perfis: GESTOR,
  },

  // DesenvolvedorController
  {
    id: 'listar', entidade: 'dev', metodo: 'GET', caminho: '/dev/listar',
    titulo: 'Listar desenvolvedores', descricao: 'Lista todos os desenvolvedores.',
    campos: [], perfis: TODOS,
  },
  {
    id: 'mostrar', entidade: 'dev', metodo: 'GET', caminho: '/dev/mostrar/{id}',
    titulo: 'Consultar desenvolvedor', descricao: 'Mostra os dados de um desenvolvedor.',
    campos: [idDe('dev')], perfis: TODOS,
  },
  {
    id: 'salvar', entidade: 'dev', metodo: 'POST', caminho: '/dev/salvar',
    titulo: 'Cadastrar desenvolvedor', descricao: 'Cria um desenvolvedor. O e-mail e a senha passam a valer para o login.',
    campos: camposUsuario, perfis: GESTOR,
  },
  {
    id: 'atribuirEquipe', entidade: 'dev', metodo: 'PUT', caminho: '/dev/atribuirEquipe',
    titulo: 'Atribuir equipe', descricao: 'Coloca o desenvolvedor em uma equipe.',
    campos: [paramIdDe('dev', 'devId', 'ID do desenvolvedor'), paramIdDe('equipe', 'equipeId', 'ID da equipe')],
    perfis: GESTOR,
  },
  {
    id: 'atribuirProjeto', entidade: 'dev', metodo: 'PUT', caminho: '/dev/atribuirProjeto',
    titulo: 'Atribuir projeto', descricao: 'Atribui um projeto ativo da equipe do desenvolvedor.',
    campos: [paramIdDe('dev', 'devId', 'ID do desenvolvedor'), paramIdDe('projeto', 'projetoId', 'ID do projeto')],
    perfis: GESTOR,
  },
];

export function buscarEntidade(chave: string): Entidade | undefined {
  return ENTIDADES.find((e) => e.chave === chave);
}

export function buscarOperacao(entidade: string, id: string): Operacao | undefined {
  return OPERACOES.find((o) => o.entidade === entidade && o.id === id);
}

export function operacoesDa(entidade: ChaveEntidade): Operacao[] {
  return OPERACOES.filter((o) => o.entidade === entidade);
}

export function podeExecutar(operacao: Operacao, perfil: Perfil): boolean {
  return operacao.perfis.includes(perfil);
}
