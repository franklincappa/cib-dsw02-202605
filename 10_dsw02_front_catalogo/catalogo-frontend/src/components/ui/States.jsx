import { IconMoodEmpty, IconAlertTriangle, IconRefresh } from "@tabler/icons-react";
import Button from "./Button";

export function TableSkeleton({ rows = 6, cols = 5 }) {
  return (
    <div className="divide-y divide-base-600">
      {Array.from({ length: rows }).map((_, r) => (
        <div key={r} className="flex items-center gap-4 px-5 py-4">
          {Array.from({ length: cols }).map((_, c) => (
            <div
              key={c}
              className="h-3.5 bg-base-600/60 rounded-full animate-pulse"
              style={{ width: c === 0 ? "28%" : `${60 + Math.random() * 30}px` }}
            />
          ))}
        </div>
      ))}
    </div>
  );
}

export function EmptyState({ title = "Nada por aquí todavía", text, actionLabel, onAction }) {
  return (
    <div className="flex flex-col items-center justify-center text-center py-16 px-6">
      <div className="w-14 h-14 rounded-2xl bg-base-600/50 flex items-center justify-center mb-4">
        <IconMoodEmpty size={28} className="text-ink-600" />
      </div>
      <p className="font-display font-semibold text-ink-100 mb-1">{title}</p>
      {text && <p className="text-sm text-ink-500 max-w-sm mb-5">{text}</p>}
      {actionLabel && onAction && (
        <Button variant="ghost" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
}

export function ErrorState({ message, onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center text-center py-16 px-6">
      <div className="w-14 h-14 rounded-2xl bg-danger/10 flex items-center justify-center mb-4">
        <IconAlertTriangle size={28} className="text-danger" />
      </div>
      <p className="font-display font-semibold text-ink-100 mb-1">No se pudo cargar la información</p>
      <p className="text-sm text-ink-500 max-w-sm mb-5">{message}</p>
      {onRetry && (
        <Button variant="ghost" icon={IconRefresh} onClick={onRetry}>
          Reintentar
        </Button>
      )}
    </div>
  );
}
