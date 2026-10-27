import { useCallback, useEffect, useState } from 'react';
import { descreverErro, listarRegistros } from '../services/apiService';
import { buscarEntidade } from '../services/endpoints';
import type { Registro } from '../types/api';
import type { ChaveEntidade } from '../types/endpoints';

type PorEntidade<T> = Partial<Record<ChaveEntidade, T>>;

// Carrega a listagem (GET .../listar) de cada entidade pedida, uma única vez por entidade
export function useListagens(chaves: ChaveEntidade[]) {
  const assinatura = [...new Set(chaves)].sort().join(',');

  const [listas, setListas] = useState<PorEntidade<Registro[]>>({});
  const [erros, setErros] = useState<PorEntidade<string>>({});
  const [carregando, setCarregando] = useState(false);

  const recarregar = useCallback(async () => {
    const entidades = assinatura ? (assinatura.split(',') as ChaveEntidade[]) : [];
    if (entidades.length === 0) return;

    setCarregando(true);
    const novasListas: PorEntidade<Registro[]> = {};
    const novosErros: PorEntidade<string> = {};

    await Promise.all(
      entidades.map(async (chave) => {
        const entidade = buscarEntidade(chave);
        if (!entidade) return;
        try {
          novasListas[chave] = await listarRegistros(entidade);
        } catch (erro) {
          novosErros[chave] = descreverErro(erro);
        }
      }),
    );

    setListas(novasListas);
    setErros(novosErros);
    setCarregando(false);
  }, [assinatura]);

  useEffect(() => {
    recarregar();
  }, [recarregar]);

  return { listas, erros, carregando, recarregar };
}
