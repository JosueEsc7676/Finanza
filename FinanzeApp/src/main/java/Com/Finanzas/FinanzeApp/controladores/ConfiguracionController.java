//package Com.Finanzas.FinanzeApp.controladores;
//
//import Com.Finanzas.FinanzeApp.modelos.Usuario;
//import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
//import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
//import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
//import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//        import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.security.Principal;
//import java.util.Base64;
//
//@Controller
//@RequestMapping("/configuracion")
//public class ConfiguracionController {
//
//    @Autowired
//    private UsuarioRepositorio usuarioRepositorio;
//
//    @Autowired
//    private EmailService emailService;
//    @Autowired
//    private UsuarioServicio usuarioServicio;
//    private  PersistentTokenRepository tokenRepo;
//
//    // Método para obtener el usuario autenticado
//    private Usuario getAuthenticatedUser() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName(); // El correo es el username
//        return usuarioRepositorio.findByCorreo(email)
//                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
//    }
//
//    @GetMapping
//    public String mostrarConfiguracion(Model model, Principal principal) {
//        Usuario usuario = getAuthenticatedUser();
//        model.addAttribute("usuario", usuario);
//
//        // Foto de Google
//        model.addAttribute("fotoGoogle", usuario.getFotoUrl());
//
//        // Foto local en base64
//        if (usuario.getFotoPerfil() != null) {
//            String fotoBase64 = Base64.getEncoder().encodeToString(usuario.getFotoPerfil());
//            model.addAttribute("fotoBase64", fotoBase64);
//        } else {
//            model.addAttribute("fotoBase64", null);
//        }
//
//        return "configuracion";
//    }
//
//
//    @PostMapping("/guardar")
//    public String guardarConfiguracion(
//            @RequestParam("nombreCompleto") String nombreCompleto,
//            @RequestParam("correo") String correo,
//            @RequestParam("telefono") String telefono,
//            @RequestParam("direccion") String direccion,
//            @RequestParam("fechaNacimiento") String fechaNacimiento,
//            @RequestParam("ocupacion") String ocupacion,
//            @RequestParam("genero") String genero,
//            @RequestParam("moneda") String moneda,
//            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil
//    ) throws IOException {
//
//        Usuario usuario = getAuthenticatedUser();
//
//        usuario.setNombreCompleto(nombreCompleto);
//        usuario.setCorreo(correo);
//        usuario.setTelefono(telefono);
//        usuario.setDireccion(direccion);
//        usuario.setFechaNacimiento(fechaNacimiento);
//        usuario.setOcupacion(ocupacion);
//        usuario.setGenero(genero);
//        usuario.setMoneda(moneda);
//
//        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
//            usuario.setFotoPerfil(fotoPerfil.getBytes());
//            usuario.setFotoUrl(null); // 👈 si sube local, anulamos Google
//        }
//
//        usuarioRepositorio.save(usuario);
//        return "redirect:/configuracion?success";
//    }
//    @PostMapping("/eliminar")
//    public String eliminarCuenta(Authentication auth,
//                                 HttpServletRequest request,
//                                 HttpServletResponse response) {
//        String correo = auth.getName();
//        Long usuarioId = usuarioServicio.obtenerIdPorCorreo(correo);
//
//        boolean eliminado = usuarioServicio.eliminarPorId(usuarioId);
//
//        if (!eliminado) {
//            return "redirect:/configuracion?error";
//        }
//
//        // 1) Revocar tokens remember-me en BD (si aplica)
//        if (tokenRepo != null) {
//            tokenRepo.removeUserTokens(correo);
//        }
//
//        // 2) Cerrar sesión de Spring Security
//        new SecurityContextLogoutHandler().logout(request, response, auth);
//
//        // 3) Borrar cookies de autenticación
//        deleteCookie("jwt", response);          // Tu JWT persistente de Google OAuth2
//        deleteCookie("remember-me", response);  // Cookie remember-me clásica
//        deleteCookie("JSESSIONID", response);   // Cookie de sesión
//
//        // 4) Opcional: limpiar localStorage / sessionStorage en front
//        response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");
//
//        // 5) Redirigir
//        return "redirect:/login?eliminado";
//    }
//
//    private void deleteCookie(String name, HttpServletResponse res) {
//        Cookie c = new Cookie(name, null);
//        c.setPath("/");
//        c.setMaxAge(0);
//        c.setHttpOnly(true);
//        c.setSecure(true); // Actívalo en prod con HTTPS
//        res.addCookie(c);
//    }
//}
package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.UsuarioServicio;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired(required = false)
    private PersistentTokenRepository tokenRepo;

    // Método para obtener el usuario autenticado
    private Usuario getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // El correo es el username
        return usuarioRepositorio.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @GetMapping
    public String mostrarConfiguracion(Model model, Principal principal) {
        Usuario usuario = getAuthenticatedUser();
        model.addAttribute("usuario", usuario);

        // Foto de Google
        model.addAttribute("fotoGoogle", usuario.getFotoUrl());

        // Foto local en base64
        if (usuario.getFotoPerfil() != null) {
            String fotoBase64 = Base64.getEncoder().encodeToString(usuario.getFotoPerfil());
            model.addAttribute("fotoBase64", fotoBase64);
        } else {
            model.addAttribute("fotoBase64", null);
        }

        return "configuracion";
    }

    @PostMapping("/guardar")
    public String guardarConfiguracion(
            @RequestParam("nombreCompleto") String nombreCompleto,
            @RequestParam("correo") String correo,
            @RequestParam("telefono") String telefono,
            @RequestParam("direccion") String direccion,
            @RequestParam("fechaNacimiento") String fechaNacimiento,
            @RequestParam("ocupacion") String ocupacion,
            @RequestParam("genero") String genero,
            @RequestParam("moneda") String moneda,
            @RequestParam(value = "fotoPerfil", required = false) MultipartFile fotoPerfil
    ) throws IOException {

        Usuario usuario = getAuthenticatedUser();

        usuario.setNombreCompleto(nombreCompleto);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setOcupacion(ocupacion);
        usuario.setGenero(genero);
        usuario.setMoneda(moneda);

        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            usuario.setFotoPerfil(fotoPerfil.getBytes());
            usuario.setFotoUrl(null); // Si sube local, anulamos Google
        }

        usuarioRepositorio.save(usuario);
        return "redirect:/configuracion?success";
    }

    @GetMapping("/eliminar")
    public String mostrarConfirmacionEliminar() {
        return "eliminar-cuenta"; // templates/eliminar-cuenta.html
    }

    @PostMapping("/eliminar")
    public String eliminarCuenta(Authentication auth,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        if (auth == null) { // por si acaso
            return "redirect:/login";
        }

        String correo = auth.getName();
        Long usuarioId = usuarioServicio.obtenerIdPorCorreo(correo);

        boolean eliminado = usuarioServicio.eliminarPorId(usuarioId);
        if (!eliminado) {
            return "redirect:/configuracion/eliminar?error";
        }

        if (tokenRepo != null) {
            tokenRepo.removeUserTokens(correo);
        }

        new SecurityContextLogoutHandler().logout(request, response, auth);

        deleteCookie("jwt", response, request.isSecure());
        deleteCookie("remember-me", response, request.isSecure());
        deleteCookie("JSESSIONID", response, request.isSecure());

        response.setHeader("Clear-Site-Data", "\"cookies\", \"storage\"");

        return "redirect:/login?eliminado";
    }

    private void deleteCookie(String name, HttpServletResponse res, boolean secure) {
        Cookie c = new Cookie(name, null);
        c.setPath("/");
        c.setMaxAge(0);
        c.setHttpOnly(true);
        c.setSecure(secure); // en localhost (http) será false
        res.addCookie(c);
    }

}
