import http from "k6/http";
import { check, sleep, group } from "k6";

// ─────────────────────────────────────────────
// Rampa de usuarios:
//  0  →  2 min  : 10 usuarios
//  2  →  3 min  : 20 usuarios
//  3  →  4 min  : 50 usuarios
//  4 min        : fin
// ─────────────────────────────────────────────
export const options = {
  stages: [
    { duration: "2m", target: 10 },  // 10 usuarios por 2 minutos
    { duration: "1m", target: 20 },  // sube a 20 por 1 minuto
    { duration: "1m", target: 50 },  // sube a 50 por 1 minuto
    { duration: "10s", target: 0  }, // baja gradual al terminar
  ],
  thresholds: {
    http_req_duration: ["p(95)<3000"], // 95% de requests bajo 3s
    http_req_failed:   ["rate<0.05"],  // menos del 5% de errores
  },
};

const BASE_URL = "http://localhost:8081/api/v1";
const HEADERS  = { "Content-Type": "application/json" };

// IDs creados en esta sesión para poder hacer DELETE
const idsCreados = [];

// ─────────────────────────────────────────────
// Flujo principal
// ─────────────────────────────────────────────
export default function () {

  // ── GET: Listar productos activos ──────────
  group("GET /productos/activos", () => {
    const res = http.get(`${BASE_URL}/productos/activos`, { headers: HEADERS });
    check(res, {
      "status 200":   (r) => r.status === 200,
      "success true": (r) => JSON.parse(r.body).success === true,
    });
    sleep(1);
  });

  // ── GET: Buscar por nombre ─────────────────
  group("GET /productos/buscar", () => {
    const res = http.get(`${BASE_URL}/productos/buscar?nombre=Dell`, { headers: HEADERS });
    check(res, {
      "status 200":   (r) => r.status === 200,
      "success true": (r) => JSON.parse(r.body).success === true,
    });
    sleep(1);
  });

  // ── POST: Crear producto ───────────────────
  group("POST /productos", () => {
    const sku     = `SKU-K6-${Date.now()}-${Math.floor(Math.random() * 9999)}`;
    const payload = JSON.stringify({
      categoriaId:  1,
      marcaId:      1,
      codigoSku:    sku,
      nombre:       `Producto K6 ${sku}`,
      descripcion:  "Creado por stress test k6",
      precio:       199.99,
      activo:       true,
      stockActual:  10,
      stockMinimo:  2,
      stockMaximo:  100,
    });

    const res = http.post(`${BASE_URL}/productos`, payload, { headers: HEADERS });
    const ok  = check(res, {
      "status 201": (r) => r.status === 201,
      "tiene id":   (r) => {
        try { return JSON.parse(r.body).data.id > 0; }
        catch { return false; }
      },
    });

    // Guarda el id para el DELETE
    if (ok) {
      try {
        const id = JSON.parse(res.body).data.id;
        idsCreados.push(id);
      } catch (_) {}
    }
    sleep(1);
  });

  // ── DELETE: Eliminar el último creado ──────
  group("DELETE /productos/{id}", () => {
    if (idsCreados.length === 0) {
      sleep(1);
      return;
    }
    // Toma el último id creado por este VU
    const id  = idsCreados.pop();
    const res = http.del(`${BASE_URL}/productos/${id}`, null, { headers: HEADERS });
    check(res, {
      "status 200 o 404": (r) => r.status === 200 || r.status === 404,
    });
    sleep(1);
  });

  sleep(0.5);
}


import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.2/index.js";

export function handleSummary(data) {
  return {
    "reporte-stress.html": htmlReport(data),           // reporte visual HTML
    "reporte-stress.json": JSON.stringify(data, null, 2), // datos en JSON
    stdout: textSummary(data, { indent: " ", enableColors: true }), // consola
  };
}

//npm install -g k6-html-reporter

//k6 run stress-basic-catalogo-v2.js
