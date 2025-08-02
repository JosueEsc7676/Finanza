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

    public void enviarCorreoRecuperacion(String to, String link) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true);

        helper.setTo(to);
        helper.setSubject("Recuperación de contraseña - FinanzeApp");
        helper.setText("<p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>" +
                "<a href=\"" + link + "\">Recuperar Contraseña</a>", true);

        mailSender.send(mensaje);
    }
}
