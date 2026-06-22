// Signature element del proyecto: una barra-semáforo de stock a la izquierda de cada fila.
// Verde = stock saludable, ámbar = cerca del mínimo, rojo = crítico o agotado.
export function stockEstado(actual, minimo) {
  if (actual == null) return "neutral";
  if (actual <= 0) return "agotado";
  if (actual <= minimo) return "critico";
  if (actual <= minimo * 1.5) return "bajo";
  return "ok";
}

const estadoColor = {
  ok: "bg-ok",
  bajo: "bg-warn",
  critico: "bg-danger",
  agotado: "bg-danger",
  neutral: "bg-base-500",
};

export function StockDot({ actual, minimo }) {
  const estado = stockEstado(actual, minimo);
  return (
    <span className="relative flex h-2.5 w-2.5 shrink-0">
      {(estado === "critico" || estado === "agotado") && (
        <span className={`absolute inline-flex h-full w-full rounded-full ${estadoColor[estado]} opacity-60 animate-pulseDot`} />
      )}
      <span className={`relative inline-flex rounded-full h-2.5 w-2.5 ${estadoColor[estado]}`} />
    </span>
  );
}

export function StockBarRow({ actual, minimo, children }) {
  const estado = stockEstado(actual, minimo);
  return (
    <div className="relative">
      <span className={`absolute left-0 top-0 bottom-0 w-[3px] rounded-r-full ${estadoColor[estado]}`} />
      {children}
    </div>
  );
}
