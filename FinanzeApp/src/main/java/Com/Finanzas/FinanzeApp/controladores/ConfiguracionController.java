package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

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
            usuario.setFotoUrl(null); // 👈 si sube local, anulamos Google
        }

        usuarioRepositorio.save(usuario);
        return "redirect:/configuracion?success";
    }

}
