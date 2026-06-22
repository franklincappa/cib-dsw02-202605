import { useEffect, useState, useCallback } from "react";
import toast from "react-hot-toast";
import { IconTag, IconSearch, IconMapPin } from "@tabler/icons-react";

import { marcasApi } from "../api/client";
import PageHeader from "../components/layout/PageHeader";
import Modal from "../components/ui/Modal";
import SimpleEntityForm from "../components/forms/SimpleEntityForm";
import RowActions from "../components/ui/RowActions";
import StatusBadge from "../components/ui/StatusBadge";
import { TableSkeleton, EmptyState, ErrorState } from "../components/ui/States";
import Pagination from "../components/ui/Pagination";
import ExportActions from "../components/ui/ExportActions";
import { usePagination } from "../hooks/usePagination";
import { exportToExcel, printListAsPdf } from "../lib/exportUtils";
import { confirmDelete } from "../lib/confirm";

export default function MarcasPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  const cargar = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setItems((await marcasApi.listar()) || []);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { cargar(); }, [cargar]);

  const filtrados = items.filter((m) =>
    m.nombre?.toLowerCase().includes(search.toLowerCase()) ||
    m.paisOrigen?.toLowerCase().includes(search.toLowerCase())
  );

  const { page, setPage, pageSize, setPageSize, paginated, totalItems } = usePagination(filtrados, 10);

  const handleSubmit = async (payload) => {
    setSaving(true);
    try {
      if (editing) {
        await marcasApi.actualizar(editing.id, payload);
        toast.success("Marca actualizada");
      } else {
        await marcasApi.crear(payload);
        toast.success("Marca creada");
      }
      setModalOpen(false);
      cargar();
    } catch (e) {
      toast.error(e.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (item) => {
    const ok = await confirmDelete({
      title: `¿Eliminar "${item.nombre}"?`,
      text: "Los productos asociados quedarán sin marca.",
    });
    if (!ok) return;
    try {
      await marcasApi.eliminar(item.id);
      toast.success("Marca eliminada");
      setItems((prev) => prev.filter((i) => i.id !== item.id));
    } catch (e) {
      toast.error(e.message);
    }
  };

  const exportColumns = [
    { label: "Marca", value: (m) => m.nombre },
    { label: "País de origen", value: (m) => m.paisOrigen || "-" },
    { label: "Descripción", value: (m) => m.descripcion || "-" },
    { label: "Estado", value: (m) => (m.activo ? "Activo" : "Inactivo") },
  ];

  const handleExportExcel = () => {
    exportToExcel(filtrados, exportColumns, "marcas");
    toast.success(`${filtrados.length} marcas exportadas`);
  };

  const handlePrintListPdf = () => {
    printListAsPdf({
      title: "Listado de Marcas",
      subtitle: `${filtrados.length} marcas${search ? ` · filtro: "${search}"` : ""}`,
      columns: exportColumns,
      rows: filtrados,
    });
  };

  return (
    <div>
      <PageHeader
        title="Marcas"
        subtitle={`${items.length} marcas registradas`}
        onAdd={() => { setEditing(null); setModalOpen(true); }}
        addLabel="Nueva marca"
        
      />

      <div className="flex flex-col sm:flex-row gap-3 mb-5">
        <div className="relative flex-1 max-w-sm">
          <IconSearch size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-600" />
          <input
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Buscar marca o país..."
            className="w-full bg-base-700 border border-base-600 rounded-lg pl-10 pr-3.5 py-2.5 text-sm text-ink-100 placeholder:text-ink-600 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40 transition-colors"
          />
        </div>

        <ExportActions
          onExportExcel={handleExportExcel}
          onPrintPdf={handlePrintListPdf}
          disabled={loading || filtrados.length === 0}
        />
      </div>

      <div className="bg-base-700 border border-base-600 rounded-2xl overflow-hidden shadow-card">
        {loading ? (
          <TableSkeleton rows={5} cols={4} />
        ) : error ? (
          <ErrorState message={error} onRetry={cargar} />
        ) : totalItems === 0 ? (
          <EmptyState
            title="Sin marcas"
            text="Registra las marcas de los productos que vendes."
            actionLabel="Crear marca"
            onAction={() => { setEditing(null); setModalOpen(true); }}
          />
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-base-600 text-left">
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider">Marca</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider hidden md:table-cell">País de origen</th>
                <th className="px-5 py-3 font-medium text-ink-500 text-xs uppercase tracking-wider">Estado</th>
                <th className="px-5 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-base-600">
              {paginated.map((m) => (
                <tr key={m.id} className="hover:bg-base-600/30 transition-colors">
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-3">
                      <div className="w-9 h-9 rounded-lg bg-accent/10 flex items-center justify-center shrink-0">
                        <IconTag size={17} className="text-accent" />
                      </div>
                      <span className="font-medium text-ink-100">{m.nombre}</span>
                    </div>
                  </td>
                  <td className="px-5 py-3.5 text-ink-400 hidden md:table-cell">
                    {m.paisOrigen ? (
                      <span className="inline-flex items-center gap-1.5">
                        <IconMapPin size={14} className="text-ink-600" />
                        {m.paisOrigen}
                      </span>
                    ) : <span className="text-ink-600">—</span>}
                  </td>
                  <td className="px-5 py-3.5"><StatusBadge activo={m.activo} /></td>
                  <td className="px-5 py-3.5">
                    <RowActions onEdit={() => { setEditing(m); setModalOpen(true); }} onDelete={() => handleDelete(m)} />
                  </td>
                </tr>
              ))}
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
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        title={editing ? "Editar marca" : "Nueva marca"}
      >
        <SimpleEntityForm
          initialData={editing}
          onSubmit={handleSubmit}
          onCancel={() => setModalOpen(false)}
          saving={saving}
          entityLabel="marca"
          extraField={{ key: "paisOrigen", label: "País de origen", placeholder: "Japón" }}
        />
      </Modal>
    </div>
  );
}
