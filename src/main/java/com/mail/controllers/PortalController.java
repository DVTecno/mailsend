package com.mail.controllers;

import com.mail.entity.Usuario;
import com.mail.exceptions.MiExcepcion;
import com.mail.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class PortalController {

   private final UsuarioService usuarioService;

    public PortalController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o contraseña incorrectos");
        }
        return "login.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro";
    }

    @PostMapping("/registrar")
    public String registrar(@RequestParam String nombre, @RequestParam String dni, @RequestParam String password,
                            @RequestParam String password2, ModelMap modelo) throws MiExcepcion {
        try {
            usuarioService.registrar(nombre, dni, password, password2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "redirect:/login";
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        switch (logueado.getRol().toString()) {
            case "USER":
                return "inicio.html";

            default:
                return "redirect:/login";
        }
    }




}
