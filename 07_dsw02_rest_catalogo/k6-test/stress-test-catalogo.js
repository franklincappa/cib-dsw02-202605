import http from "k6/http";
import { check, sleep, group } from "k6";
import { Counter, Rate, Trend } from "k6/metrics";
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.2/index.js";


// Métricas personalizadas
// ─────────────────────────────────────────────
const errores         = new Counter("errores_totales");
const tasaExito       = new Rate("tasa_exito");
const duracionListar  = new Trend("duracion_listar_productos");
const duracionBuscar  = new Trend("duracion_buscar_producto");
const duracionCrear   = new Trend("duracion_crear_producto");


// Configuración del escenario de stress
// ─────────────────────────────────────────────
export const options = {
  scenarios: {

    // Escenario 1 — Carga sostenida (smoke test inicial)
    carga_base: {
      executor: "constant-vus",
      vus: 5,
      duration: "30s",
      startTime: "0s",
      tags: { escenario: "carga_base" },
    },

    // Escenario 2 — Rampa de carga (carga progresiva)
    rampa_carga: {
      executor: "ramping-vus",
      startTime: "35s",
      stages: [
        { duration: "20s", target: 20  }, // sube a 20 usuarios
        { duration: "30s", target: 50  }, // sube a 50 usuarios
        { duration: "20s", target: 50  }, // mantiene 50
        { duration: "20s", target: 0   }, // baja a 0
      ],
      tags: { escenario: "rampa_carga" },
    },

    // Escenario 3 — Stress pico (spike test)
    pico_stress: {
      executor: "ramping-vus",
      startTime: "130s",
      stages: [
        { duration: "10s", target: 100 }, // pico súbito
        { duration: "20s", target: 100 }, // mantiene el pico
        { duration: "10s", target: 0   }, // cae rápido
      ],
      tags: { escenario: "pico_stress" },
    },
  },

  // Umbrales — el test FALLA si se superan
  thresholds: {
    http_req_duration:        ["p(95)<2000"],  // 95% de requests < 2s
    http_req_duration:        ["p(99)<5000"],  // 99% de requests < 5s
    http_req_failed:          ["rate<0.05"],   // menos del 5% de errores
    tasa_exito:               ["rate>0.95"],   // más del 95% exitosos
    duracion_listar_productos:["p(95)<1500"],
    duracion_buscar_producto: ["p(95)<1500"],
    duracion_crear_producto:  ["p(95)<3000"],
  },
};

// ─────────────────────────────────────────────
// Configuración base
// ─────────────────────────────────────────────
const BASE_URL = "http://localhost:8081/api/v1";

const HEADERS = {
  "Content-Type": "application/json",
  Accept: "application/json",
};

// ─────────────────────────────────────────────
// Datos de prueba
// ─────────────────────────────────────────────
const CATEGORIAS_IDS  = [1, 2, 3, 4, 5, 6];
const MARCAS_IDS      = [1, 2, 3, 4, 5, 6, 7];
const NOMBRES_BUSCAR  = ["Dell", "Logitech", "Sony", "Samsung", "Corsair"];
const RANGOS_PRECIO   = [
  { min: 50,   max: 300  },
  { min: 300,  max: 800  },
  { min: 800,  max: 3000 },
];

function randomItem(arr) {
  return arr[Math.floor(Math.random() * arr.length)];
}

function randomSku() {
  const rand = Math.floor(Math.random() * 99999);
  return `SKU-TEST-${rand}`;
}

function productoPayload(categoriaId, marcaId) {
  const sku = randomSku();
  return JSON.stringify({
    categoriaId:  categoriaId,
    marcaId:      marcaId,
    codigoSku:    sku,
    nombre:       `Producto Test ${sku}`,
    descripcion:  "Producto generado por prueba de stress k6",
    precio:       parseFloat((Math.random() * 2000 + 50).toFixed(2)),
    activo:       true,
    stockActual:  Math.floor(Math.random() * 100),
    stockMinimo:  5,
    stockMaximo:  500,
  });
}


// Helpers de request
// ─────────────────────────────────────────────
function verificarRespuesta(res, tag) {
  const ok = check(res, {
    [`[${tag}] status 200 o 201`]: (r) => r.status === 200 || r.status === 201,
    [`[${tag}] tiene body`]:        (r) => r.body && r.body.length > 0,
    [`[${tag}] success=true`]:      (r) => {
      try { return JSON.parse(r.body).success === true; }
      catch { return false; }
    },
  });
  tasaExito.add(ok);
  if (!ok) errores.add(1);
  return ok;
}


// Flujo principal — ejecutado por cada VU
// ─────────────────────────────────────────────
export default function () {

  // ── Grupo 1: Consultas de solo lectura (70% del tráfico real) ──
  group("GET - Listar productos", () => {
    const start = Date.now();
    const res = http.get(`${BASE_URL}/productos`, { headers: HEADERS });
    duracionListar.add(Date.now() - start);
    verificarRespuesta(res, "listar_productos");
    sleep(0.5);
  });

  group("GET - Listar activos", () => {
    const res = http.get(`${BASE_URL}/productos/activos`, { headers: HEADERS });
    verificarRespuesta(res, "listar_activos");
    sleep(0.3);
  });

  group("GET - Obtener producto por ID", () => {
    const id  = Math.floor(Math.random() * 10) + 1;
    const res = http.get(`${BASE_URL}/productos/${id}`, { headers: HEADERS });
    // Acepta 404 también — producto puede no existir
    check(res, {
      "[get_por_id] status válido": (r) => r.status === 200 || r.status === 404,
    });
    sleep(0.2);
  });

  group("GET - Buscar por nombre", () => {
    const start  = Date.now();
    const nombre = randomItem(NOMBRES_BUSCAR);
    const res    = http.get(
      `${BASE_URL}/productos/buscar?nombre=${nombre}`,
      { headers: HEADERS }
    );
    duracionBuscar.add(Date.now() - start);
    verificarRespuesta(res, "buscar_nombre");
    sleep(0.3);
  });

  group("GET - Listar por categoría", () => {
    const catId = randomItem(CATEGORIAS_IDS);
    const res   = http.get(
      `${BASE_URL}/productos/categoria/${catId}`,
      { headers: HEADERS }
    );
    verificarRespuesta(res, "por_categoria");
    sleep(0.3);
  });

  group("GET - Listar por marca", () => {
    const marcaId = randomItem(MARCAS_IDS);
    const res     = http.get(
      `${BASE_URL}/productos/marca/${marcaId}`,
      { headers: HEADERS }
    );
    verificarRespuesta(res, "por_marca");
    sleep(0.3);
  });

  group("GET - Filtrar por rango de precio", () => {
    const rango = randomItem(RANGOS_PRECIO);
    const res   = http.get(
      `${BASE_URL}/productos/precio?min=${rango.min}&max=${rango.max}`,
      { headers: HEADERS }
    );
    verificarRespuesta(res, "rango_precio");
    sleep(0.3);
  });

  group("GET - Productos bajo crítico", () => {
    const res = http.get(`${BASE_URL}/productos/bajo-critico`, { headers: HEADERS });
    verificarRespuesta(res, "bajo_critico");
    sleep(0.3);
  });

  // ── Grupo 2: Inventario ──
  group("GET - Inventario completo", () => {
    const res = http.get(`${BASE_URL}/inventario`, { headers: HEADERS });
    verificarRespuesta(res, "inventario_listar");
    sleep(0.3);
  });

  group("GET - Inventario sin stock", () => {
    const res = http.get(`${BASE_URL}/inventario/sin-stock`, { headers: HEADERS });
    verificarRespuesta(res, "sin_stock");
    sleep(0.3);
  });

  group("PATCH - Ajustar stock", () => {
    const id      = Math.floor(Math.random() * 10) + 1;
    const entrada = Math.floor(Math.random() * 10) + 1; // siempre positivo
    const res     = http.patch(
      `${BASE_URL}/inventario/producto/${id}/ajustar?cantidad=${entrada}`,
      null,
      { headers: HEADERS }
    );
    check(res, {
      "[ajustar_stock] status válido": (r) =>
        r.status === 200 || r.status === 404 || r.status === 400,
    });
    sleep(0.5);
  });

  // ── Grupo 3: Escritura — solo un % de usuarios crea productos ──
  // Simula que solo 1 de cada 5 VUs hace POST
  if (Math.random() < 0.2) {
    group("POST - Crear producto", () => {
      const start   = Date.now();
      const payload = productoPayload(
        randomItem(CATEGORIAS_IDS),
        randomItem(MARCAS_IDS)
      );
      const res = http.post(`${BASE_URL}/productos`, payload, { headers: HEADERS });
      duracionCrear.add(Date.now() - start);
      check(res, {
        "[crear_producto] status 201": (r) => r.status === 201,
        "[crear_producto] tiene id":   (r) => {
          try { return JSON.parse(r.body).data.id > 0; }
          catch { return false; }
        },
      });
      if (res.status !== 201) errores.add(1);
      sleep(1);
    });
  }

  // ── Pausa entre iteraciones — simula usuario real ──
  sleep(Math.random() * 1 + 0.5); // entre 0.5s y 1.5s
}

// ─────────────────────────────────────────────
// Resumen al finalizar — genera HTML + JSON + consola
// ─────────────────────────────────────────────
export function handleSummary(data) {
  return {
    "reporte-stress-catalogo.html": htmlReport(data),              // reporte visual
    "reporte-stress-catalogo.json": JSON.stringify(data, null, 2), // datos completos
    stdout: textSummary(data, { indent: " ", enableColors: true }), // consola
  };
}


//k6 run stress-test-catalogo.js