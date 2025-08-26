package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.modelos.EstadoMeta;
import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.MetaRepositorio;
import Com.Finanzas.FinanzeApp.repositorios.NotificacionRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import Com.Finanzas.FinanzeApp.servicios.interfaces.MetaService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MetaServiceImpl implements MetaService {

    @Autowired
    private MetaRepositorio metaRepositorio;

    @Autowired
    private EmailService emailService;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Override
    public Meta guardarMeta(Meta meta) {
        Meta metaGuardada = metaRepositorio.save(meta);

        if (metaGuardada.getMontoActual() >= metaGuardada.getMontoMeta()
                && metaGuardada.getEstado() != EstadoMeta.COMPLETADA) {

            metaGuardada.setEstado(EstadoMeta.COMPLETADA);
            metaRepositorio.save(metaGuardada);

            try {
                emailService.enviarNotificacionMetaCompletada(
                        metaGuardada.getUsuario().getCorreo(),
                        metaGuardada
                );

                Notificacion noti = new Notificacion(
                        metaGuardada.getUsuario().getCorreo(),
                        "🎯 Meta completada",
                        "Has completado tu meta: " + metaGuardada.getTitulo(),
                        LocalDateTime.now()
                );
                noti.setUsuario(metaGuardada.getUsuario());
                noti.setMeta(metaGuardada);
                notificacionRepositorio.save(noti);

                System.out.println("📧 Correo + notificación registrados.");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return metaGuardada;
    }

    @Override
    public List<Meta> obtenerTodas() {
        return metaRepositorio.findAll();
    }

    @Override
    public Optional<Meta> obtenerPorId(Long id) {
        return metaRepositorio.findById(id);
    }

    @Override
    public void eliminarMeta(Long id) {
        metaRepositorio.deleteById(id);
    }

    @Override
    public List<Meta> obtenerPorUsuario(Usuario usuario) {
        return metaRepositorio.findByUsuario(usuario);
    }

    @Override
    public void completarMeta(Long id, Usuario usuario) throws MessagingException {
        Meta meta = metaRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta no encontrada"));

        meta.setEstado(EstadoMeta.COMPLETADA);
        metaRepositorio.save(meta);

        emailService.enviarNotificacionMetaCompletada(usuario.getCorreo(), meta);

        Notificacion noti = new Notificacion(
                usuario.getCorreo(),
                "🎯 Meta completada",
                "Has completado tu meta: " + meta.getTitulo(),
                LocalDateTime.now()
        );
        noti.setUsuario(usuario);
        noti.setMeta(meta);
        notificacionRepositorio.save(noti);
    }

    @Override
    public void enviarRecordatoriosMetas() {
        List<Meta> metas = metaRepositorio.findAll();
        LocalDate hoy = LocalDate.now();

        for (Meta meta : metas) {
            if (meta.getEstado() != EstadoMeta.COMPLETADA && meta.getFechaLimite() != null) {
                long diasRestantes = hoy.until(meta.getFechaLimite()).getDays();
                if (diasRestantes <= 3 && diasRestantes >= 0) {
                    try {
                        emailService.enviarAvisoPagoProximo(
                                meta.getUsuario().getCorreo(),
                                "Meta: " + meta.getTitulo(),
                                meta.getFechaLimite().toString(),
                                String.valueOf(meta.getMontoMeta() - meta.getMontoActual())
                        );

                        Notificacion noti = new Notificacion(
                                meta.getUsuario().getCorreo(),
                                "⏰ Recordatorio de meta",
                                "Tu meta '" + meta.getTitulo() + "' vence el " + meta.getFechaLimite(),
                                LocalDateTime.now()
                        );
                        noti.setUsuario(meta.getUsuario());
                        noti.setMeta(meta);
                        notificacionRepositorio.save(noti);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
