import { useEffect } from "react";
import { IconX } from "@tabler/icons-react";

export default function Modal({ open, onClose, title, subtitle, children, width = "max-w-lg" }) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === "Escape" && onClose();
    document.addEventListener("keydown", onKey);
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = "";
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center p-4 animate-fadeIn"
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
    >
      <div
        className="absolute inset-0 bg-base-900/70 backdrop-blur-sm"
        onClick={onClose}
      />
      <div
        className={`relative w-full ${width} bg-base-700 border border-base-600 rounded-2xl shadow-card animate-slideUp max-h-[88vh] overflow-y-auto`}
      >
        <div className="flex items-start justify-between px-6 pt-6 pb-4 border-b border-base-600 sticky top-0 bg-base-700 rounded-t-2xl">
          <div>
            <h2 id="modal-title" className="font-display text-lg font-semibold text-ink-100">
              {title}
            </h2>
            {subtitle && <p className="text-sm text-ink-500 mt-0.5">{subtitle}</p>}
          </div>
          <button
            onClick={onClose}
            aria-label="Cerrar"
            className="text-ink-500 hover:text-ink-100 hover:bg-base-600 rounded-lg p-1.5 transition-colors"
          >
            <IconX size={20} />
          </button>
        </div>
        <div className="px-6 py-5">{children}</div>
      </div>
    </div>
  );
}
