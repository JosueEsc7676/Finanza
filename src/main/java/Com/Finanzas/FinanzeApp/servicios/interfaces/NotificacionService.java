package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import Com.Finanzas.FinanzeApp.servicios.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private IEmailService emailService;

    public String enviarRecordatoriosPrueba() {
        System.out.println("[v0] Ejecutando prueba manual de recordatorios...");

        List<Usuario> usuarios = usuarioRepositorio.findAll();
        int enviados = 0;
        int errores = 0;

        for (Usuario usuario : usuarios) {
            if (usuario.getNotificacionesActivas() != null && usuario.getNotificacionesActivas()) {
                try {
                    emailService.enviarRecordatorioIngresosDatos(usuario.getCorreo(), usuario.getNombreCompleto());
                    enviados++;
                    System.out.println("[v0] Recordatorio de prueba enviado a: " + usuario.getCorreo());

                } catch (Exception e) {
                    errores++;
                    System.err.println("[v0] Error enviando recordatorio de prueba a " + usuario.getCorreo() + ": " + e.getMessage());
                }
            }
        }

        String resultado = String.format("Prueba completada. Enviados: %d, Errores: %d, Total usuarios con notificaciones activas: %d",
                enviados, errores, enviados + errores);
        System.out.println("[v0] " + resultado);
        return resultado;
    }

    @Scheduled(cron = "0 0 9 */3 * *")
//@Scheduled(cron = "0 */2 * * * ?")
    public void enviarRecordatoriosIngresosDatos() {
        System.out.println("[v0] Ejecutando recordatorios de ingreso de datos...");

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime hace3Dias = ahora.minusDays(3);

        List<Usuario> usuarios = usuarioRepositorio.findAll();

        for (Usuario usuario : usuarios) {
            if (usuario.getNotificacionesActivas() != null && usuario.getNotificacionesActivas() &&
                    (usuario.getUltimoRecordatorio() == null || usuario.getUltimoRecordatorio().isBefore(hace3Dias))) {

                try {
                    emailService.enviarRecordatorioIngresosDatos(usuario.getCorreo(), usuario.getNombreCompleto());

                    usuario.setUltimoRecordatorio(ahora);
                    usuarioRepositorio.save(usuario);

                    System.out.println("[v0] Recordatorio enviado a: " + usuario.getCorreo());

                } catch (Exception e) {
                    System.err.println("[v0] Error enviando recordatorio a " + usuario.getCorreo() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("[v0] Proceso de recordatorios completado.");
    }

    @Scheduled(cron = "0 0 8 * * MON")
//@Scheduled(cron = "0 */2 * * * ?")
    public void enviarRecordatorioSemanal() {
        System.out.println("[v0] Ejecutando recordatorios semanales...");

        List<Usuario> usuarios = usuarioRepositorio.findAll();

        for (Usuario usuario : usuarios) {
            if (usuario.getNotificacionesActivas() != null && usuario.getNotificacionesActivas()) {
                try {
                    emailService.enviarRecordatorioIngresosDatos(usuario.getCorreo(), usuario.getNombreCompleto());
                    System.out.println("[v0] Recordatorio semanal enviado a: " + usuario.getCorreo());

                } catch (Exception e) {
                    System.err.println("[v0] Error enviando recordatorio semanal a " + usuario.getCorreo() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("[v0] Proceso de recordatorios semanales completado.");
    }

    // 🔔 NUEVO RECORDATORIO: Cierre de mes
    @Scheduled(cron = "0 0 18 L * ?")
//    @Scheduled(cron = "0 */2 * * * ?")
    public void enviarRecordatorioCierreMes() {
        System.out.println("[v0] Ejecutando recordatorio de cierre de mes...");

        List<Usuario> usuarios = usuarioRepositorio.findAll();

        for (Usuario usuario : usuarios) {
            if (usuario.getNotificacionesActivas() != null && usuario.getNotificacionesActivas()) {
                try {
                    emailService.enviarRecordatorioCierreMes(usuario.getCorreo(), usuario.getNombreCompleto());
                    System.out.println("[v0] Recordatorio de cierre de mes enviado a: " + usuario.getCorreo());

                } catch (Exception e) {
                    System.err.println("[v0] Error enviando recordatorio de cierre de mes a " + usuario.getCorreo() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("[v0] Proceso de recordatorio de cierre de mes completado.");
    }
}
