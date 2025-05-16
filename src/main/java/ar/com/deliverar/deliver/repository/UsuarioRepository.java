package ar.com.deliverar.deliver.repository;

import ar.com.deliverar.deliver.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Podés agregar métodos custom como:
    // Optional<Usuario> findByEmail(String email);
}
