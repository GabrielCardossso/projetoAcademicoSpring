import { Navigate, Route, Routes } from 'react-router-dom';
import { RotaProtegida } from './auth/RotaProtegida';
import { Layout } from './components/Layout';
import { InicioPage } from './pages/InicioPage';
import { LoginPage } from './pages/LoginPage';
import { OperacaoPage } from './pages/OperacaoPage';

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      {/* Rotas privadas: exigem sessão confirmada pelo backend */}
      <Route element={<RotaProtegida />}>
        <Route element={<Layout />}>
          <Route index element={<InicioPage />} />
          <Route path=":entidade/:operacao" element={<OperacaoPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
