import { Outlet } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import Sidebar from "./Sidebar";
import Topbar from "./Topbar";

export default function AppLayout() {
  return (
    <div className="flex min-h-screen bg-base-900">
      <Sidebar />

      <div className="flex-1 min-w-0 flex flex-col">
        <Topbar />
        <main className="flex-1 max-w-7xl mx-auto w-full px-4 sm:px-6 lg:px-8 py-6 lg:py-8">
          <Outlet />
        </main>
      </div>

      <Toaster
        position="top-right"
        toastOptions={{
          style: {
            background: "var(--c-base-700)",
            color: "var(--c-ink-100)",
            border: "1px solid var(--c-base-600)",
            borderRadius: "12px",
            fontSize: "14px",
            padding: "12px 14px",
          },
          success: { iconTheme: { primary: "#10B981", secondary: "#1E293B" } },
          error: { iconTheme: { primary: "#EF4444", secondary: "#1E293B" } },
        }}
      />
    </div>
  );
}
