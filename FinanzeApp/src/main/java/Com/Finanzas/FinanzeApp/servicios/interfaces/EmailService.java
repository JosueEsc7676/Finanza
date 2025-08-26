package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Notificacion;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.NotificacionRepositorio;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService implements IEmailService{

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificacionRepositorio notificacionRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Guardar notificación en BD
    public void registrarNotificacion(String correo, Meta meta, String asunto, String mensaje) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
        if (usuario == null) return;

        Notificacion noti = new Notificacion();
        noti.setUsuario(usuario);
        noti.setCorreo(correo);
        if (meta != null) noti.setMeta(meta);
        noti.setAsunto(asunto);
        noti.setMensaje(mensaje);
        noti.setFechaEnvio(LocalDateTime.now());
        notificacionRepositorio.save(noti);
    }

    // Recuperación de contraseña
    public void enviarCorreoRecuperacion(String to, String link) throws MessagingException {
        String contenido = "Haz clic en el siguiente enlace para restablecer tu contraseña:\n" + link;

        enviarCorreo(to, "Recuperación de contraseña - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Recuperación de contraseña - FinanzeApp", contenido);
    }

    // Confirmación de cuenta
    public void enviarCorreoConfirmacionCuenta(String to, String nombre, String link) throws MessagingException {
        String contenido = "Hola " + nombre + ",\nGracias por registrarte en FinanzeApp.\n" +
                "Confirma tu cuenta aquí: " + link;

        enviarCorreo(to, "Confirma tu cuenta - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Confirma tu cuenta - FinanzeApp", contenido);
    }

    // Notificación de seguridad
    public void enviarNotificacionSeguridad(String to, String ip, String fecha) throws MessagingException {
        String contenido = "Detectamos un intento de inicio de sesión sospechoso.\n" +
                "IP: " + ip + "\nFecha: " + fecha + "\n" +
                "Si no fuiste tú, cambia tu contraseña inmediatamente.";

        enviarCorreo(to, "Alerta de seguridad - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Alerta de seguridad - FinanzeApp", contenido);
    }

    // Confirmación de transacción
    public void enviarConfirmacionTransaccion(String to, String detalle, String monto) throws MessagingException {
        String contenido = "Tu transacción ha sido procesada exitosamente.\n" +
                "Detalle: " + detalle + "\nMonto: $" + monto;

        enviarCorreo(to, "Confirmación de transacción - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Confirmación de transacción - FinanzeApp", contenido);
    }

    // Reporte mensual
    public void enviarReporteMensual(String to, String resumen) throws MessagingException {
        String contenido = "Resumen de tu actividad financiera:\n" + resumen;

        enviarCorreo(to, "Reporte mensual de tus finanzas - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Reporte mensual de tus finanzas - FinanzeApp", contenido);
    }

    // Aviso de pago próximo
    public void enviarAvisoPagoProximo(String to, String concepto, String fecha, String monto) throws MessagingException {
        String contenido = "Tienes un pago próximo:\nConcepto: " + concepto +
                "\nFecha límite: " + fecha + "\nMonto: $" + monto;

        enviarCorreo(to, "Recordatorio de pago próximo - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Recordatorio de pago próximo - FinanzeApp", contenido);
    }

    // Boletín financiero
    public void enviarBoletinFinanciero(String to, String contenido) throws MessagingException {
        String texto = "Consejos y noticias financieras:\n" + contenido;

        enviarCorreo(to, "Boletín financiero - FinanzeApp", texto);
        registrarNotificacion(to, null, "Boletín financiero - FinanzeApp", texto);
    }

    // Recordatorio de ingreso de datos
    public void enviarRecordatorioIngresosDatos(String to, String nombre) throws MessagingException {
        String contenido = "Hola " + nombre + ",\n" +
                "No olvides mantener tus finanzas al día. Ingresa tus últimos movimientos financieros.\n" +
                "Esto te ayudará a:\n- Controlar mejor tus gastos\n- Identificar patrones de consumo\n- Alcanzar tus metas";

        enviarCorreo(to, "Recordatorio: Actualiza tus finanzas - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Recordatorio: Actualiza tus finanzas - FinanzeApp", contenido);
    }

    // Meta completada
    public void enviarNotificacionMetaCompletada(String to, Meta meta) throws MessagingException {
        String contenido = "¡Felicidades!\nHas completado tu meta: " + meta.getTitulo();

        enviarCorreo(to, "Meta completada - FinanzeApp", contenido);
        registrarNotificacion(to, meta, "Meta completada", contenido);
    }

    // Recordatorio de meta
    public void enviarRecordatorioMeta(String to, Meta meta, String diasRestantes) throws MessagingException {
        String contenido = "Tu meta '" + meta.getTitulo() + "' vence en " + diasRestantes + " días.\n" +
                "No olvides registrar tus avances.";

        enviarCorreo(to, "Recordatorio de meta - FinanzeApp", contenido);
        registrarNotificacion(to, meta, "Recordatorio de meta", contenido);
    }

    // 🔹 Método auxiliar para enviar correos en texto plano
    private void enviarCorreo(String to, String asunto, String contenido) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, false); // false = texto plano
        helper.setTo(to);
        helper.setSubject(asunto);
        helper.setText(contenido, false);
        mailSender.send(mensaje);
    }
    // Recordatorio de cierre de mes
    public void enviarRecordatorioCierreMes(String to, String nombre) throws MessagingException {
        String contenido = "Hola " + nombre + ",\n" +
                "Estamos por cerrar el mes. Es un buen momento para revisar tus ingresos, egresos " +
                "y actualizar tus movimientos financieros.\n" +
                "Esto te ayudará a tener un mejor control y generar tu reporte mensual.";

        enviarCorreo(to, "Recordatorio: Cierre de mes - FinanzeApp", contenido);
        registrarNotificacion(to, null, "Recordatorio: Cierre de mes - FinanzeApp", contenido);
    }

}
