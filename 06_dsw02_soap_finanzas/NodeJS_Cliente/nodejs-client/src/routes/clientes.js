// src/routes/clientes.js — Express API REST que envuelve el WS SOAP
const express  = require('express');
const router   = express.Router();
const svc      = require('../services/clienteService');

// GET  /api/clientes          — listar activos
router.get('/', async (req, res) => {
  try {
    const { buscar } = req.query;
    const resp = buscar ? await svc.buscar(buscar) : await svc.listarActivos();
    res.json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

// GET  /api/clientes/:id
router.get('/:id', async (req, res) => {
  try {
    const resp = await svc.consultarPorId(req.params.id);
    res.json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

// POST /api/clientes
router.post('/', async (req, res) => {
  try {
    const resp = await svc.registrar(req.body);
    res.status(resp.codigo === '00' ? 201 : 400).json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

// PUT  /api/clientes/:id
router.put('/:id', async (req, res) => {
  try {
    const resp = await svc.actualizar(req.params.id, req.body);
    res.json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

// PATCH /api/clientes/:id/estado
router.patch('/:id/estado', async (req, res) => {
  try {
    const resp = await svc.cambiarEstado(req.params.id, req.body.estado);
    res.json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

// DELETE /api/clientes/:id  (baja logica)
router.delete('/:id', async (req, res) => {
  try {
    const resp = await svc.eliminar(req.params.id);
    res.json(resp);
  } catch (e) { res.status(500).json({ codigo:'99', mensaje: e.message }); }
});

module.exports = router;
