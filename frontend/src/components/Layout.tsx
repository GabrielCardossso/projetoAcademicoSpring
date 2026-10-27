import { useState } from 'react';
import { Outlet, useOutletContext } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export interface ContextoLayout {
  // Exibe também as operações bloqueadas para o perfil (para demonstrar o 403 do backend)
  mostrarSemPermissao: boolean;
}

export function Layout() {
  const [mostrarSemPermissao, setMostrarSemPermissao] = useState(false);

  return (
    <div className="layout">
      <Sidebar mostrarSemPermissao={mostrarSemPermissao} onMostrarSemPermissao={setMostrarSemPermissao} />
      <main className="conteudo">
        <Outlet context={{ mostrarSemPermissao } satisfies ContextoLayout} />
      </main>
    </div>
  );
}

export function useContextoLayout(): ContextoLayout {
  return useOutletContext<ContextoLayout>();
}
