import { IconPlus } from "@tabler/icons-react";
import Button from "../ui/Button";

export default function PageHeader({ title, subtitle, onAdd, addLabel = "Agregar" }) {
  return (
    <div className="flex items-start justify-between gap-4 mb-6">
      <div>
        <h1 className="font-display text-2xl font-bold text-ink-100">{title}</h1>
        {subtitle && <p className="text-sm text-ink-500 mt-1">{subtitle}</p>}
      </div>

      {onAdd && (
        <>
          {/* Botón clásico - visible desde tablet hacia arriba */}
          <Button onClick={onAdd} icon={IconPlus} className="hidden sm:flex shrink-0">
            {addLabel}
          </Button>

          {/* FAB - solo en móvil, flotante abajo a la derecha */}
          <button
            onClick={onAdd}
            aria-label={addLabel}
            className="sm:hidden fixed bottom-6 right-6 z-30 w-14 h-14 rounded-full bg-accent text-base-900 shadow-glow flex items-center justify-center active:scale-95 transition-transform"
          >
            <IconPlus size={26} />
          </button>
        </>
      )}
    </div>
  );
}
