// server.js — FinanzasPeru Node.js API Gateway
require('dotenv').config();
const express = require('express');
const cors    = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

// API REST que internamente llama al WS SOAP
app.use('/api/clientes', require('./src/routes/clientes'));

app.get('/health', (_, res) => res.json({ status: 'ok', service: 'finanzas-peru-node' }));

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => console.log(`API Gateway escuchando en http://localhost:${PORT}`));
