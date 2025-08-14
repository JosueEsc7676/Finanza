////package Com.Finanzas.FinanzeApp.servicios.interfaces;
////
////import Com.Finanzas.FinanzeApp.modelos.Usuario;
////import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.core.userdetails.*;
////import org.springframework.stereotype.Service;
////
////@Service
////public class UsuarioDetailService implements UserDetailsService {
////
////
////    @Autowired
////    private UsuarioRepositorio usuarioRepo;
////
////    @Override
////    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
////        Usuario usuario = usuarioRepo.findByCorreo(correo)
////                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
////
////        return User.withUsername(usuario.getCorreo())
////                .password(usuario.getContrasena())
////                .roles("USER") // no se usan roles realmente
////                .build();
////    }
////}
//package Com.Finanzas.FinanzeApp.servicios.interfaces;
//
//import Com.Finanzas.FinanzeApp.modelos.Usuario;
//import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.*;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UsuarioDetailService implements UserDetailsService {
//
//    @Autowired
//    private UsuarioRepositorio usuarioRepo;
//
//    @Override
//    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
//        Usuario usuario = usuarioRepo.findByCorreo(correo)
//                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
//
//        return User.withUsername(usuario.getCorreo())
//                .password(usuario.getContrasena())
//                .roles("USER")
//                .build();
//    }
//}
package Com.Finanzas.FinanzeApp.servicios.interfaces;

import Com.Finanzas.FinanzeApp.modelos.Usuario;
import Com.Finanzas.FinanzeApp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getContrasena())
                .roles("USER")
                .build();
    }
}
