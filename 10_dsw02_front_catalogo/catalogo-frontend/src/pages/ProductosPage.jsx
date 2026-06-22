import { useEffect, useMemo, useState, useCallback } from "react";
import toast from "react-hot-toast";
import {
  IconSearch,
  IconBoxSeam,
  IconFilter,
} from "@tabler/icons-react";

import { productosApi, categoriasApi, marcasApi } from "../api/client";
import PageHeader from "../components/layout/PageHeader";
import Modal from "../components/ui/Modal";
import ProductoForm from "../components/forms/ProductoForm";
import RowActions from "../components/ui/RowActions";
import StatusBadge from "../components/ui/StatusBadge";
import { StockDot, StockBarRow, stockEstado } from "../components/ui/StockBar";
import { TableSkeleton, EmptyState, ErrorState } from "../components/ui/States";
import Pagination from "../components/ui/Pagination";
import ExportActions from "../components/ui/ExportActions";
import { usePagination } from "../hooks/usePagination";
import { exportToExcel, printListAsPdf } from "../lib/exportUtils";
import { confirmDelete } from "../lib/confirm";

const money = (n) =>
  new Intl.NumberFormat("es-PE", { style: "currency", currency: "PEN" }).format(n ?? 0);

export default function ProductosPage() {

  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [marcas, setMarcas] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const [search, setSearch] = useState("");
  const [filtroStock, setFiltroStock] = useState("todos"); // todos | critico | agotado

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  const cargarTodo = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [p, c, m] = await Promise.all([
        productosApi.listar(),
        categoriasApi.listarActivas(),
        marcasApi.listarActivas(),
      ]);
      setProductos(p || []);
      setCategorias(c || []);
      setMarcas(m || []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    cargarTodo();
  }, [cargarTodo]);

  const filtrados = useMemo(() => {
    let list = productos;
    if (search.trim()) {
      const q = search.toLowerCase();
      list = list.filter(
        (p) =>
          p.nombre?.toLowerCase().includes(q) ||
          p.codigoSku?.toLowerCase().includes(q) ||
          p.categoriaNombre?.toLowerCase().includes(q) ||
          p.marcaNombre?.toLowerCase().includes(q)
      );
    }
    if (filtroStock !== "todos") {
      list = list.filter((p) => {
        const estado = stockEstado(p.stockActual, p.stockMinimo);
        if (filtroStock === "critico") return estado === "critico" || estado === "bajo";
        if (filtroStock === "agotado") return estado === "agotado";
        return true;
      });
    }
    return list;
  }, [productos, search, filtroStock]);

  const { page, setPage, pageSize, setPageSize, paginated, totalItems } = usePagination(filtrados, 10);

  const openCreate = () => {
    setEditing(null);
    setModalOpen(true);
  };

  const openEdit = (producto) => {
    setEditing(producto);
    setModalOpen(true);
  };

  const handleSubmit = async (payload) => {
    setSaving(true);
    try {
      if (editing) {
        await productosApi.actualizar(editing.id, payload);
        toast.success("Producto actualizado");
      } else {
        await productosApi.crear(payload);
        toast.success("Producto creado");
      }
      setModalOpen(false);
      cargarTodo();
    } catch (e) {
      toast.error(e.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (producto) => {
    const ok = await confirmDelete({
      title: `¿Eliminar "${producto.nombre}"?`,
      text: `El SKU ${producto.codigoSku} se eliminará permanentemente del catálogo.`,
    });
    if (!ok) return;

    try {
      await productosApi.eliminar(producto.id);
      toast.success("Producto eliminado");
      setProductos((prev) => prev.filter((p) => p.id !== producto.id));
    } catch (e) {
      toast.error(e.message);
    }
  };

  const handleDownload = (producto) => {
    const rows = [
      ["SKU", "Nombre", "Categoría", "Marca", "Precio", "Stock"],
      [producto.codigoSku, producto.nombre, producto.categoriaNombre, producto.marcaNombre, producto.precio, producto.stockActual],
    ];
    const csv = rows.map((r) => r.join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `producto-${producto.codigoSku}.csv`;
    a.click();
    URL.revokeObjectURL(url);
    toast.success("Descarga iniciada");
  };

  const handlePrint = (producto) => {
    const w = window.open("", "_blank");
    w.document.write(`
      <html><head><title>${producto.codigoSku}</title>
      <style>body{font-family:sans-serif;padding:40px;color:#0F172A}
      h1{font-size:20px}table{margin-top:16px;width:100%;border-collapse:collapse}
      td{padding:8px;border-bottom:1px solid #e2e8f0;font-size:14px}
      td:first-child{color:#64748b;width:160px}</style></head>
      <body><h1>VentasTech — Ficha de producto</h1>
      <table>
        <tr><td>SKU</td><td>${producto.codigoSku}</td></tr>
        <tr><td>Nombre</td><td>${producto.nombre}</td></tr>
        <tr><td>Categoría</td><td>${producto.categoriaNombre ?? "-"}</td></tr>
        <tr><td>Marca</td><td>${producto.marcaNombre ?? "-"}</td></tr>
        <tr><td>Precio</td><td>${money(producto.precio)}</td></tr>
        <tr><td>Stock actual</td><td>${producto.stockActual ?? "-"}</td></tr>
      </table></body></html>
    `);
    w.document.close();
    w.print();
  };

  // Columnas para exportación masiva (Excel y PDF) — usa la lista filtrada completa,
  // no solo la página actual visible en pantalla.
  const exportColumns = [
    { label: "SKU", value: (p) => p.codigoSku },
    { label: "Nombre", value: (p) => p.nombre },
    { label: "Categoría", value: (p) => p.categoriaNombre || "-" },
    { label: "Marca", value: (p) => p.marcaNombre || "-" },
    { label: "Precio", value: (p) => money(p.precio) },
    { label: "Stock actual", value: (p) => p.stockActual ?? "-" },
    { label: "Stock mínimo", value: (p) => p.stockMinimo ?? "-" },
    { label: "Estado", value: (p) => (p.activo ? "Activo" : "Inactivo") },
  ];

  const handleExportExcel = () => {
    exportToExcel(filtrados, exportColumns, "productos");
    toast.success(`${filtrados.length} productos exportados`);
  };

  const handlePrintListPdf = () => {
    printListAsPdf({
      title: "Listado de Productos",
      subtitle: `${filtrados.length} productos${search ? ` · filtro: "${search}"` : ""}`,
      columns: exportColumns,
      rows: filtrados,
    });
  };

  return (
    <div>
      <PageHeader
        title="Productos"
        subtitle={`${productos.length} productos en el catálogo`}
        onAdd={openCreate}
        addLabel="Nuevo producto"
        
      />

      {/* Toolbar */}
      <div className="flex flex-col sm:flex-row gap-3 mb-5">
        <div className="relative flex-1 max-w-sm">
          <IconSearch size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-600" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar por nombre, SKU, marca..."
            className="w-full bg-base-700 border border-base-600 rounded-lg pl-10 pr-3.5 py-2.5 text-sm text-ink-100 placeholder:text-ink-600 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40 transition-colors"
          />
        </div>

        <div className="flex items-center gap-1.5 bg-base-700 border border-base-600 rounded-lg p-1">
          {[
            { key: "todos", label: "Todos" },
            { key: "critico", label: "Stock bajo" },
            { key: "agotado", label: "Agotados" },
          ].map((opt) => (
            <button
              key={opt.key}
              onClick={() => setFiltroStock(opt.key)}
              className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors ${
                filtroStock === opt.key
                  ? "bg-accent text-base-900"
                  : "text-ink-400 hover:text-ink-100"
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>

        <ExportActions
          onExportExcel={handleExportExcel}
          onPrintPdf={handlePrintListPdf}
          disabled={loading || filtrados.length === 0}
        />
      </div>

      {/* Tabla */}
      <div className="bg-base-700 border border-base-600 rounded-2xl overflow-hidden shadow-card">
        {loading ? (
          <TableSkeleton rows={6} cols={6} />
        ) : error ? (
          <ErrorState message={error} onRetry={cargarTodo} />
        ) : totalItems === 0 ? (
          <EmptyState
            title={search ? "Sin resultados" : "Aún no hay productos"}
            text={
              search
                ? "Intenta con otro término de búsqueda o quita los filtros."
                : "Crea tu primer producto para empezar a construir el catálogo."
            }
            actionLabel={!search ? "Crear producto" : undefined}
            onAction={!search ? openCreate : undefined}
          />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-base-600 text-left">
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider">Producto</th>
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider hidden md:table-cell">Categoría</th>
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider hidden lg:table-cell">Marca</th>
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider text-right">Precio</th>
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider text-center">Stock</th>
                  <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider">Estado</th>
                  <th className="px-5 py-3"></th>
                </tr>
              </thead>
              <tbody className="divide-y divide-base-600">
                {paginated.map((p) => (
                  <tr key={p.id} className="hover:bg-base-600/30 transition-colors group">
                    <td className="p-0">
                      <StockBarRow actual={p.stockActual} minimo={p.stockMinimo}>
                        <div className="px-5 py-3.5 flex items-center gap-3">
                          <div className="w-9 h-9 rounded-lg bg-base-600/50 flex items-center justify-center shrink-0">
                            <IconBoxSeam size={17} className="text-ink-500" />
                          </div>
                          <div className="min-w-0">
                            <p className="font-medium text-ink-100 truncate">{p.nombre}</p>
                            <p className="text-xs text-ink-600 font-mono">{p.codigoSku}</p>
                          </div>
                        </div>
                      </StockBarRow>
                    </td>
                    <td className="px-5 py-3.5 text-ink-300 hidden md:table-cell">
                      {p.categoriaNombre || <span className="text-ink-600">—</span>}
                    </td>
                    <td className="px-5 py-3.5 text-ink-300 hidden lg:table-cell">
                      {p.marcaNombre || <span className="text-ink-600">—</span>}
                    </td>
                    <td className="px-5 py-3.5 text-right font-medium text-ink-100">
                      {money(p.precio)}
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="flex items-center justify-center gap-2">
                        <StockDot actual={p.stockActual} minimo={p.stockMinimo} />
                        <span className="text-ink-300 tabular-nums">{p.stockActual ?? "—"}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5">
                      <StatusBadge activo={p.activo} />
                    </td>
                    <td className="px-5 py-3.5">
                      <RowActions
                        onEdit={() => openEdit(p)}
                        onDelete={() => handleDelete(p)}
                        onDownload={() => handleDownload(p)}
                        onPrint={() => handlePrint(p)}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        {!loading && !error && totalItems > 0 && (
          <Pagination
            page={page}
            pageSize={pageSize}
            totalItems={totalItems}
            onPageChange={setPage}
            onPageSizeChange={setPageSize}
          />
        )}
      </div>

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? "Editar producto" : "Nuevo producto"}
        subtitle={editing ? `SKU ${editing.codigoSku}` : "Completa los datos para agregarlo al catálogo"}
        width="max-w-xl"
      >
        <ProductoForm
          initialData={editing}
          categorias={categorias}
          marcas={marcas}
          onSubmit={handleSubmit}
          onCancel={() => setModalOpen(false)}
          saving={saving}
        />
      </Modal>
    </div>
  );
}
