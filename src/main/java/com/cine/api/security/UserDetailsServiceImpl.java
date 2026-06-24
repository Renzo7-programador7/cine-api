package com.cine.api.security;

import com.cine.api.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .map(u -> new User(
                        u.getEmail(),
                        u.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}