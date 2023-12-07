package com.mail.repositories;

import com.mail.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByDni(@Param("dni") String dni);
    @Query("SELECT u FROM Usuario u WHERE u.dni = :dni")
    Usuario buscarPorDni(String dni);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(@Param("email") String email);
    @Query("SELECT u FROM Usuario u WHERE u.verificationCode = ?1")
    Usuario findByVerificationCode(String verificationCode);
    public Usuario findByResetPasswordToken(String token);
}
