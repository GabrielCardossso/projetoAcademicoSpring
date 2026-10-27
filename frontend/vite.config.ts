import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// A porta 5173 é a origem liberada no CORS do backend (app.cors.origens)
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
  },
});
