import axios from 'axios';
import { API_URL, http } from './http';
import type { Registro } from '../types/api';
import type { Entidade, Operacao, ResultadoRequisicao } from '../types/endpoints';

export type Valores = Record<string, string>;

const TEXTO_STATUS: Record<number, string> = {
  200: 'OK',
  201: 'Created',
  204: 'No Content',
  400: 'Bad Request',
  401: 'Unauthorized',
  403: 'Forbidden',
  404: 'Not Found',
  405: 'Method Not Allowed',
  500: 'Internal Server Error',
};

export function textoStatus(status: number): string {
  return `${status} ${TEXTO_STATUS[status] ?? ''}`.trim();
}

function mensagemDeErro(status: number): string {
  if (status === 400) return 'Requisição inválida: parâmetro ausente ou em formato incorreto.';
  if (status === 401) return 'Não autenticado: a sessão não existe ou expirou. Faça login novamente.';
  if (status === 403) return 'Acesso negado: o perfil do usuário não tem permissão para esta operação.';
  if (status === 404) return 'Não encontrado: o endpoint ou o recurso não existe.';
  if (status === 405) return 'Método HTTP não suportado por este endpoint.';
  if (status >= 500) return 'Erro interno no servidor.';
  return `O backend respondeu com status ${status}.`;
}

function mensagemDeSucesso(dados: unknown): string {
  // POST/PUT/consultas de detalhe devolvem um texto (inclusive quando a regra de negócio recusa a operação)
  if (typeof dados === 'string') return dados;
  if (Array.isArray(dados)) {
    return dados.length === 0 ? 'Nenhum registro encontrado.' : `${dados.length} registro(s) retornado(s).`;
  }
  return 'Resposta recebida.';
}

// Monta o caminho real trocando {variavel} pelos valores dos campos "path"
export function montarCaminho(operacao: Operacao, valores: Valores): string {
  return operacao.campos
    .filter((c) => c.local === 'path')
    .reduce((caminho, c) => caminho.replace(`{${c.nome}}`, encodeURIComponent(valores[c.nome] ?? '')), operacao.caminho);
}

// Executa qualquer operação do mapa de endpoints e devolve um resultado pronto para exibir
export async function executarOperacao(operacao: Operacao, valores: Valores): Promise<ResultadoRequisicao> {
  const caminho = montarCaminho(operacao, valores);

  // @RequestParam: query string no GET, corpo form-urlencoded no POST/PUT
  const params = new URLSearchParams();
  for (const campo of operacao.campos) {
    const valor = valores[campo.nome]?.trim();
    if (campo.local === 'param' && valor) {
      params.append(campo.nome, valor);
    }
  }
  const temCorpo = operacao.metodo === 'POST' || operacao.metodo === 'PUT';
  const temParams = params.toString() !== '';
  const query = !temCorpo && temParams ? `?${params.toString()}` : '';

  // A senha não é exibida no painel de resultado
  const corpoExibido = new URLSearchParams(params);
  if (corpoExibido.has('senha')) corpoExibido.set('senha', '******');

  const base = {
    metodo: operacao.metodo,
    caminho: caminho + query,
    corpoEnviado: temCorpo && temParams ? corpoExibido.toString() : undefined,
  };
  const inicio = performance.now();

  try {
    const resposta = await http.request({
      method: operacao.metodo,
      url: caminho + query,
      data: temCorpo ? params : undefined,
      // Toda resposta HTTP é tratada aqui (inclusive 4xx/5xx); só falha de rede cai no catch
      validateStatus: () => true,
    });
    const sucessoHttp = resposta.status >= 200 && resposta.status < 300;

    return {
      ...base,
      status: resposta.status,
      sucessoHttp,
      mensagem: sucessoHttp ? mensagemDeSucesso(resposta.data) : mensagemDeErro(resposta.status),
      dados: resposta.data === '' ? undefined : resposta.data,
      duracaoMs: Math.round(performance.now() - inicio),
    };
  } catch (erro) {
    const detalhe = axios.isAxiosError(erro) ? ` (${erro.message})` : '';
    return {
      ...base,
      sucessoHttp: false,
      mensagem: `Sem resposta do backend em ${API_URL}${detalhe}. Verifique se o Spring Boot está em execução e se o CORS libera esta origem.`,
      duracaoMs: Math.round(performance.now() - inicio),
    };
  }
}

// Registros atuais de uma entidade (tabelas e sugestões de ID nos formulários)
export async function listarRegistros(entidade: Entidade): Promise<Registro[]> {
  const resposta = await http.get<Registro[]>(entidade.caminhoListagem);
  return resposta.data;
}

export function descreverErro(erro: unknown): string {
  if (axios.isAxiosError(erro)) {
    if (!erro.response) return `Sem resposta do backend em ${API_URL}.`;
    return `${textoStatus(erro.response.status)}: ${mensagemDeErro(erro.response.status)}`;
  }
  return erro instanceof Error ? erro.message : String(erro);
}
