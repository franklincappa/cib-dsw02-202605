export default function StatusBadge({ activo }) {
  return activo ? (
    <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-ok/10 text-emerald-400 border border-ok/25">
      <span className="w-1.5 h-1.5 rounded-full bg-ok" />
      Activo
    </span>
  ) : (
    <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-base-600/50 text-ink-500 border border-base-500">
      <span className="w-1.5 h-1.5 rounded-full bg-ink-600" />
      Inactivo
    </span>
  );
}
