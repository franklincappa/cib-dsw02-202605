package com.finanzasperu.ws.service.impl;

import com.finanzasperu.ws.dto.ClienteDTO;
import com.finanzasperu.ws.dto.ListaClientesDTO;
import com.finanzasperu.ws.dto.RespuestaDTO;
import com.finanzasperu.ws.mapper.ClienteMapper;
import com.finanzasperu.ws.model.Cliente;
import com.finanzasperu.ws.repository.ClienteRepository;
import com.finanzasperu.ws.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de negocio de Clientes.
 *
 * Patrones aplicados:
 *  @Slf4j          -> Lombok inyecta un logger (log.info, log.error, etc.)
 *  @RequiredArgsConstructor -> Lombok genera constructor con campos final (inyeccion)
 *  @Transactional  -> garantiza atomicidad en operaciones de escritura
 *  Separation of Concerns -> esta clase solo tiene logica de negocio, no HTTP ni SOAP
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper     clienteMapper;

    // ── Crear ───────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RespuestaDTO crear(ClienteDTO dto) {
        log.info("Creando cliente: documento={}", dto.getNroDocumento());

        if (clienteRepository.existsByNroDocumento(dto.getNroDocumento())) {
            return RespuestaDTO.error("Ya existe un cliente con el documento: " + dto.getNroDocumento());
        }
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            return RespuestaDTO.error("Ya existe un cliente con el email: " + dto.getEmail());
        }

        Cliente cliente = clienteMapper.toEntity(dto);
        Cliente guardado = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente: id={}", guardado.getIdCliente());

        return RespuestaDTO.exitoUno("Cliente registrado exitosamente", clienteMapper.toDTO(guardado));
    }

    // ── Lectura ─────────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public RespuestaDTO buscarPorId(Long idCliente) {
        log.debug("Buscando cliente por id={}", idCliente);
        return clienteRepository.findById(idCliente)
                .map(c -> RespuestaDTO.exitoUno("Cliente encontrado", clienteMapper.toDTO(c)))
                .orElse(RespuestaDTO.error("Cliente no encontrado con id: " + idCliente));
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaDTO buscarPorDocumento(String nroDocumento) {
        log.debug("Buscando cliente por documento={}", nroDocumento);
        return clienteRepository.findByNroDocumento(nroDocumento)
                .map(c -> RespuestaDTO.exitoUno("Cliente encontrado", clienteMapper.toDTO(c)))
                .orElse(RespuestaDTO.error("Cliente no encontrado con documento: " + nroDocumento));
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaDTO listarTodos() {
        List<ClienteDTO> lista = clienteMapper.toDTOList(clienteRepository.findAll());
        log.debug("Listando todos los clientes: total={}", lista.size());
        return RespuestaDTO.exitoLista(
                "Se encontraron " + lista.size() + " clientes",
                new ListaClientesDTO(lista)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaDTO listarActivos() {
        List<ClienteDTO> lista = clienteMapper.toDTOList(clienteRepository.findByEstado("ACTIVO"));
        return RespuestaDTO.exitoLista(
                "Clientes activos " + lista.size() + " clientes",
                new ListaClientesDTO(lista)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaDTO buscar(String termino) {
        List<ClienteDTO> lista = clienteMapper.toDTOList(clienteRepository.buscarPorTermino(termino));
        return RespuestaDTO.exitoLista(
                "Se encontraron " + lista.size() + " clientes",
                new ListaClientesDTO(lista)
        );
    }

    // ── Actualización ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RespuestaDTO actualizar(Long idCliente, ClienteDTO dto) {
        log.info("Actualizando cliente id={}", idCliente);
        Optional<Cliente> opt = clienteRepository.findById(idCliente);
        if (opt.isEmpty()) {
            return RespuestaDTO.error("Cliente no encontrado con id: " + idCliente);
        }

        Cliente cliente = opt.get();
        // Actualizar solo campos permitidos (nunca el documento)
        cliente.setNombres(dto.getNombres());
        cliente.setApellidoPaterno(dto.getApellidoPaterno());
        cliente.setApellidoMaterno(dto.getApellidoMaterno());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setEmail(dto.getEmail());
        cliente.setTipoCliente(dto.getTipoCliente());

        Cliente actualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado: id={}", actualizado.getIdCliente());
        return RespuestaDTO.exitoUno("Cliente actualizado correctamente", clienteMapper.toDTO(actualizado));
    }

    @Override
    @Transactional
    public RespuestaDTO cambiarEstado(Long idCliente, String estado) {
        log.info("Cambiando estado cliente id={} -> {}", idCliente, estado);
        return clienteRepository.findById(idCliente).map(c -> {
            c.setEstado(estado);
            clienteRepository.save(c);
            return RespuestaDTO.builder().codigo("00").mensaje("Estado actualizado").build();
        }).orElse(RespuestaDTO.error("Cliente no encontrado con id: " + idCliente));
    }

    // ── Eliminar ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public RespuestaDTO eliminar(Long idCliente) {
        log.info("Eliminando cliente id={}", idCliente);
        if (!clienteRepository.existsById(idCliente)) {
            return RespuestaDTO.error("Cliente no encontrado con id: " + idCliente);
        }
        // Baja logica: cambiar estado a INACTIVO, se evita el borrado fisicamente
        return cambiarEstado(idCliente, "INACTIVO");
    }
}
