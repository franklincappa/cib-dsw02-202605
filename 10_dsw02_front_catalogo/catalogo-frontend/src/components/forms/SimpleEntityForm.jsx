import { useEffect, useState } from "react";
import { Field, Input, Textarea } from "../ui/Field";
import Button from "../ui/Button";

export default function SimpleEntityForm({
  initialData,
  onSubmit,
  onCancel,
  saving,
  extraField, // { key, label, placeholder } ej: paisOrigen para Marca
  entityLabel = "registro",
}) {
  const [form, setForm] = useState({ nombre: "", descripcion: "", activo: true, extra: "" });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialData) {
      setForm({
        nombre: initialData.nombre ?? "",
        descripcion: initialData.descripcion ?? "",
        activo: initialData.activo ?? true,
        extra: extraField ? initialData[extraField.key] ?? "" : "",
      });
    } else {
      setForm({ nombre: "", descripcion: "", activo: true, extra: "" });
    }
    setErrors({});
  }, [initialData, extraField]);

  const set = (key) => (e) => {
    const value = e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setForm((f) => ({ ...f, [key]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!form.nombre.trim()) {
      setErrors({ nombre: "El nombre es obligatorio" });
      return;
    }
    const payload = { nombre: form.nombre, descripcion: form.descripcion, activo: form.activo };
    if (extraField) payload[extraField.key] = form.extra;
    onSubmit(payload);
  };

  return (
    <form onSubmit={handleSubmit}>
      <Field label="Nombre" error={errors.nombre}>
        <Input value={form.nombre} onChange={set("nombre")} placeholder={`Nombre del ${entityLabel}`} />
      </Field>

      {extraField && (
        <Field label={extraField.label}>
          <Input value={form.extra} onChange={set("extra")} placeholder={extraField.placeholder} />
        </Field>
      )}

      <Field label="Descripción" hint="Opcional">
        <Textarea rows={3} value={form.descripcion} onChange={set("descripcion")} placeholder="Breve descripción..." />
      </Field>

      <div className="flex items-center gap-2.5 mb-2">
        <input
          type="checkbox"
          id="activo-entity"
          checked={form.activo}
          onChange={set("activo")}
          className="w-4 h-4 rounded accent-cyan-500 bg-base-800 border-base-600"
        />
        <label htmlFor="activo-entity" className="text-sm text-ink-300">Activo</label>
      </div>

      <div className="flex justify-end gap-3 pt-3 border-t border-base-600 mt-2">
        <Button type="button" variant="ghost" onClick={onCancel}>Cancelar</Button>
        <Button type="submit" loading={saving}>{initialData ? "Guardar cambios" : "Crear"}</Button>
      </div>
    </form>
  );
}
