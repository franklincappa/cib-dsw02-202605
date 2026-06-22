import axios from "axios";

// Cambia esto si tu backend corre en otro host/puerto
export const API_BASE_URL = "http://localhost:8081/api/v1";

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

// Interceptor de respuesta: tu backend envuelve todo en ApiResponse { success, mensaje, data }
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const mensaje =
      error.response?.data?.mensaje ||
      error.message ||
      "Error de conexión con el servidor";
    return Promise.reject(new Error(mensaje));
  }
);

// Helper: desenvuelve { success, mensaje, data } -> data
const unwrap = (promise) => promise.then((res) => res.data.data);

export const productosApi = {
  listar: () => unwrap(api.get("/productos")),
  listarActivos: () => unwrap(api.get("/productos/activos")),
  obtener: (id) => unwrap(api.get(`/productos/${id}`)),
  buscar: (nombre) => unwrap(api.get("/productos/buscar", { params: { nombre } })),
  porCategoria: (categoriaId) => unwrap(api.get(`/productos/categoria/${categoriaId}`)),
  porMarca: (marcaId) => unwrap(api.get(`/productos/marca/${marcaId}`)),
  bajoCritico: () => unwrap(api.get("/productos/bajo-critico")),
  crear: (payload) => unwrap(api.post("/productos", payload)),
  actualizar: (id, payload) => unwrap(api.put(`/productos/${id}`, payload)),
  eliminar: (id) => unwrap(api.delete(`/productos/${id}`)),
  desactivar: (id) => unwrap(api.patch(`/productos/${id}/desactivar`)),
};

export const categoriasApi = {
  listar: () => unwrap(api.get("/categorias")),
  listarActivas: () => unwrap(api.get("/categorias/activas")),
  obtener: (id) => unwrap(api.get(`/categorias/${id}`)),
  crear: (payload) => unwrap(api.post("/categorias", payload)),
  actualizar: (id, payload) => unwrap(api.put(`/categorias/${id}`, payload)),
  eliminar: (id) => unwrap(api.delete(`/categorias/${id}`)),
  desactivar: (id) => unwrap(api.patch(`/categorias/${id}/desactivar`)),
};

export const marcasApi = {
  listar: () => unwrap(api.get("/marcas")),
  listarActivas: () => unwrap(api.get("/marcas/activas")),
  obtener: (id) => unwrap(api.get(`/marcas/${id}`)),
  porPais: (pais) => unwrap(api.get("/marcas/pais", { params: { pais } })),
  crear: (payload) => unwrap(api.post("/marcas", payload)),
  actualizar: (id, payload) => unwrap(api.put(`/marcas/${id}`, payload)),
  eliminar: (id) => unwrap(api.delete(`/marcas/${id}`)),
  desactivar: (id) => unwrap(api.patch(`/marcas/${id}/desactivar`)),
};

export const inventarioApi = {
  listar: () => unwrap(api.get("/inventario")),
  obtener: (id) => unwrap(api.get(`/inventario/${id}`)),
  porProducto: (productoId) => unwrap(api.get(`/inventario/producto/${productoId}`)),
  bajoCritico: () => unwrap(api.get("/inventario/bajo-critico")),
  sinStock: () => unwrap(api.get("/inventario/sin-stock")),
  actualizar: (productoId, payload) => unwrap(api.put(`/inventario/producto/${productoId}`, payload)),
  ajustar: (productoId, cantidad) =>
    unwrap(api.patch(`/inventario/producto/${productoId}/ajustar`, null, { params: { cantidad } })),
};
