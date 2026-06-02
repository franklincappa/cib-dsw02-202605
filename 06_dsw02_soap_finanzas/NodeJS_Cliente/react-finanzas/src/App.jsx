import { useState, useEffect, useCallback } from "react";
import {
  Search, X, Plus, RefreshCw, Pencil, Lock, Unlock,
  Trash2, Users, Loader2, CheckCircle, AlertCircle,
  Building2, User, ChevronRight, Save
} from "lucide-react";

const API = import.meta.env.VITE_API_URL || "http://localhost:3001/api";

const api = {
  get:    url       => fetch(`${API}${url}`).then(r => r.json()),
  post:   (url, b)  => fetch(`${API}${url}`, { method:"POST",  headers:{"Content-Type":"application/json"}, body:JSON.stringify(b) }).then(r=>r.json()),
  put:    (url, b)  => fetch(`${API}${url}`, { method:"PUT",   headers:{"Content-Type":"application/json"}, body:JSON.stringify(b) }).then(r=>r.json()),
  patch:  (url, b)  => fetch(`${API}${url}`, { method:"PATCH", headers:{"Content-Type":"application/json"}, body:JSON.stringify(b) }).then(r=>r.json()),
  delete: url       => fetch(`${API}${url}`, { method:"DELETE" }).then(r=>r.json()),
};

const EMPTY = {
  tipoDocumento:"DNI", nroDocumento:"", nombres:"", apellidoPaterno:"",
  apellidoMaterno:"", fechaNacimiento:"", direccion:"", telefono:"",
  email:"", tipoCliente:"NATURAL"
};

// ── Toast ─────────────────────────────────────────────────────────────────────
function Toast({ toast }) {
  if (!toast) return null;
  const isOk = toast.type === "ok";
  return (
    <div className={`fixed top-4 right-4 z-50 flex items-center gap-2 px-4 py-3 rounded-xl shadow-lg text-sm font-medium transition-all
      ${isOk ? "bg-green-50 text-green-800 border border-green-200" : "bg-red-50 text-red-800 border border-red-200"}`}>
      {isOk
        ? <CheckCircle size={16} className="text-green-600 shrink-0" />
        : <AlertCircle size={16} className="text-red-600 shrink-0" />}
      {toast.msg}
    </div>
  );
}

// ── Badge estado ──────────────────────────────────────────────────────────────
function EstadoBadge({ estado }) {
  const map = {
    ACTIVO:   "bg-green-100 text-green-800 ring-1 ring-green-200",
    BLOQUEADO:"bg-amber-100 text-amber-800 ring-1 ring-amber-200",
    INACTIVO: "bg-gray-100  text-gray-600  ring-1 ring-gray-200",
  };
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold ${map[estado] ?? map.INACTIVO}`}>
      {estado}
    </span>
  );
}

// ── Badge tipo doc ────────────────────────────────────────────────────────────
function DocBadge({ tipo }) {
  return (
    <span className="inline-flex items-center px-2 py-0.5 rounded text-xs font-bold bg-blue-100 text-blue-700 mr-1.5">
      {tipo}
    </span>
  );
}

// ── Campo de formulario ───────────────────────────────────────────────────────
function Field({ label, required, children }) {
  return (
    <div className="flex flex-col gap-1">
      <label className="text-xs font-semibold text-gray-500 uppercase tracking-wide">
        {label}{required && <span className="text-red-400 ml-0.5">*</span>}
      </label>
      {children}
    </div>
  );
}

const inputCls = "w-full px-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white disabled:bg-gray-50 disabled:text-gray-400";

// ── Modal ─────────────────────────────────────────────────────────────────────
function ClienteModal({ cliente, onClose, onSaved }) {
  const [form, setForm]       = useState(cliente ?? EMPTY);
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState("");
  const editing = !!form.idCliente;

  const set = k => e => setForm(f => ({ ...f, [k]: e.target.value }));

  const submit = async () => {
    setError(""); setLoading(true);
    try {
      const resp = editing
        ? await api.put(`/clientes/${form.idCliente}`, form)
        : await api.post("/clientes", form);
      if (resp.codigo === "00") { onSaved(resp.mensaje); onClose(); }
      else setError(resp.mensaje);
    } catch { setError("Error de conexión con el servidor."); }
    finally { setLoading(false); }
  };

  return (
    <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-40 p-4"
         onClick={e => e.target === e.currentTarget && onClose()}>
      <div className="bg-white rounded-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto shadow-2xl">

        {/* Header modal */}
        <div className="flex items-center justify-between px-6 py-4 bg-blue-600 rounded-t-2xl">
          <div className="flex items-center gap-2 text-white font-semibold">
            {editing ? <Pencil size={18}/> : <Plus size={18}/>}
            {editing ? "Editar Cliente" : "Nuevo Cliente"}
          </div>
          <button onClick={onClose}
            className="text-white/70 hover:text-white hover:bg-white/20 rounded-lg p-1 transition-colors">
            <X size={20}/>
          </button>
        </div>

        {/* Body modal */}
        <div className="p-6 space-y-5">
          {error && (
            <div className="flex items-center gap-2 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">
              <AlertCircle size={16} className="shrink-0"/>
              {error}
            </div>
          )}

          {/* Identificación */}
          <div>
            <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center gap-1">
              <ChevronRight size={12}/> Identificación
            </p>
            <div className="grid grid-cols-2 gap-4">
              <Field label="Tipo Documento" required>
                <select className={inputCls} value={form.tipoDocumento}
                        onChange={set("tipoDocumento")} disabled={editing}>
                  <option>DNI</option>
                  <option>RUC</option>
                  <option>CE</option>
                </select>
              </Field>
              <Field label="Nro. Documento" required>
                <input className={inputCls} value={form.nroDocumento}
                       onChange={set("nroDocumento")} readOnly={editing} maxLength={15}/>
              </Field>
            </div>
          </div>

          {/* Datos personales */}
          <div>
            <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center gap-1">
              <ChevronRight size={12}/> Datos Personales
            </p>
            <div className="grid grid-cols-2 gap-4">
              <Field label="Nombres" required>
                <input className={inputCls} value={form.nombres}
                       onChange={set("nombres")} maxLength={100}/>
              </Field>
              <Field label="Apellido Paterno" required>
                <input className={inputCls} value={form.apellidoPaterno}
                       onChange={set("apellidoPaterno")} maxLength={60}/>
              </Field>
              <Field label="Apellido Materno">
                <input className={inputCls} value={form.apellidoMaterno || ""}
                       onChange={set("apellidoMaterno")} maxLength={60}/>
              </Field>
              <Field label="Fecha Nacimiento">
                <input className={inputCls} value={form.fechaNacimiento || ""}
                       onChange={set("fechaNacimiento")} placeholder="dd/MM/yyyy"/>
              </Field>
            </div>
          </div>

          {/* Contacto */}
          <div>
            <p className="text-xs font-bold text-gray-400 uppercase tracking-widest mb-3 flex items-center gap-1">
              <ChevronRight size={12}/> Contacto
            </p>
            <div className="grid grid-cols-2 gap-4">
              <Field label="Teléfono" required>
                <input className={inputCls} value={form.telefono}
                       onChange={set("telefono")} maxLength={15}/>
              </Field>
              <Field label="Email" required>
                <input className={inputCls} type="email" value={form.email}
                       onChange={set("email")} maxLength={100}/>
              </Field>
              <Field label="Tipo Cliente" required>
                <select className={inputCls} value={form.tipoCliente}
                        onChange={set("tipoCliente")}>
                  <option value="NATURAL">Persona Natural</option>
                  <option value="JURIDICO">Persona Jurídica</option>
                </select>
              </Field>
            </div>
            <div className="mt-4">
              <Field label="Dirección" required>
                <input className={inputCls} value={form.direccion}
                       onChange={set("direccion")} maxLength={200}/>
              </Field>
            </div>
          </div>

          {/* Acciones */}
          <div className="flex justify-end gap-3 pt-2 border-t border-gray-100">
            <button onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-600 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors">
              Cancelar
            </button>
            <button onClick={submit} disabled={loading}
              className="flex items-center gap-2 px-5 py-2 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 disabled:opacity-50 rounded-lg transition-colors">
              {loading
                ? <><Loader2 size={15} className="animate-spin"/> Guardando...</>
                : <><Save size={15}/> {editing ? "Guardar cambios" : "Registrar cliente"}</>}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

// ── App ───────────────────────────────────────────────────────────────────────
export default function App() {
  const [clientes, setClientes] = useState([]);
  const [loading,  setLoading]  = useState(false);
  const [buscar,   setBuscar]   = useState("");
  const [modal,    setModal]    = useState(null);
  const [toast,    setToast]    = useState(null);

  const showToast = (type, msg) => {
    setToast({ type, msg });
    setTimeout(() => setToast(null), 4000);
  };

  const cargar = useCallback(async (termino = "") => {
    setLoading(true);
    try {
      const resp = termino
        ? await api.get(`/clientes?buscar=${encodeURIComponent(termino)}`)
        : await api.get("/clientes");
      if (resp.codigo === "00")
        setClientes(Array.isArray(resp.data) ? resp.data : resp.data ? [resp.data] : []);
      else showToast("err", resp.mensaje);
    } catch {
      showToast("err", "No se pudo conectar con el servidor.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { cargar(); }, [cargar]);

  const onSearch = e => { e.preventDefault(); cargar(buscar); };

  const cambiarEstado = async (id, estado) => {
    const resp = await api.patch(`/clientes/${id}/estado`, { estado });
    showToast(resp.codigo === "00" ? "ok" : "err", resp.mensaje);
    if (resp.codigo === "00") cargar(buscar);
  };

  const eliminar = async (id) => {
    if (!confirm("¿Confirma la baja lógica del cliente?")) return;
    const resp = await api.delete(`/clientes/${id}`);
    showToast(resp.codigo === "00" ? "ok" : "err", resp.mensaje);
    if (resp.codigo === "00") cargar(buscar);
  };

  const onSaved = msg => { showToast("ok", msg); cargar(buscar); };

  return (
    <div className="min-h-screen bg-slate-50">
      <Toast toast={toast} />

      {/* Header */}
      <header className="bg-blue-600 shadow-md">
        <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-white/20 rounded-xl p-2">
              <Building2 size={22} className="text-white"/>
            </div>
            <div>
              <h1 className="text-white font-bold text-lg leading-tight">FinanzasPeru S.A.</h1>
              <p className="text-blue-200 text-xs">Gestión de Clientes</p>
            </div>
          </div>
          <div className="flex items-center gap-2 text-blue-200 text-xs">
            <div className="w-2 h-2 rounded-full bg-green-400 animate-pulse"/>
            SOAP · Node.js · React
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-6">

        {/* Stats card */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 px-6 py-4 mb-5 flex items-center gap-4">
          <div className="bg-blue-50 rounded-xl p-3">
            <Users size={22} className="text-blue-600"/>
          </div>
          <div>
            <p className="text-2xl font-bold text-gray-800">{clientes.length}</p>
            <p className="text-xs text-gray-400">Clientes cargados</p>
          </div>
          <div className="ml-auto text-xs text-gray-400">
            Fuente: Web Service SOAP → Node.js → React
          </div>
        </div>

        {/* Toolbar */}
        <div className="flex flex-wrap gap-3 mb-4 items-center">
          <form onSubmit={onSearch} className="flex gap-2 flex-1 min-w-64">
            <div className="relative flex-1">
              <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"/>
              <input
                className="w-full pl-9 pr-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
                value={buscar}
                onChange={e => setBuscar(e.target.value)}
                placeholder="Buscar por nombre, apellido o documento..."
              />
            </div>
            <button type="submit"
              className="flex items-center gap-1.5 px-4 py-2.5 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl transition-colors">
              <Search size={14}/> Buscar
            </button>
            {buscar && (
              <button type="button" onClick={() => { setBuscar(""); cargar(""); }}
                className="p-2.5 text-gray-500 hover:text-gray-700 bg-white border border-gray-200 hover:border-gray-300 rounded-xl transition-colors">
                <X size={16}/>
              </button>
            )}
          </form>

          <button onClick={() => setModal("nuevo")}
            className="flex items-center gap-1.5 px-4 py-2.5 text-sm font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl transition-colors shadow-sm">
            <Plus size={16}/> Nuevo Cliente
          </button>

          <button onClick={() => cargar(buscar)}
            className="flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium text-gray-600 bg-white hover:bg-gray-50 border border-gray-200 rounded-xl transition-colors">
            <RefreshCw size={15}/> Actualizar
          </button>
        </div>

        {/* Tabla */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          {loading ? (
            <div className="flex flex-col items-center justify-center py-16 text-gray-400">
              <Loader2 size={28} className="animate-spin mb-3 text-blue-500"/>
              <p className="text-sm">Consultando servicio SOAP...</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="bg-slate-50 border-b border-gray-100">
                    {["ID","Documento","Nombre Completo","Teléfono","Email","Tipo","Estado","Acciones"]
                      .map(h => (
                        <th key={h} className="px-4 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wide">
                          {h}
                        </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {clientes.length === 0 ? (
                    <tr>
                      <td colSpan={8} className="px-4 py-14 text-center">
                        <User size={32} className="mx-auto mb-2 text-gray-300"/>
                        <p className="text-sm text-gray-400">No se encontraron clientes.</p>
                      </td>
                    </tr>
                  ) : clientes.map((c, i) => (
                    <tr key={c.idCliente}
                        className={`hover:bg-blue-50/40 transition-colors ${i % 2 === 0 ? "bg-white" : "bg-slate-50/50"}`}>
                      <td className="px-4 py-3 text-sm text-gray-500 font-mono">{c.idCliente}</td>
                      <td className="px-4 py-3 text-sm">
                        <DocBadge tipo={c.tipoDocumento}/>
                        <span className="text-gray-700 font-medium">{c.nroDocumento}</span>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-800 font-medium">
                        {c.nombres} {c.apellidoPaterno} {c.apellidoMaterno}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600">{c.telefono}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">{c.email}</td>
                      <td className="px-4 py-3 text-sm">
                        <span className="flex items-center gap-1 text-gray-600">
                          {c.tipoCliente === "JURIDICO"
                            ? <Building2 size={13} className="text-purple-500"/>
                            : <User size={13} className="text-blue-400"/>}
                          {c.tipoCliente}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <EstadoBadge estado={c.estado}/>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-1.5">
                          {/* Editar */}
                          <button onClick={() => setModal(c)}
                            className="p-1.5 text-blue-600 hover:bg-blue-100 rounded-lg transition-colors" title="Editar">
                            <Pencil size={15}/>
                          </button>
                          {/* Bloquear / Desbloquear */}
                          {c.estado === "ACTIVO"
                            ? <button onClick={() => cambiarEstado(c.idCliente, "BLOQUEADO")}
                                className="p-1.5 text-amber-600 hover:bg-amber-100 rounded-lg transition-colors" title="Bloquear">
                                <Lock size={15}/>
                              </button>
                            : <button onClick={() => cambiarEstado(c.idCliente, "ACTIVO")}
                                className="p-1.5 text-emerald-600 hover:bg-emerald-100 rounded-lg transition-colors" title="Activar">
                                <Unlock size={15}/>
                              </button>}
                          {/* Baja lógica */}
                          <button onClick={() => eliminar(c.idCliente)}
                            className="p-1.5 text-red-500 hover:bg-red-100 rounded-lg transition-colors" title="Dar de baja">
                            <Trash2 size={15}/>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* Footer tabla */}
          <div className="px-4 py-2.5 bg-slate-50 border-t border-gray-100 flex items-center justify-between">
            <p className="text-xs text-gray-400">{clientes.length} registros</p>
            <p className="text-xs text-gray-400">SOAP → Express → React</p>
          </div>
        </div>
      </main>

      {/* Modal */}
      {modal && (
        <ClienteModal
          cliente={modal === "nuevo" ? null : modal}
          onClose={() => setModal(null)}
          onSaved={onSaved}
        />
      )}
    </div>
  );
}