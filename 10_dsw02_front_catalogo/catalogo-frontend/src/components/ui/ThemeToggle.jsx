import { IconSun, IconMoon } from "@tabler/icons-react";
import { useTheme } from "../../context/ThemeContext";

export default function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();
  const isLight = theme === "light";

  return (
    <button
      onClick={toggleTheme}
      role="switch"
      aria-checked={isLight}
      aria-label="Cambiar tema claro/oscuro"
      title={isLight ? "Cambiar a tema oscuro" : "Cambiar a tema claro"}
      className="relative w-[52px] h-8 rounded-full bg-base-600 border border-base-500 flex items-center px-1 transition-colors shrink-0"
    >
      <span
        className={`absolute top-0.5 left-0.5 w-6 h-6 rounded-full bg-base-800 shadow-md flex items-center justify-center transition-transform duration-200 ${
          isLight ? "translate-x-[22px]" : "translate-x-0"
        }`}
      >
        {isLight ? (
          <IconSun size={14} className="text-amber-500" />
        ) : (
          <IconMoon size={14} className="text-accent" />
        )}
      </span>
    </button>
  );
}
