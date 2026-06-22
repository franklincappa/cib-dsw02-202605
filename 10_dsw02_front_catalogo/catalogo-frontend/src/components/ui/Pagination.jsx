import { IconChevronLeft, IconChevronRight, IconChevronsLeft, IconChevronsRight } from "@tabler/icons-react";

const PAGE_SIZE_OPTIONS = [5, 10, 20, 50];

export default function Pagination({
  page,
  pageSize,
  totalItems,
  onPageChange,
  onPageSizeChange,
}) {
  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const from = totalItems === 0 ? 0 : (page - 1) * pageSize + 1;
  const to = Math.min(page * pageSize, totalItems);

  const goTo = (p) => onPageChange(Math.min(Math.max(1, p), totalPages));

  return (
    <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-3.5 border-t border-base-600">
      <div className="flex items-center gap-2 text-sm text-ink-500">
        <span>Mostrar</span>
        <select
          value={pageSize}
          onChange={(e) => {
            onPageSizeChange(Number(e.target.value));
            onPageChange(1);
          }}
          className="bg-base-800 border border-base-600 rounded-lg px-2.5 py-1.5 text-sm text-ink-100 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40"
        >
          {PAGE_SIZE_OPTIONS.map((n) => (
            <option key={n} value={n}>{n}</option>
          ))}
        </select>
        <span className="hidden sm:inline">
          de {totalItems} registro{totalItems !== 1 ? "s" : ""}
        </span>
      </div>

      <div className="flex items-center gap-3">
        <span className="text-sm text-ink-500 tabular-nums hidden sm:inline">
          {from}–{to} de {totalItems}
        </span>

        <div className="flex items-center gap-1">
          <button
            onClick={() => goTo(1)}
            disabled={page === 1}
            className="p-1.5 rounded-lg text-ink-500 hover:text-ink-100 hover:bg-base-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            aria-label="Primera página"
          >
            <IconChevronsLeft size={17} />
          </button>
          <button
            onClick={() => goTo(page - 1)}
            disabled={page === 1}
            className="p-1.5 rounded-lg text-ink-500 hover:text-ink-100 hover:bg-base-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            aria-label="Página anterior"
          >
            <IconChevronLeft size={17} />
          </button>

          <span className="px-3 text-sm text-ink-300 tabular-nums">
            {page} <span className="text-ink-600">/ {totalPages}</span>
          </span>

          <button
            onClick={() => goTo(page + 1)}
            disabled={page === totalPages}
            className="p-1.5 rounded-lg text-ink-500 hover:text-ink-100 hover:bg-base-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            aria-label="Página siguiente"
          >
            <IconChevronRight size={17} />
          </button>
          <button
            onClick={() => goTo(totalPages)}
            disabled={page === totalPages}
            className="p-1.5 rounded-lg text-ink-500 hover:text-ink-100 hover:bg-base-600 disabled:opacity-30 disabled:cursor-not-allowed transition-colors"
            aria-label="Última página"
          >
            <IconChevronsRight size={17} />
          </button>
        </div>
      </div>
    </div>
  );
}
