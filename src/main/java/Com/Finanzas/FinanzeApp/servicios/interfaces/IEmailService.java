package Com.Finanzas.FinanzeApp.servicios.interfaces;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void enviarRecordatorioIngresosDatos(String correo, String nombre) throws MessagingException;
    void enviarRecordatorioCierreMes(String correo, String nombre) throws MessagingException;
    void enviarCorreoRecuperacion(String correo, String token) throws MessagingException;
}

