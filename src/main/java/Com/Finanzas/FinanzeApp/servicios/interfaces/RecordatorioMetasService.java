package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.EstadoMeta;
import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.repositorios.MetaRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecordatorioMetasService {

    private final MetaRepositorio metaRepo;
    private final EmailService emailService;

    public RecordatorioMetasService(MetaRepositorio metaRepo, EmailService emailService) {
        this.metaRepo = metaRepo;
        this.emailService = emailService;
    }

    // 🔹 Se ejecuta cada 2 minutos
//    @Scheduled(cron = "0 */2 * * * ?")
    @Scheduled(cron = "0 0 8 * * MON")

    public void enviarRecordatorios() {
        List<Meta> metas = metaRepo.findAllWithUsuario(); // 🔹 Ya carga el usuario

        for (Meta meta : metas) {
            if (meta.getEstado() != EstadoMeta.COMPLETADA
                    && meta.getFechaLimite().isAfter(LocalDate.now())) {

                // ✅ Verificar si el usuario tiene notificaciones activas
                if (meta.getUsuario().getNotificacionesActivas() != null
                        && meta.getUsuario().getNotificacionesActivas()) {
                    try {
                        String correo = meta.getUsuario().getCorreo();
                        long diasRestantes = LocalDate.now().until(meta.getFechaLimite()).getDays();

                        emailService.enviarRecordatorioMeta(correo, meta, String.valueOf(diasRestantes));
                        System.out.println("📧 Recordatorio enviado para la meta: " + meta.getTitulo());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("⚠ Usuario con notificaciones desactivadas, no se envía recordatorio. Usuario: "
                            + meta.getUsuario().getCorreo());
                }
            }
        }
    }

}
