import http from "k6/http";
import { check, sleep, group } from "k6";

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

  
  sleep(0.5);
}


//k6 run stress-basic-catalogo-v1.js


