package com.mail.service;

import com.mail.entity.Usuario;
import com.mail.enumerated.Roles;
import com.mail.exceptions.MiExcepcion;
import com.mail.repositories.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;


    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra un nuevo usuario en la aplicación.
     *
     * @param nombre    El nombre del usuario.
     * @param dni       El número de identificación del usuario.
     * @param password  La contraseña del usuario.
     * @param password2 La confirmación de la contraseña del usuario.
     * @throws Exception Si ocurre un error durante el proceso de registro.
     */
    @Transactional
    public void registrar(String nombre, String dni, String password, String password2) throws Exception {
        validar(nombre, dni, password, password2);
        Usuario usuario = new Usuario();
        usuario.setName(nombre);
        usuario.setDni(dni);
        usuario.setRol(Roles.USER);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuarioRepository.save(usuario);
    }

    /**
     * Valida los parámetros de entrada para el registro de un usuario.
     *
     * @param nombre      El nombre del usuario.
     * @param dni         El DNI del usuario.
     * @param password    La contraseña del usuario.
     * @param password2   La confirmación de la contraseña.
     * @throws IllegalArgumentException Si alguno de los parámetros es nulo o no cumple con los requisitos.
     */
    private void validar(String nombre, String dni, String password, String password2) throws Exception {

        if (nombre.isEmpty() || nombre == null) {
            throw new Exception("El nombre no puede ser nulo o estar vacío");
        }
        if (dni.isEmpty() || dni == null) {
            throw new Exception("El dni no puede ser nulo o estar vacío");
        }
        if (password.isEmpty() || password == null || password.length() <= 6) {
            throw new Exception("El password no puede estar vacío y debe contener por lo menos 6 caracteres");
        }
        if (!password.equals(password2)) {
            throw new Exception("Los password ingresados deben ser iguales");
        }
    }

    /**
     * Actualiza el token de restablecimiento de contraseña para un usuario específico.
     *
     * @param token El nuevo token de restablecimiento de contraseña.
     * @param email La dirección de correo electrónico asociada al usuario.
     * @throws MiExcepcion Si no se encuentra el usuario con la dirección de correo electrónico proporcionada.
     */
    public void actualizarPasswordToken(String token, String email) throws MiExcepcion {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if(usuario != null){
            usuario.setResetPasswordToken(token);
            usuarioRepository.save(usuario);
        } else {
            throw new MiExcepcion("No se encontró el usuario" + email);
        }
    }

    /**
     * Recupera un usuario basado en el token de restablecimiento de contraseña.
     *
     * @param resetPasswordToken El token de restablecimiento de contraseña asociado al usuario.
     * @return El objeto Usuario correspondiente al token proporcionado, o null si no se encuentra.
     */
    public Usuario get(String resetPasswordToken) {
        return usuarioRepository.findByResetPasswordToken(resetPasswordToken);
    }

    /**
     * Actualiza la contraseña de un usuario y elimina el token de restablecimiento de contraseña asociado.
     *
     * @param usuario  El objeto Usuario cuya contraseña se va a actualizar.
     * @param password La nueva contraseña en texto plano.
     */
    public void actualizarPassword(Usuario usuario, String password) {
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setResetPasswordToken(null);
        usuarioRepository.save(usuario);
    }

    /**
     * Carga los detalles del usuario utilizando el DNI como nombre de usuario.
     *
     * @param dni El DNI del usuario.
     * @return Detalles del usuario como UserDetails.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el DNI especificado.
     */
    @Override
    public UserDetails loadUserByUsername(String dni) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorDni(dni);
        if (usuario != null) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            permisos.add(new SimpleGrantedAuthority("ROLE_USER"));

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getDni(), usuario.getPassword(), permisos);
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado con el DNI: " + dni);
        }
    }
}
