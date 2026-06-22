import { IconLoader2 } from "@tabler/icons-react";

const variants = {
  primary: "bg-accent hover:bg-accent-dim text-base-900 shadow-glow",
  ghost: "bg-base-600/40 hover:bg-base-600 text-ink-100 border border-base-500",
  danger: "bg-danger/15 hover:bg-danger/25 text-red-300 border border-danger/30",
  subtle: "bg-transparent hover:bg-base-600 text-ink-300",
};

export default function Button({
  children,
  variant = "primary",
  icon: Icon,
  loading = false,
  className = "",
  ...props
}) {
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-medium transition-all active:scale-[0.97] disabled:opacity-50 disabled:cursor-not-allowed ${variants[variant]} ${className}`}
      disabled={loading || props.disabled}
      {...props}
    >
      {loading ? <IconLoader2 size={17} className="animate-spin" /> : Icon && <Icon size={17} />}
      {children}
    </button>
  );
}
