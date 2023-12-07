package com.mail.controllers;

import com.mail.entity.Usuario;
import com.mail.exceptions.MiExcepcion;
import com.mail.service.EmailService;
import com.mail.service.UsuarioService;
import com.mail.utilitys.Utility;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class EmailController {
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    public EmailController(UsuarioService usuarioService, EmailService emailService) {
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    /**
     * Maneja las solicitudes GET para la recuperación de contraseña.
     *
     * Muestra la página de recuperación de contraseña con el título correspondiente.
     *
     * @param modelo El modelo que se utiliza para pasar datos a la vista.
     * @return La vista "recuperoContraseña".
     */
    @GetMapping("/olvido-password")
    public String forgotPassword(Model modelo) {
        return "recuperoContraseña";
    }

    /**
     * Maneja las solicitudes POST para el proceso de recuperación de contraseña.
     *
     * Procesa la solicitud de recuperación de contraseña, generando un token único,
     * actualizando la información del usuario en la base de datos y enviando un correo
     * electrónico con un enlace para restablecer la contraseña. Muestra un mensaje de éxito
     * o error en la vista.
     *
     * @param request La solicitud HTTP que contiene los parámetros de la solicitud.
     * @param modelo El modelo que se utiliza para pasar datos a la vista.
     * @return La vista "recuperoContraseña" con un mensaje de éxito o error.
     */
    @PostMapping("/olvido-password")
    public String procesarOlvidoPassword(HttpServletRequest request, Model modelo) {
        String email = request.getParameter("email");
        String token = RandomString.make(45);
        try {
            usuarioService.actualizarPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            emailService.sendEmail(email, resetPasswordLink);
            modelo.addAttribute("mensaje", "Se ha enviado un correo electrónico a su dirección de correo electrónico con un enlace para restablecer su contraseña.");
        } catch (MiExcepcion e) {
            modelo.addAttribute("error", e.getMessage());
        }
        modelo.addAttribute("titulo", "Recuperar Contraseña");
        return "recuperoContraseña";
    }

    /**
     * Maneja las solicitudes GET para restablecer la contraseña.
     *
     * Verifica la validez del token proporcionado y muestra la página de cambio de contraseña
     * si el token es válido. En caso contrario, muestra un mensaje de error.
     *
     * @param token El token proporcionado en la URL para restablecer la contraseña.
     * @param modelo El modelo que se utiliza para pasar datos a la vista.
     * @return La vista "cambiar_password" con el token y título correspondientes o la vista "mensaje" en caso de token inválido.
     */
    @GetMapping("/reset_password")
    public String resetearPassword(@Param(value = "token") String token, Model modelo) {
        Usuario usuario = usuarioService.get(token);
        if (usuario == null) {
            modelo.addAttribute("mensaje", "Token Invalido.");
            return "mensaje";
        }
        modelo.addAttribute("token", token);
        modelo.addAttribute("titulo", "Recuperar Contraseña");
        return "cambiar_password";
    }

    /**
     * Maneja las solicitudes POST para procesar el restablecimiento de contraseña.
     *
     * Procesa la solicitud de restablecimiento de contraseña utilizando el token proporcionado.
     * Si el token es válido, actualiza la contraseña del usuario en la base de datos, envía un
     * correo electrónico de confirmación y redirige a la página de inicio de sesión. En caso
     * contrario, muestra un mensaje de error.
     *
     * @param request La solicitud HTTP que contiene los parámetros de la solicitud.
     * @param modelo El modelo que se utiliza para pasar datos a la vista.
     * @return La vista "cambiar_password" con un mensaje de error o la redirección a la página de inicio de sesión en caso de éxito.
     */
    @PostMapping("/reset_password")
    public String procesarResetearPassword(HttpServletRequest request, Model modelo) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        Usuario usuario = usuarioService.get(token);
        if (usuario == null) {
            modelo.addAttribute("mensaje", "Token Invalido.");
            return "cambiar_password";
        }else {

        usuarioService.actualizarPassword(usuario, password);
        emailService.enviarEmailConfirmacionCambioPassword(usuario.getEmail());
        return "redirect:/login";
        }
    }

    /**
     * Maneja las solicitudes GET para la página de envío de correo con PDF.
     *
     * Muestra la vista "enviarCorreoConPDF" que permite al usuario introducir la información
     * necesaria para enviar un correo electrónico con un archivo PDF adjunto.
     *
     * @param modelo El modelo que se utiliza para pasar datos a la vista.
     * @return La vista "enviarCorreoConPDF".
     */
    @GetMapping("/enviar_correo_con_pdf")
    public String enviarCorreoConPDF(Model modelo) {
        return "enviarCorreoConPDF";
    }

    /**
     * Maneja las solicitudes POST para enviar un correo con un archivo PDF adjunto.
     *
     * Procesa la solicitud para enviar un correo electrónico con un archivo PDF adjunto.
     * Verifica si el archivo adjunto es un PDF, y en caso afirmativo, envía el correo electrónico
     * con el PDF adjunto. Si hay algún problema, muestra un mensaje de error.
     *
     * @param to      La dirección de correo electrónico de destino.
     * @param subject El asunto del correo electrónico.
     * @param text    El texto del cuerpo del correo electrónico.
     * @param pdfFile El archivo PDF adjunto.
     * @param request La solicitud HTTP que contiene los parámetros de la solicitud.
     * @return La vista "index" si el correo electrónico se envía con éxito, o la vista "mensaje" con un mensaje de error.
     */
    @PostMapping("/enviar_correo_con_pdf")
    public String enviarCorreoConPDF(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("text") String text,
            @RequestParam("pdfFile") MultipartFile pdfFile,
            HttpServletRequest request) {
        try {
            if (!pdfFile.getContentType().equals("application/pdf")) {
                request.setAttribute("mensaje", "Por favor seleccione un archivo PDF.");
                return "mensaje";
            }
            byte[] pdfBytes = pdfFile.getBytes();
            emailService.sendEmailWithAttachment( to, subject, text, pdfBytes, pdfFile.getOriginalFilename());
            return "index";
        } catch (IOException | MiExcepcion e) {
            request.setAttribute("mensaje", e.getMessage());
            return "mensaje";
        }
    }
}