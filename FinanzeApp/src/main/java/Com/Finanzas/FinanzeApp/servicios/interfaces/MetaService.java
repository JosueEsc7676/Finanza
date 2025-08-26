package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Meta;
import Com.Finanzas.FinanzeApp.modelos.Usuario;
import jakarta.mail.MessagingException;

import java.util.List;
import java.util.Optional;

public interface MetaService {
    Meta guardarMeta(Meta meta);
    List<Meta> obtenerTodas();
    Optional<Meta> obtenerPorId(Long id);
    void eliminarMeta(Long id);
    List<Meta> obtenerPorUsuario(Usuario usuario);
    void completarMeta(Long id, Usuario usuario) throws MessagingException;
    void enviarRecordatoriosMetas();
}
