import { NavLink } from "react-router-dom";
import {
  IconBoxSeam,
  IconCategory,
  IconTag,
  IconStack2,
  IconSettings,
  IconLogout,
  IconChevronDown,
  IconLayoutGrid,
} from "@tabler/icons-react";
import { useState, useRef, useEffect } from "react";
import { useSidebar } from "../../context/SidebarContext";

const navItems = [
  { to: "/productos", label: "Productos", icon: IconBoxSeam },
  { to: "/categorias", label: "Categorías", icon: IconCategory },
  { to: "/marcas", label: "Marcas", icon: IconTag },
  { to: "/inventario", label: "Inventario", icon: IconStack2 },
];

export default function Sidebar() {
  const { collapsed, mobileOpen, setMobileOpen } = useSidebar();
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const handler = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) setUserMenuOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const close = () => setMobileOpen(false);

  return (
    <>
      {mobileOpen && (
        <div className="fixed inset-0 bg-black/60 z-30 lg:hidden" onClick={close} />
      )}
      <aside
        style={{ background: "var(--sidebar-bg)", borderColor: "var(--sidebar-border)" }}
        className={`fixed lg:sticky top-0 left-0 h-screen flex flex-col z-40 border-r transition-[width,transform] duration-200 shrink-0 ${
          collapsed ? "lg:w-[76px]" : "lg:w-72"
        } w-72 ${mobileOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"}`}
      >
        {/* Brand */}
        <div
          style={{ borderColor: "var(--sidebar-border)" }}
          className={`h-16 flex items-center gap-3 border-b shrink-0 ${
            collapsed ? "lg:justify-center lg:px-0 px-6" : "px-6"
          }`}
        >
          <div className="w-9 h-9 rounded-xl bg-white/15 border border-white/25 flex items-center justify-center shrink-0">
            <IconLayoutGrid size={20} style={{ color: "var(--sidebar-ink)" }} />
          </div>
          <div className={collapsed ? "lg:hidden" : ""}>
            <p
              style={{ color: "var(--sidebar-ink)" }}
              className="font-display font-bold leading-tight"
            >
              VentasTech
            </p>
            <p
              style={{ color: "var(--sidebar-ink-dim)" }}
              className="text-[11px] leading-tight tracking-wide uppercase"
            >
              Catálogo
            </p>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 overflow-y-auto px-3 py-5 space-y-1">
          <p
            style={{ color: "var(--sidebar-ink-dim)" }}
            className={`px-3 text-[11px] font-semibold uppercase tracking-wider mb-2 ${
              collapsed ? "lg:hidden" : ""
            }`}
          >
            Catálogo
          </p>
          {navItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={close}
              title={collapsed ? label : undefined}
              className={({ isActive }) =>
                `nav-link group flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-colors relative ${
                  collapsed ? "lg:justify-center" : ""
                } ${isActive ? "nav-link-active" : ""}`
              }
            >
              {({ isActive }) => (
                <>
                  {isActive && !collapsed && (
                    <span
                      className="absolute left-0 top-1.5 bottom-1.5 w-[3px] rounded-full"
                      style={{ background: "var(--sidebar-ink)" }}
                    />
                  )}
                  <Icon size={19} stroke={1.75} className="shrink-0" />
                  <span className={collapsed ? "lg:hidden" : ""}>{label}</span>
                </>
              )}
            </NavLink>
          ))}
        </nav>

        {/* User */}
        <div
          ref={menuRef}
          style={{ borderColor: "var(--sidebar-border)" }}
          className="relative border-t p-3 shrink-0"
        >
          {userMenuOpen && (
            <div className="absolute bottom-full left-3 right-3 mb-2 bg-base-700 border border-base-600 rounded-xl shadow-card overflow-hidden animate-slideUp">
              <button className="w-full flex items-center gap-2.5 px-4 py-3 text-sm text-ink-300 hover:bg-base-600 hover:text-ink-100 transition-colors">
                <IconSettings size={17} />
                Configuración
              </button>
              <button className="w-full flex items-center gap-2.5 px-4 py-3 text-sm text-red-300 hover:bg-danger/15 transition-colors border-t border-base-600">
                <IconLogout size={17} />
                Cerrar sesión
              </button>
            </div>
          )}
          <button
            onClick={() => setUserMenuOpen((v) => !v)}
            title={collapsed ? "Franklin Cappa" : undefined}
            className={`user-btn w-full flex items-center gap-3 px-2 py-2 rounded-xl transition-colors ${
              collapsed ? "lg:justify-center" : ""
            }`}
          >
            <div
              className="w-9 h-9 rounded-full bg-white/20 border border-white/30 flex items-center justify-center text-sm font-display font-bold shrink-0"
              style={{ color: "var(--sidebar-ink)" }}
            >
              FC
            </div>
            <div className={`flex-1 text-left min-w-0 ${collapsed ? "lg:hidden" : ""}`}>
              <p style={{ color: "var(--sidebar-ink)" }} className="text-sm font-medium truncate">
                Franklin Cappa
              </p>
              <p style={{ color: "var(--sidebar-ink-dim)" }} className="text-xs truncate">
                Administrador
              </p>
            </div>
            <IconChevronDown
              size={16}
              style={{ color: "var(--sidebar-ink-dim)" }}
              className={`shrink-0 transition-transform ${userMenuOpen ? "rotate-180" : ""} ${
                collapsed ? "lg:hidden" : ""
              }`}
            />
          </button>
        </div>
      </aside>
    </>
  );
}
