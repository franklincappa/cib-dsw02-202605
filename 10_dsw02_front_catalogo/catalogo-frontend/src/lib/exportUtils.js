// Exporta un arreglo de objetos a un archivo .csv que Excel abre nativamente.
// Se usa BOM UTF-8 para que tildes/ñ se vean correctas al abrir en Excel (Windows).
export function exportToExcel(rows, columns, filename = "listado") {
  if (!rows.length) return;

  const headers = columns.map((c) => c.label);
  const escape = (val) => {
    const str = val === null || val === undefined ? "" : String(val);
    // Si contiene coma, comillas o salto de línea, se envuelve en comillas dobles
    if (/[",\n;]/.test(str)) {
      return `"${str.replace(/"/g, '""')}"`;
    }
    return str;
  };

  const lines = [
    headers.map(escape).join(";"),
    ...rows.map((row) => columns.map((c) => escape(c.value(row))).join(";")),
  ];

  // BOM UTF-8 al inicio para que Excel detecte la codificación correctamente
  const csvContent = "\uFEFF" + lines.join("\r\n");
  const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });

  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `${filename}-${new Date().toISOString().slice(0, 10)}.csv`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}

// Abre una ventana de impresión limpia (sin sidebar/botones) con los datos como tabla HTML.
// El usuario elige "Guardar como PDF" en el diálogo de impresión del navegador.
export function printListAsPdf({ title, subtitle, columns, rows }) {
  const win = window.open("", "_blank", "width=900,height=700");
  if (!win) {
    alert("El navegador bloqueó la ventana de impresión. Permite pop-ups para este sitio.");
    return;
  }

  const theadHtml = columns.map((c) => `<th>${c.label}</th>`).join("");
  const tbodyHtml = rows
    .map(
      (row) =>
        `<tr>${columns.map((c) => `<td>${c.value(row) ?? "-"}</td>`).join("")}</tr>`
    )
    .join("");

  win.document.write(`
    <html>
      <head>
        <title>${title}</title>
        <meta charset="utf-8" />
        <style>
          * { box-sizing: border-box; }
          body {
            font-family: -apple-system, "Segoe UI", Arial, sans-serif;
            color: #0F172A;
            padding: 32px 40px;
          }
          .brand {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 4px;
          }
          .brand-dot {
            width: 10px; height: 10px; border-radius: 50%;
            background: #06B6D4;
          }
          .brand-name {
            font-weight: 700;
            font-size: 13px;
            letter-spacing: 0.04em;
            color: #475569;
            text-transform: uppercase;
          }
          h1 {
            font-size: 20px;
            margin: 4px 0 2px;
          }
          .subtitle {
            color: #64748B;
            font-size: 13px;
            margin-bottom: 20px;
          }
          table {
            width: 100%;
            border-collapse: collapse;
            font-size: 12px;
          }
          th {
            text-align: left;
            background: #0F172A;
            color: #fff;
            padding: 8px 10px;
            font-weight: 600;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 0.03em;
          }
          td {
            padding: 7px 10px;
            border-bottom: 1px solid #E2E8F0;
          }
          tr:nth-child(even) td {
            background: #F8FAFC;
          }
          .footer {
            margin-top: 24px;
            font-size: 10px;
            color: #94A3B8;
            text-align: right;
          }
          @media print {
            body { padding: 12px 16px; }
            .no-print { display: none; }
          }
        </style>
      </head>
      <body>
        <div class="brand">
          <span class="brand-dot"></span>
          <span class="brand-name">VentasTech · Catálogo</span>
        </div>
        <h1>${title}</h1>
        <p class="subtitle">${subtitle ?? ""}</p>
        <table>
          <thead><tr>${theadHtml}</tr></thead>
          <tbody>${tbodyHtml}</tbody>
        </table>
        <div class="footer">
          Generado el ${new Date().toLocaleString("es-PE")} · ${rows.length} registro${rows.length !== 1 ? "s" : ""}
        </div>
      </body>
    </html>
  `);
  win.document.close();

  // Espera a que el documento renderice antes de invocar el diálogo de impresión
  win.onload = () => {
    win.focus();
    win.print();
  };
  // Fallback por si onload no dispara en algunos navegadores
  setTimeout(() => {
    win.focus();
    win.print();
  }, 400);
}
