package ar.com.deliverar.deliver.controller;

import ar.com.deliverar.deliver.model.Usuario;
import ar.com.deliverar.deliver.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Por si accedés desde un frontend local
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    // Obtener uno
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear uno
    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    // Modificar uno
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario datosActualizados) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, datosActualizados);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar uno
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar rol
    @PatchMapping("/{id}/rol")
    public ResponseEntity<Usuario> actualizarRol(@PathVariable Long id, @RequestBody String nuevoRol) {
        Usuario actualizado = usuarioService.actualizarRol(id, nuevoRol);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Alta masiva
    @PostMapping("/batch")
    public List<Usuario> crearVarios(@RequestBody List<Usuario> usuarios) {
        return usuarioService.crearUsuarios(usuarios);
    }

    // Baja masiva
    @DeleteMapping("/batch")
    public ResponseEntity<Void> eliminarVarios(@RequestBody List<Long> ids) {
        usuarioService.eliminarUsuarios(ids);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/carga-masiva")
    public ResponseEntity<List<Usuario>> cargarUsuariosDesdeCSV(@RequestParam("archivo") MultipartFile archivo) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<Usuario> usuarios = usuarioService.crearUsuariosDesdeCSV(archivo.getInputStream());
            return ResponseEntity.ok(usuarios);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
