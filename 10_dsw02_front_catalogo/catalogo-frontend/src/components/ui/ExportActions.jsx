import { IconFileSpreadsheet, IconPrinter } from "@tabler/icons-react";

export default function ExportActions({ onExportExcel, onPrintPdf, disabled }) {
  return (
    <div className="flex items-center gap-1.5 bg-base-700 border border-base-600 rounded-lg p-1">
      <button
        onClick={onExportExcel}
        disabled={disabled}
        title="Exportar a Excel"
        className="flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-medium text-ink-400 hover:text-emerald-400 hover:bg-ok/10 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
      >
        <IconFileSpreadsheet size={16} />
        <span className="hidden sm:inline">Excel</span>
      </button>
      <span className="w-px h-4 bg-base-600" />
      <button
        onClick={onPrintPdf}
        disabled={disabled}
        title="Imprimir / Exportar PDF"
        className="flex items-center gap-1.5 px-3 py-1.5 rounded-md text-xs font-medium text-ink-400 hover:text-accent hover:bg-accent/10 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
      >
        <IconPrinter size={16} />
        <span className="hidden sm:inline">PDF</span>
      </button>
    </div>
  );
}
