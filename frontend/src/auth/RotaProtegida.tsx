import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

// Páginas internas só abrem com sessão confirmada pelo backend; sem ela, vai para o login
export function RotaProtegida() {
  const { usuario, verificando } = useAuth();
  const location = useLocation();

  if (verificando) {
    return <p className="pagina-simples">Verificando sessão...</p>;
  }

  if (!usuario) {
    return <Navigate to="/login" replace state={{ voltarPara: location.pathname }} />;
  }

  return <Outlet />;
}
