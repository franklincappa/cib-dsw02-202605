import Swal from "sweetalert2";

// Instancia de SweetAlert con el theme oscuro de la app
const swalDark = Swal.mixin({
  background: "#1E293B",
  color: "#F1F5F9",
  confirmButtonColor: "#EF4444",
  cancelButtonColor: "#2A3950",
  buttonsStyling: true,
  customClass: {
    popup: "rounded-2xl border border-base-600 font-body",
    confirmButton: "rounded-lg px-4 py-2 text-sm font-medium",
    cancelButton: "rounded-lg px-4 py-2 text-sm font-medium",
    title: "font-display text-lg",
  },
});

export async function confirmDelete({ title, text, confirmText = "Sí, eliminar" }) {
  const result = await swalDark.fire({
    title: title || "¿Eliminar este registro?",
    text: text || "Esta acción no se puede deshacer.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonText: confirmText,
    cancelButtonText: "Cancelar",
    reverseButtons: true,
    focusCancel: true,
  });
  return result.isConfirmed;
}

export async function confirmAction({ title, text, icon = "question", confirmText = "Confirmar", confirmColor = "#06B6D4" }) {
  const result = await swalDark.fire({
    title,
    text,
    icon,
    showCancelButton: true,
    confirmButtonText: confirmText,
    confirmButtonColor: confirmColor,
    cancelButtonText: "Cancelar",
    reverseButtons: true,
  });
  return result.isConfirmed;
}
