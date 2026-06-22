import { IconMenu2, IconLayoutSidebarLeftCollapse, IconLayoutSidebarLeftExpand } from "@tabler/icons-react";
import ThemeToggle from "../ui/ThemeToggle";
import { useSidebar } from "../../context/SidebarContext";

export default function Topbar() {
  const { collapsed, toggleCollapsed, setMobileOpen } = useSidebar();

  return (
    <header className="h-16 flex items-center justify-between px-4 sm:px-6 lg:px-8 border-b border-base-600 bg-base-800/60 backdrop-blur-sm sticky top-0 z-20">
      <div className="flex items-center gap-3">
        {/* Abrir sidebar en móvil */}
        <button
          onClick={() => setMobileOpen(true)}
          className="lg:hidden text-ink-300 hover:text-ink-100"
          aria-label="Abrir menú"
        >
          <IconMenu2 size={22} />
        </button>

        {/* Colapsar/expandir sidebar en desktop */}
        <button
          onClick={toggleCollapsed}
          className="hidden lg:flex items-center justify-center text-ink-500 hover:text-ink-100 hover:bg-base-600 rounded-lg p-1.5 transition-colors"
          aria-label={collapsed ? "Expandir menú" : "Colapsar menú"}
          title={collapsed ? "Expandir menú" : "Colapsar menú"}
        >
          {collapsed ? (
            <IconLayoutSidebarLeftExpand size={20} />
          ) : (
            <IconLayoutSidebarLeftCollapse size={20} />
          )}
        </button>
      </div>

      <div className="flex items-center gap-4">
        <ThemeToggle />
      </div>
    </header>
  );
}
