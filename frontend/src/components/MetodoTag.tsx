import type { MetodoHttp } from '../types/endpoints';

export function MetodoTag({ metodo }: { metodo: MetodoHttp }) {
  return <span className={`metodo metodo-${metodo.toLowerCase()}`}>{metodo}</span>;
}
