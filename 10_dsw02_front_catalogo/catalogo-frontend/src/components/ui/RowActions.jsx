import { IconPencil, IconTrash, IconDownload, IconPrinter } from "@tabler/icons-react";

const baseBtn =
  "p-2 rounded-lg transition-colors disabled:opacity-30 disabled:cursor-not-allowed";

export default function RowActions({ onEdit, onDelete, onDownload, onPrint }) {
  return (
    <div className="flex items-center justify-end gap-1">
      {onPrint && (
        <button
          onClick={onPrint}
          title="Imprimir"
          aria-label="Imprimir"
          className={`${baseBtn} text-ink-500 hover:text-ink-100 hover:bg-base-600`}
        >
          <IconPrinter size={17} />
        </button>
      )}
      {onDownload && (
        <button
          onClick={onDownload}
          title="Descargar"
          aria-label="Descargar"
          className={`${baseBtn} text-ink-500 hover:text-accent hover:bg-accent/10`}
        >
          <IconDownload size={17} />
        </button>
      )}
      {onEdit && (
        <button
          onClick={onEdit}
          title="Editar"
          aria-label="Editar"
          className={`${baseBtn} text-ink-500 hover:text-accent hover:bg-accent/10`}
        >
          <IconPencil size={17} />
        </button>
      )}
      {onDelete && (
        <button
          onClick={onDelete}
          title="Eliminar"
          aria-label="Eliminar"
          className={`${baseBtn} text-ink-500 hover:text-red-400 hover:bg-danger/10`}
        >
          <IconTrash size={17} />
        </button>
      )}
    </div>
  );
}
