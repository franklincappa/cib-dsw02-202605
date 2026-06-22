export function Field({ label, error, children, hint }) {
  return (
    <label className="block mb-4">
      <span className="block text-sm font-medium text-ink-300 mb-1.5">{label}</span>
      {children}
      {hint && !error && <span className="block text-xs text-ink-600 mt-1">{hint}</span>}
      {error && <span className="block text-xs text-danger mt-1">{error}</span>}
    </label>
  );
}

export function Input({ error, className = "", ...props }) {
  return (
    <input
      className={`w-full bg-base-800 border ${
        error ? "border-danger" : "border-base-600"
      } rounded-lg px-3.5 py-2.5 text-sm text-ink-100 placeholder:text-ink-600 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40 transition-colors ${className}`}
      {...props}
    />
  );
}

export function Select({ error, className = "", children, ...props }) {
  return (
    <select
      className={`w-full bg-base-800 border ${
        error ? "border-danger" : "border-base-600"
      } rounded-lg px-3.5 py-2.5 text-sm text-ink-100 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40 transition-colors appearance-none ${className}`}
      {...props}
    >
      {children}
    </select>
  );
}

export function Textarea({ error, className = "", ...props }) {
  return (
    <textarea
      className={`w-full bg-base-800 border ${
        error ? "border-danger" : "border-base-600"
      } rounded-lg px-3.5 py-2.5 text-sm text-ink-100 placeholder:text-ink-600 outline-none focus:border-accent focus:ring-1 focus:ring-accent/40 transition-colors resize-none ${className}`}
      {...props}
    />
  );
}
