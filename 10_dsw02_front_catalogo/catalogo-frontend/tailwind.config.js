/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ["selector", ":root:not(.light)"],
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        base: {
          900: "var(--c-base-900)",
          800: "var(--c-base-800)",
          700: "var(--c-base-700)",
          600: "var(--c-base-600)",
          500: "var(--c-base-500)",
        },
        ink: {
          100: "var(--c-ink-100)",
          300: "var(--c-ink-300)",
          500: "var(--c-ink-500)",
          600: "var(--c-ink-600)",
        },
        accent: {
          DEFAULT: "#06B6D4",
          dim: "#0891B2",
          glow: "#22D3EE",
        },
        ok: "#10B981",
        warn: "#F59E0B",
        danger: "#EF4444",
        dangerDim: "#DC2626",
      },
      fontFamily: {
        display: ["Sora", "ui-sans-serif", "system-ui"],
        body: ["Inter", "ui-sans-serif", "system-ui"],
      },
      boxShadow: {
        glow: "0 0 0 1px rgba(6,182,212,0.25), 0 8px 24px -8px rgba(6,182,212,0.35)",
        card: "0 1px 0 0 rgba(255,255,255,0.03) inset, 0 8px 24px -12px rgba(0,0,0,0.5)",
      },
      keyframes: {
        slideUp: {
          "0%": { opacity: 0, transform: "translateY(12px) scale(0.98)" },
          "100%": { opacity: 1, transform: "translateY(0) scale(1)" },
        },
        fadeIn: {
          "0%": { opacity: 0 },
          "100%": { opacity: 1 },
        },
        pulseDot: {
          "0%, 100%": { opacity: 1 },
          "50%": { opacity: 0.4 },
        },
      },
      animation: {
        slideUp: "slideUp 0.22s cubic-bezier(0.16,1,0.3,1)",
        fadeIn: "fadeIn 0.18s ease-out",
        pulseDot: "pulseDot 2s ease-in-out infinite",
      },
    },
  },
  plugins: [],
};
