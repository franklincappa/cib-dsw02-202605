# Catálogo VentasTech — Frontend

Panel administrativo en React + Tailwind para el microservicio `catalogo-service`.

## Stack
React 19 + Vite · Tailwind CSS (tema oscuro/claro) · React Router DOM · Axios
react-hot-toast · SweetAlert2 · @tabler/icons-react

## Instalación

```bash
npm install
npm run dev
```

App en `http://localhost:5173`. Verifica el `CorsConfig.java` del backend para
permitir ese origen.

## Conexión al backend

`src/api/client.js` → `API_BASE_URL = "http://localhost:8081/api/v1"`

## Novedades de esta versión

### Tema claro / oscuro
- Toggle en la esquina superior derecha (`Topbar.jsx` + `ThemeToggle.jsx`).
- Persistido en `localStorage` (`vt-theme`), respeta `prefers-color-scheme` si no hay preferencia guardada.
- Implementado con CSS variables (`index.css` → `:root` y `:root.light`), no con la utilidad `dark:` de Tailwind, para poder cambiar el sidebar a un degradado sin duplicar componentes.

### Sidebar colapsable (solo iconos)
- Botón de colapsar junto al menú hamburguesa en el `Topbar`.
- Estado persistido en `localStorage` (`vt-sidebar-collapsed`).
- En modo claro, el sidebar usa un degradado azul eléctrico (`#0EA5E9 → #2563EB → #1D4ED8`) en vez de fondo plano, para mantener identidad visual fuerte incluso en tema claro.
- En móvil el sidebar nunca queda en modo solo-iconos: se abre como drawer completo.

### Paginación
- Componente `Pagination.jsx` + hook `usePagination.js`, reutilizado en las 4 páginas (Productos, Categorías, Marcas, Inventario).
- Selector de registros por página: 5 / 10 / 20 / 50.
- Si cambias de filtro/búsqueda y la página actual queda fuera de rango, retrocede automáticamente.

## Estructura nueva

```
src/
├── context/
│   ├── ThemeContext.jsx        light/dark + persistencia
│   └── SidebarContext.jsx      colapsado (desktop) + abierto (mobile)
├── hooks/
│   └── usePagination.js        paginación client-side reutilizable
└── components/
    ├── layout/Topbar.jsx        toggle de tema + colapsar sidebar
    └── ui/
        ├── ThemeToggle.jsx
        └── Pagination.jsx
```

## Exportar Excel e Imprimir PDF

Ambos botones viven en el toolbar de cada página (Productos, Categorías, Marcas, Inventario), junto al buscador.

### Excel (`ExportActions` → icono hoja de cálculo)
- Genera un `.csv` con BOM UTF-8 — Excel lo abre nativo con doble clic, tildes y ñ se ven correctas.
- Exporta el **listado filtrado completo**, no solo la página visible — si tienes 200 productos filtrados y la grilla muestra 10 por página, exporta los 200.
- No se usó la librería `xlsx` (SheetJS) por tener 2 CVEs sin parche disponible (prototype pollution y ReDoS). El CSV-UTF8 cubre el mismo caso de uso sin esa dependencia.
- Archivo: `src/lib/exportUtils.js` → `exportToExcel()`.

### PDF (`ExportActions` → icono impresora)
- Abre una ventana nueva con una tabla HTML limpia (sin sidebar, sin botones) y dispara el diálogo de impresión del navegador.
- El usuario elige "Guardar como PDF" como destino — no se genera un PDF en el cliente con librerías pesadas, se aprovecha el motor de impresión nativo.
- También exporta el listado filtrado completo.
- Archivo: `src/lib/exportUtils.js` → `printListAsPdf()`.

Ambos respetan los filtros de búsqueda y de estado de stock activos en el momento de exportar.
