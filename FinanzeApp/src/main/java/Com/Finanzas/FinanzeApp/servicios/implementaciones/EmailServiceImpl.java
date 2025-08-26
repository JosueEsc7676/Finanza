package Com.Finanzas.FinanzeApp.servicios.implementaciones;

import Com.Finanzas.FinanzeApp.servicios.interfaces.IEmailService;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements IEmailService{

    @Override
    public void enviarRecordatorioIngresosDatos(String correo, String nombre) {
        // Aquí va tu lógica para enviar el correo
        System.out.println("Enviando recordatorio de ingresos a " + correo + " para " + nombre);
    }

    @Override
    public void enviarRecordatorioCierreMes(String correo, String nombre) {
        // Aquí va tu lógica para enviar el correo
        System.out.println("Enviando recordatorio de cierre de mes a " + correo + " para " + nombre);
    }

    @Override
    public void enviarCorreoRecuperacion(String correo, String token) {
        // Aquí va tu lógica para enviar el correo
        System.out.println("Enviando correo de recuperación a " + correo + " con token " + token);
    }
}
