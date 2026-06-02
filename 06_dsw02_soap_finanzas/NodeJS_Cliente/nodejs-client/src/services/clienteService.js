// src/services/clienteService.js
const axios = require('axios');
const { parseStringPromise } = require('xml2js');

const SOAP_URL = process.env.SOAP_URL || 'http://localhost:8099/ws/clientes';
const NS_ENV   = 'http://schemas.xmlsoap.org/soap/envelope/';
const NS_CLI   = 'http://ws.finanzasperu.com/clientes';

function envelope(bodyContent) {
  return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="${NS_ENV}" xmlns:cli="${NS_CLI}">
   <soapenv:Header/>
   <soapenv:Body>${bodyContent}</soapenv:Body>
</soapenv:Envelope>`;
}

async function call(body) {
  const { data } = await axios.post(SOAP_URL, envelope(body), {
    headers: { 'Content-Type': 'text/xml; charset=utf-8', SOAPAction: '""' },
    timeout: 15000,
  });

  // DEBUG: descomenta esta linea si necesitas ver el XML crudo
  // console.log('SOAP RAW:', data);

  const parsed = await parseStringPromise(data, { explicitArray: false, ignoreAttrs: true });

  // Navegar el envelope independientemente del prefijo que use CXF
  const bodyEl = parsed['S:Envelope']?.['S:Body']
              ?? parsed['soapenv:Envelope']?.['soapenv:Body']
              ?? parsed['soap:Envelope']?.['soap:Body'];

  if (!bodyEl) throw new Error('Respuesta SOAP inesperada');

  const responseNode = Object.values(bodyEl)[0];
  const ret = responseNode?.return ?? responseNode?.['ns2:return'] ?? {};

  return {
    codigo:  ret.codigo  ?? '99',
    mensaje: ret.mensaje ?? 'Sin respuesta',
    // parsear lista (clientes.cliente) o un solo cliente
    data: parseLista(ret) ?? parseUno(ret.cliente) ?? null,
  };
}

// ── Parseo de lista: ret.clientes.cliente ────────────────────────────────────
function parseLista(ret) {
  const contenedor = ret.clientes;
  if (!contenedor) return null;

  const items = contenedor.cliente;
  if (!items) return [];

  // xml2js devuelve objeto si hay 1 elemento, array si hay varios
  if (Array.isArray(items)) return items.map(mapCliente);
  return [mapCliente(items)];
}

// ── Parseo de un solo cliente: ret.cliente ───────────────────────────────────
function parseUno(d) {
  if (!d || typeof d !== 'object') return null;
  return mapCliente(d);
}

function mapCliente(d) {
  return {
    idCliente:       d.idCliente,
    tipoDocumento:   d.tipoDocumento,
    nroDocumento:    d.nroDocumento,
    nombres:         d.nombres,
    apellidoPaterno: d.apellidoPaterno,
    apellidoMaterno: d.apellidoMaterno,
    fechaNacimiento: d.fechaNacimiento,
    direccion:       d.direccion,
    telefono:        d.telefono,
    email:           d.email,
    tipoCliente:     d.tipoCliente,
    estado:          d.estado,
    fechaRegistro:   d.fechaRegistro,
  };
}

// ── Operaciones CRUD ──────────────────────────────────────────────────────────
const clienteService = {

  async listarActivos() {
    return call('<cli:listarClientesActivos/>');
  },

  async buscar(termino) {
    return call(`<cli:buscarClientes><termino>${termino}</termino></cli:buscarClientes>`);
  },

  async consultarPorId(id) {
    return call(`<cli:consultarClientePorId><idCliente>${id}</idCliente></cli:consultarClientePorId>`);
  },

  async consultarPorDocumento(nro) {
    return call(`<cli:consultarClientePorDocumento><nroDocumento>${nro}</nroDocumento></cli:consultarClientePorDocumento>`);
  },

  async registrar(dto) {
    const body = `
      <cli:registrarCliente>
         <clienteDTO>
            <tipoDocumento>${dto.tipoDocumento}</tipoDocumento>
            <nroDocumento>${dto.nroDocumento}</nroDocumento>
            <nombres>${dto.nombres}</nombres>
            <apellidoPaterno>${dto.apellidoPaterno}</apellidoPaterno>
            <apellidoMaterno>${dto.apellidoMaterno ?? ''}</apellidoMaterno>
            <fechaNacimiento>${dto.fechaNacimiento ?? ''}</fechaNacimiento>
            <direccion>${dto.direccion}</direccion>
            <telefono>${dto.telefono}</telefono>
            <email>${dto.email}</email>
            <tipoCliente>${dto.tipoCliente ?? 'NATURAL'}</tipoCliente>
         </clienteDTO>
      </cli:registrarCliente>`;
    return call(body);
  },

  async actualizar(id, dto) {
    const body = `
      <cli:actualizarCliente>
         <idCliente>${id}</idCliente>
         <clienteDTO>
            <nombres>${dto.nombres}</nombres>
            <apellidoPaterno>${dto.apellidoPaterno}</apellidoPaterno>
            <apellidoMaterno>${dto.apellidoMaterno ?? ''}</apellidoMaterno>
            <direccion>${dto.direccion}</direccion>
            <telefono>${dto.telefono}</telefono>
            <email>${dto.email}</email>
            <tipoCliente>${dto.tipoCliente}</tipoCliente>
         </clienteDTO>
      </cli:actualizarCliente>`;
    return call(body);
  },

  async cambiarEstado(id, estado) {
    return call(`
      <cli:cambiarEstadoCliente>
         <idCliente>${id}</idCliente>
         <estado>${estado}</estado>
      </cli:cambiarEstadoCliente>`);
  },

  async eliminar(id) {
    return call(`<cli:eliminarCliente><idCliente>${id}</idCliente></cli:eliminarCliente>`);
  },
};

module.exports = clienteService;