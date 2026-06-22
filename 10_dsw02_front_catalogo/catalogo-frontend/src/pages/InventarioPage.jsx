import { useEffect, useState, useCallback } from "react";
import toast from "react-hot-toast";
import { IconSearch, IconPlus, IconMinus, IconStack2 } from "@tabler/icons-react";

import { inventarioApi } from "../api/client";
import PageHeader from "../components/layout/PageHeader";
import Modal from "../components/ui/Modal";
import Button from "../components/ui/Button";
import { Field, Input } from "../components/ui/Field";
import { StockDot, StockBarRow, stockEstado } from "../components/ui/StockBar";
import { TableSkeleton, EmptyState, ErrorState } from "../components/ui/States";
import Pagination from "../components/ui/Pagination";
import ExportActions from "../components/ui/ExportActions";
import { usePagination } from "../hooks/usePagination";
import { exportToExcel, printListAsPdf } from "../lib/exportUtils";

const estadoLabel = {
  ok: { text: "Saludable", cls: "text-emerald-400 bg-ok/10 border-ok/25" },
  bajo: { text: "Bajo", cls: "text-amber-400 bg-warn/10 border-warn/25" },
  critico: { text: "Crítico", cls: "text-red-400 bg-danger/10 border-danger/25" },
  agotado: { text: "Agotado", cls: "text-red-400 bg-danger/10 border-danger/25" },
  neutral: { text: "—", cls: "text-ink-500 bg-base-600/30 border-base-500" },
};

export default function InventarioPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState("");
  const [filtro, setFiltro] = useState("todos");

  const [ajusteModal, setAjusteModal] = useState(null); // item seleccionado
  const [cantidad, setCantidad] = useState("");
  const [modo, setModo] = useState("entrada"); // entrada | salida
  const [saving, setSaving] = useState(false);

  const cargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems((await inventarioApi.listar()) || []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { cargar(); }, [cargar]);

  const filtrados = items.filter((i) => {
    const q = search.toLowerCase();
    const matchSearch =
      i.productoNombre?.toLowerCase().includes(q) || i.productoSku?.toLowerCase().includes(q);
    if (!matchSearch) return false;
    if (filtro === "todos") return true;
    const estado = stockEstado(i.stockActual, i.stockMinimo);
    if (filtro === "critico") return estado === "critico" || estado === "bajo";
    if (filtro === "agotado") return estado === "agotado";
    return true;
  });

  const { page, setPage, pageSize, setPageSize, paginated, totalItems } = usePagination(filtrados, 10);

  const openAjuste = (item, modoInicial) => {
    setAjusteModal(item);
    setModo(modoInicial);
    setCantidad("");
  };

  const handleAjustar = async (e) => {
    e.preventDefault();
    const n = Number(cantidad);
    if (!n || n <= 0) {
      toast.error("Ingresa una cantidad válida");
      return;
    }
    setSaving(true);
    try {
      const delta = modo === "entrada" ? n : -n;
      await inventarioApi.ajustar(ajusteModal.productoId, delta);
      toast.success(modo === "entrada" ? `+${n} unidades agregadas` : `-${n} unidades retiradas`);
      setAjusteModal(null);
      cargar();
    } catch (e) {
      toast.error(e.message);
    } finally {
      setSaving(false);
    }
  };

  const exportColumns = [
    { label: "SKU", value: (i) => i.productoSku },
    { label: "Producto", value: (i) => i.productoNombre },
    { label: "Stock actual", value: (i) => i.stockActual },
    { label: "Stock mínimo", value: (i) => i.stockMinimo },
    { label: "Estado", value: (i) => estadoLabel[stockEstado(i.stockActual, i.stockMinimo)].text },
  ];

  const handleExportExcel = () => {
    exportToExcel(filtrados, exportColumns, "inventario");
    toast.success(`${filtrados.length} registros exportados`);
  };

  const handlePrintListPdf = () => {
    printListAsPdf({
      title: "Reporte de Inventario",
      subtitle: `${filtrados.length} productos${search ? ` · filtro: "${search}"` : ""}`,
      columns: exportColumns,
      rows: filtrados,
    });
  };

  return (
    <div>
      <PageHeader
        title="Inventario"
        subtitle={`${items.length} productos con seguimiento de stock`}
        
      />

      <div className="flex flex-col sm:flex-row gap-3 mb-5">
        <div className="relative flex-1 max-w-sm">
          <IconSearch size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-600" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar producto o SKU..."
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
              onClick={() => setFiltro(opt.key)}
              className={`px-3 py-1.5 rounded-md text-xs font-medium transition-colors ${
                filtro === opt.key ? "bg-accent text-base-900" : "text-ink-400 hover:text-ink-100"
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

      <div className="bg-base-700 border border-base-600 rounded-2xl overflow-hidden shadow-card">
        {loading ? (
          <TableSkeleton rows={6} cols={5} />
        ) : error ? (
          <ErrorState message={error} onRetry={cargar} />
        ) : totalItems === 0 ? (
          <EmptyState title="Sin resultados" text="No hay productos que coincidan con el filtro." />
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-base-600 text-left">
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider">Producto</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider text-center">Stock actual</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider text-center hidden sm:table-cell">Mínimo</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider hidden md:table-cell">Estado</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider text-right">Ajustar</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-base-600">
              {paginated.map((i) => {
                const estado = stockEstado(i.stockActual, i.stockMinimo);
                const label = estadoLabel[estado];
                return (
                  <tr key={i.id} className="hover:bg-base-600/30 transition-colors">
                    <td className="p-0">
                      <StockBarRow actual={i.stockActual} minimo={i.stockMinimo}>
                        <div className="px-5 py-3.5 flex items-center gap-3">
                          <div className="w-9 h-9 rounded-lg bg-base-600/50 flex items-center justify-center shrink-0">
                            <IconStack2 size={17} className="text-ink-500" />
                          </div>
                          <div className="min-w-0">
                            <p className="font-medium text-ink-100 truncate">{i.productoNombre}</p>
                            <p className="text-xs text-ink-600 font-mono">{i.productoSku}</p>
                          </div>
                        </div>
                      </StockBarRow>
                    </td>
                    <td className="px-5 py-3.5 text-center">
                      <div className="inline-flex items-center gap-2">
                        <StockDot actual={i.stockActual} minimo={i.stockMinimo} />
                        <span className="font-semibold text-ink-100 tabular-nums">{i.stockActual}</span>
                      </div>
                    </td>
                    <td className="px-5 py-3.5 text-center text-ink-400 tabular-nums hidden sm:table-cell">
                      {i.stockMinimo}
                    </td>
                    <td className="px-5 py-3.5 hidden md:table-cell">
                      <span className={`inline-flex px-2.5 py-1 rounded-full text-xs font-medium border ${label.cls}`}>
                        {label.text}
                      </span>
                    </td>
                    <td className="px-5 py-3.5">
                      <div className="flex items-center justify-end gap-1.5">
                        <button
                          onClick={() => openAjuste(i, "salida")}
                          title="Registrar salida"
                          className="p-2 rounded-lg text-ink-500 hover:text-red-400 hover:bg-danger/10 transition-colors"
                        >
                          <IconMinus size={16} />
                        </button>
                        <button
                          onClick={() => openAjuste(i, "entrada")}
                          title="Registrar entrada"
                          className="p-2 rounded-lg text-ink-500 hover:text-emerald-400 hover:bg-ok/10 transition-colors"
                        >
                          <IconPlus size={16} />
                        </button>
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
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
        open={Boolean(ajusteModal)}
        onClose={() => setAjusteModal(null)}
        title={modo === "entrada" ? "Registrar entrada de stock" : "Registrar salida de stock"}
        subtitle={ajusteModal?.productoNombre}
        width="max-w-sm"
      >
        {ajusteModal && (
          <form onSubmit={handleAjustar}>
            <div className="flex items-center gap-1.5 bg-base-800 border border-base-600 rounded-lg p-1 mb-4">
              <button
                type="button"
                onClick={() => setModo("entrada")}
                className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${
                  modo === "entrada" ? "bg-ok/15 text-emerald-400" : "text-ink-500"
                }`}
              >
                + Entrada
              </button>
              <button
                type="button"
                onClick={() => setModo("salida")}
                className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${
                  modo === "salida" ? "bg-danger/15 text-red-400" : "text-ink-500"
                }`}
              >
                − Salida
              </button>
            </div>

            <Field label="Cantidad" hint={`Stock actual: ${ajusteModal.stockActual} unidades`}>
              <Input
                type="number"
                min="1"
                autoFocus
                value={cantidad}
                onChange={(e) => setCantidad(e.target.value)}
                placeholder="0"
              />
            </Field>

            <div className="flex justify-end gap-3 pt-3 border-t border-base-600 mt-2">
              <Button type="button" variant="ghost" onClick={() => setAjusteModal(null)}>
                Cancelar
              </Button>
              <Button type="submit" loading={saving}>
                {modo === "entrada" ? "Registrar entrada" : "Registrar salida"}
              </Button>
            </div>
          </form>
        )}
      </Modal>
    </div>
  );
}
