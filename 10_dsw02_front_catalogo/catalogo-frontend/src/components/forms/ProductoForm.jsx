import { useEffect, useState } from "react";
import { Field, Input, Select, Textarea } from "../ui/Field";
import Button from "../ui/Button";

const emptyForm = {
  categoriaId: "",
  marcaId: "",
  codigoSku: "",
  nombre: "",
  descripcion: "",
  precio: "",
  activo: true,
  stockActual: 0,
  stockMinimo: 0,
  stockMaximo: 100,
};

export default function ProductoForm({ initialData, categorias, marcas, onSubmit, onCancel, saving }) {
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});
  const isEdit = Boolean(initialData?.id);

  useEffect(() => {
    if (initialData) {
      setForm({
        categoriaId: initialData.categoriaId ?? "",
        marcaId: initialData.marcaId ?? "",
        codigoSku: initialData.codigoSku ?? "",
        nombre: initialData.nombre ?? "",
        descripcion: initialData.descripcion ?? "",
        precio: initialData.precio ?? "",
        activo: initialData.activo ?? true,
        stockActual: initialData.stockActual ?? 0,
        stockMinimo: initialData.stockMinimo ?? 0,
        stockMaximo: initialData.stockMaximo ?? 100,
      });
    } else {
      setForm(emptyForm);
    }
    setErrors({});
  }, [initialData]);

  const set = (key) => (e) => {
    const value = e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setForm((f) => ({ ...f, [key]: value }));
  };

  const validate = () => {
    const err = {};
    if (!form.codigoSku.trim()) err.codigoSku = "El SKU es obligatorio";
    if (!form.nombre.trim()) err.nombre = "El nombre es obligatorio";
    if (!form.precio || Number(form.precio) <= 0) err.precio = "Ingresa un precio mayor a 0";
    setErrors(err);
    return Object.keys(err).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    onSubmit({
      ...form,
      categoriaId: form.categoriaId ? Number(form.categoriaId) : null,
      marcaId: form.marcaId ? Number(form.marcaId) : null,
      precio: Number(form.precio),
      stockActual: Number(form.stockActual),
      stockMinimo: Number(form.stockMinimo),
      stockMaximo: Number(form.stockMaximo),
    });
  };

  return (
    <form onSubmit={handleSubmit}>
      <div className="grid grid-cols-2 gap-x-4">
        <div className="col-span-2 sm:col-span-1">
          <Field label="Código SKU" error={errors.codigoSku}>
            <Input
              placeholder="LAP-DELL-001"
              value={form.codigoSku}
              onChange={set("codigoSku")}
              disabled={isEdit}
            />
          </Field>
        </div>
        <div className="col-span-2 sm:col-span-1">
          <Field label="Precio (S/)" error={errors.precio}>
            <Input
              type="number"
              step="0.01"
              placeholder="0.00"
              value={form.precio}
              onChange={set("precio")}
            />
          </Field>
        </div>

        <div className="col-span-2">
          <Field label="Nombre del producto" error={errors.nombre}>
            <Input
              placeholder="Laptop Dell Inspiron 15"
              value={form.nombre}
              onChange={set("nombre")}
            />
          </Field>
        </div>

        <div className="col-span-2">
          <Field label="Descripción" hint="Opcional">
            <Textarea
              rows={2}
              placeholder="Detalles técnicos del producto..."
              value={form.descripcion}
              onChange={set("descripcion")}
            />
          </Field>
        </div>

        <div className="col-span-2 sm:col-span-1">
          <Field label="Categoría">
            <Select value={form.categoriaId} onChange={set("categoriaId")}>
              <option value="">Sin categoría</option>
              {categorias.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre}</option>
              ))}
            </Select>
          </Field>
        </div>
        <div className="col-span-2 sm:col-span-1">
          <Field label="Marca">
            <Select value={form.marcaId} onChange={set("marcaId")}>
              <option value="">Sin marca</option>
              {marcas.map((m) => (
                <option key={m.id} value={m.id}>{m.nombre}</option>
              ))}
            </Select>
          </Field>
        </div>

        {!isEdit && (
          <>
            <div className="col-span-2 pt-1 pb-2">
              <p className="text-xs font-semibold text-ink-600 uppercase tracking-wider">
                Inventario inicial
              </p>
            </div>
            <div className="col-span-1">
              <Field label="Stock inicial">
                <Input type="number" min="0" value={form.stockActual} onChange={set("stockActual")} />
              </Field>
            </div>
            <div className="col-span-1">
              <Field label="Stock mínimo">
                <Input type="number" min="0" value={form.stockMinimo} onChange={set("stockMinimo")} />
              </Field>
            </div>
          </>
        )}

        <div className="col-span-2 flex items-center gap-2.5 mt-1 mb-2">
          <input
            type="checkbox"
            id="activo"
            checked={form.activo}
            onChange={set("activo")}
            className="w-4 h-4 rounded accent-cyan-500 bg-base-800 border-base-600"
          />
          <label htmlFor="activo" className="text-sm text-ink-300">
            Producto activo y visible en el catálogo
          </label>
        </div>
      </div>

      <div className="flex justify-end gap-3 pt-3 border-t border-base-600 mt-2">
        <Button type="button" variant="ghost" onClick={onCancel}>
          Cancelar
        </Button>
        <Button type="submit" loading={saving}>
          {isEdit ? "Guardar cambios" : "Crear producto"}
        </Button>
      </div>
    </form>
  );
}
