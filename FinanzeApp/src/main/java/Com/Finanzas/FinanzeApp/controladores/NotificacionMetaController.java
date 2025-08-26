package Com.Finanzas.FinanzeApp.controladores;

import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.NotificacionMetaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones/metas")
public class NotificacionMetaController {

    @Autowired
    private NotificacionMetaRepositorio notificacionMetaRepositorio;

    @GetMapping("/{usuarioId}")
    public List<Notificacion> listarNotificacionesMetas(@PathVariable Long usuarioId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        return notificacionMetaRepositorio.findByMetaUsuarioOrderByFechaEnvioDesc(usuario);
    }
}

