package Com.Finanzas.FinanzeApp.servicios.interfaces;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Recuperación de contraseña
    public void enviarCorreoRecuperacion(String to, String link) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Recuperación de contraseña - FinanzeApp");
        helper.setText("<p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>" +
                "<a href=\"" + link + "\">Recuperar Contraseña</a>", true);

        mailSender.send(mensaje);
    }

    // ✅ (1) Confirmación de cuenta
    public void enviarCorreoConfirmacionCuenta(String to, String nombre, String link) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Confirma tu cuenta - FinanzeApp");
        helper.setText("<p>Hola " + nombre + ",</p>" +
                "<p>Gracias por registrarte en FinanzeApp. Haz clic en el siguiente enlace para activar tu cuenta:</p>" +
                "<a href=\"" + link + "\">Confirmar Cuenta</a>", true);

        mailSender.send(mensaje);
    }

    // ✅ (3) Notificación de seguridad
    public void enviarNotificacionSeguridad(String to, String ip, String fecha) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Alerta de seguridad - FinanzeApp");
        helper.setText("<p>Detectamos un intento de inicio de sesión sospechoso.</p>" +
                "<p><b>IP:</b> " + ip + "</p>" +
                "<p><b>Fecha:</b> " + fecha + "</p>" +
                "<p>Si no fuiste tú, por favor cambia tu contraseña inmediatamente.</p>", true);

        mailSender.send(mensaje);
    }

    // ✅ (4) Confirmación de transacción
    public void enviarConfirmacionTransaccion(String to, String detalle, String monto) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Confirmación de transacción - FinanzeApp");
        helper.setText("<p>Tu transacción ha sido procesada exitosamente.</p>" +
                "<p><b>Detalle:</b> " + detalle + "</p>" +
                "<p><b>Monto:</b> $" + monto + "</p>", true);

        mailSender.send(mensaje);
    }

    // ✅ (6) Reporte mensual
    public void enviarReporteMensual(String to, String resumen) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Reporte mensual de tus finanzas - FinanzeApp");
        helper.setText("<h3>Resumen de tu actividad financiera:</h3>" +
                "<p>" + resumen + "</p>", true);

        mailSender.send(mensaje);
    }

    // ✅ (7) Aviso de pago próximo
    public void enviarAvisoPagoProximo(String to, String concepto, String fecha, String monto) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Recordatorio de pago próximo - FinanzeApp");
        helper.setText("<p>Tienes un pago próximo:</p>" +
                "<p><b>Concepto:</b> " + concepto + "</p>" +
                "<p><b>Fecha límite:</b> " + fecha + "</p>" +
                "<p><b>Monto:</b> $" + monto + "</p>", true);

        mailSender.send(mensaje);
    }

    // ✅ (8) Boletín de noticias / tips financieros
    public void enviarBoletinFinanciero(String to, String contenido) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Boletín financiero - FinanzeApp");
        helper.setText("<h2>Consejos y noticias financieras</h2>" +
                "<p>" + contenido + "</p>", true);

        mailSender.send(mensaje);
    }
    // ✅ (9) Recordatorio de ingreso de datos
    public void enviarRecordatorioIngresosDatos(String to, String nombre) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Recordatorio: Actualiza tus finanzas - FinanzeApp");
        helper.setText("<p>Hola " + nombre + ",</p>" +
                "<p>¡No olvides mantener tus finanzas al día! Te recordamos que ingreses tus últimos movimientos financieros.</p>" +
                "<p>Mantener un registro actualizado te ayudará a:</p>" +
                "<ul>" +
                "<li>Controlar mejor tus gastos</li>" +
                "<li>Identificar patrones de consumo</li>" +
                "<li>Alcanzar tus metas financieras</li>" +
                "</ul>" +
                "<p><a href=\"https://finanzeapp.pagekite.me/new_movimiento\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Ingresar Movimientos</a></p>" +
                "<p>¡Gracias por usar FinanzeApp!</p>", true);

        mailSender.send(mensaje);
    }
}

