import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ThemeProvider } from "./context/ThemeContext";
import { SidebarProvider } from "./context/SidebarContext";
import AppLayout from "./components/layout/AppLayout";
import ProductosPage from "./pages/ProductosPage";
import CategoriasPage from "./pages/CategoriasPage";
import MarcasPage from "./pages/MarcasPage";
import InventarioPage from "./pages/InventarioPage";

export default function App() {
  return (
    <ThemeProvider>
      <SidebarProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<AppLayout />}>
              <Route index element={<Navigate to="/productos" replace />} />
              <Route path="/productos" element={<ProductosPage />} />
              <Route path="/categorias" element={<CategoriasPage />} />
              <Route path="/marcas" element={<MarcasPage />} />
              <Route path="/inventario" element={<InventarioPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </SidebarProvider>
    </ThemeProvider>
  );
}
