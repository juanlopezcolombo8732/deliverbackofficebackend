package ar.com.deliverar.deliver.service;

import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.repository.UsuarioRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Crear
    public Usuario crearUsuario(Usuario usuario) {
        System.out.println("USUARIO ID");

        return usuarioRepository.save(usuario);
    }

    // Modificar
    // Modificar
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario existente = usuarioOpt.get();
            existente.setNombre(usuarioActualizado.getNombre());
            existente.setApellido(usuarioActualizado.getApellido());
            existente.setEmail(usuarioActualizado.getEmail());
            existente.setTelefono(usuarioActualizado.getTelefono());
            existente.setDireccion(usuarioActualizado.getDireccion());
            existente.setCiudad(usuarioActualizado.getCiudad());
            existente.setPais(usuarioActualizado.getPais());
            existente.setDepartamento(usuarioActualizado.getDepartamento());
            existente.setFechaContratacion(usuarioActualizado.getFechaContratacion());
            existente.setSalarioBase(usuarioActualizado.getSalarioBase());
            existente.setPorcentajeComision(usuarioActualizado.getPorcentajeComision());
            existente.setNombreContactoEmergencia(usuarioActualizado.getNombreContactoEmergencia());
            existente.setTelefonoContactoEmergencia(usuarioActualizado.getTelefonoContactoEmergencia());
            existente.setTotalVentas(usuarioActualizado.getTotalVentas());
            existente.setSaldoActual(usuarioActualizado.getSaldoActual());

            return usuarioRepository.save(existente);
        }
        return null;
    }


    // Eliminar
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Ver uno
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Ver todos
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Actualizar rol
    public Usuario actualizarRol(Long id, String nuevoRol) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setRol(nuevoRol);
            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // Alta masiva
    public List<Usuario> crearUsuarios(List<Usuario> usuarios) {
        return usuarioRepository.saveAll(usuarios);
    }

    // Baja masiva
    public void eliminarUsuarios(List<Long> ids) {
        usuarioRepository.deleteAllById(ids);
    }

    // Carga desde CSV con formato dd/MM/yyyy
    public List<Usuario> crearUsuariosDesdeCSV(InputStream archivoCSV) throws IOException {
        List<Usuario> usuarios = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(archivoCSV))) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true)
                    .build();

            CSVParser csvParser = new CSVParser(reader, format);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (CSVRecord record : csvParser) {
                Usuario usuario = new Usuario();
                usuario.setNombre(record.get("nombre"));
                usuario.setApellido(record.get("apellido"));
                usuario.setEmail(record.get("email"));
                usuario.setTelefono(record.get("telefono"));
                usuario.setDireccion(record.get("direccion"));
                usuario.setCiudad(record.get("ciudad"));
                usuario.setPais(record.get("pais"));
                usuario.setRol(record.get("rol"));
                usuario.setDepartamento(record.get("departamento"));
                usuario.setNombreContactoEmergencia(record.get("nombreContactoEmergencia"));
                usuario.setTelefonoContactoEmergencia(record.get("telefonoContactoEmergencia"));

                // Campos con conversión
                String fecha = record.get("fechaContratacion");
                if (fecha != null && !fecha.isEmpty()) {
                    usuario.setFechaContratacion(LocalDate.parse(fecha, dateFormatter));
                }

                String salario = record.get("salarioBase");
                if (salario != null && !salario.isEmpty()) {
                    usuario.setSalarioBase(Double.parseDouble(salario));
                }

                String comision = record.get("porcentajeComision");
                if (comision != null && !comision.isEmpty()) {
                    usuario.setPorcentajeComision(Double.parseDouble(comision));
                }

                usuarios.add(usuario);
            }
        }

        return usuarioRepository.saveAll(usuarios);
    }
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario findOrCreateByExternalId(String externalId) {
        return usuarioRepository.findByExternalId(externalId)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario();
                    nuevo.setExternalId(externalId);
                    return usuarioRepository.save(nuevo);
                });
    }


}
