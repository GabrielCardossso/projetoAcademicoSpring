import { useState } from 'react';
import type { FormEvent } from 'react';
import { montarCaminho } from '../services/apiService';
import type { Valores } from '../services/apiService';
import { buscarEntidade } from '../services/endpoints';
import type { Registro, UsuarioLogado } from '../types/api';
import type { Campo, ChaveEntidade, Operacao } from '../types/endpoints';
import { MetodoTag } from './MetodoTag';
import { lerAtributo } from './TabelaRegistros';

interface Props {
  operacao: Operacao;
  usuario: UsuarioLogado;
  // Registros atuais de cada entidade, para sugerir IDs e preencher o formulário
  listas: Partial<Record<ChaveEntidade, Registro[]>>;
  executando: boolean;
  onExecutar: (valores: Valores) => void;
}

function valoresIniciais(operacao: Operacao, usuario: UsuarioLogado): Valores {
  const valores: Valores = {};
  for (const campo of operacao.campos) {
    const ehODesenvolvedorLogado = campo.padraoUsuarioLogado && usuario.perfil === 'DESENVOLVEDOR';
    valores[campo.nome] = ehODesenvolvedorLogado ? String(usuario.id) : '';
  }
  return valores;
}

function localDoCampo(campo: Campo, operacao: Operacao): string {
  if (campo.local === 'path') return 'caminho';
  return operacao.metodo === 'GET' ? 'query' : 'corpo';
}

export function FormularioOperacao({ operacao, usuario, listas, executando, onExecutar }: Props) {
  const [valores, setValores] = useState<Valores>(() => valoresIniciais(operacao, usuario));

  function buscarRegistro(entidade: ChaveEntidade | undefined, id: string): Registro | undefined {
    if (!entidade || !id) return undefined;
    return listas[entidade]?.find((r) => String(r.id) === id);
  }

  function alterar(campo: Campo, valor: string) {
    const novos = { ...valores, [campo.nome]: valor };

    // Ao escolher o registro a atualizar, preenche os outros campos com os dados atuais dele
    if (operacao.preencherPor === campo.nome) {
      const registro = buscarRegistro(operacao.entidade, valor);
      if (registro) {
        for (const outro of operacao.campos) {
          if (outro.nome === campo.nome) continue;
          const atual = lerAtributo(registro, outro.atributoRegistro ?? outro.nome);
          if (atual !== null && atual !== undefined && typeof atual !== 'object') {
            novos[outro.nome] = String(atual);
          }
        }
      }
    }
    setValores(novos);
  }

  function enviar(evento: FormEvent) {
    evento.preventDefault();
    if (operacao.confirmar && !window.confirm(operacao.confirmar)) {
      return;
    }
    onExecutar(valores);
  }

  function renderizarEntrada(campo: Campo) {
    const comum = {
      id: `campo-${campo.nome}`,
      value: valores[campo.nome] ?? '',
      required: campo.obrigatorio,
      onChange: (e: { target: { value: string } }) => alterar(campo, e.target.value),
    };

    if (campo.tipo === 'opcoes') {
      return (
        <select {...comum}>
          <option value="">Selecione</option>
          {campo.opcoes?.map((opcao) => (
            <option key={opcao} value={opcao}>
              {opcao}
            </option>
          ))}
        </select>
      );
    }

    if (campo.tipo === 'registro') {
      const entidade = campo.entidade ? buscarEntidade(campo.entidade) : undefined;
      const registros = campo.entidade ? listas[campo.entidade] ?? [] : [];
      const selecionado = buscarRegistro(campo.entidade, valores[campo.nome]);
      return (
        <>
          <input {...comum} type="number" min={1} list={`lista-${campo.nome}`} />
          <datalist id={`lista-${campo.nome}`}>
            {registros.map((r) => (
              <option key={r.id} value={r.id}>
                {String(lerAtributo(r, entidade?.atributoNome ?? 'nome') ?? '')}
              </option>
            ))}
          </datalist>
          {selecionado && entidade && (
            <span className="info"> {String(lerAtributo(selecionado, entidade.atributoNome) ?? '')}</span>
          )}
        </>
      );
    }

    if (campo.tipo === 'data') return <input {...comum} type="date" />;
    if (campo.tipo === 'senha') return <input {...comum} type="password" autoComplete="new-password" />;
    return <input {...comum} type="text" />;
  }

  return (
    <form className="formulario" onSubmit={enviar}>
      <p className="previa">
        <MetodoTag metodo={operacao.metodo} /> <code>{montarCaminho(operacao, valores)}</code>
      </p>

      {operacao.campos.length === 0 && <p className="info">Este endpoint não recebe parâmetros.</p>}

      {operacao.campos.map((campo) => (
        <div key={campo.nome} className="campo">
          <label htmlFor={`campo-${campo.nome}`}>
            {campo.rotulo}
            {campo.obrigatorio && ' *'}
            <span className="info">
              {' '}
              <code>{campo.nome}</code> ({localDoCampo(campo, operacao)})
            </span>
          </label>
          <div>{renderizarEntrada(campo)}</div>
        </div>
      ))}

      <button type="submit" disabled={executando}>
        {executando ? 'Enviando...' : 'Enviar requisição'}
      </button>
    </form>
  );
}
