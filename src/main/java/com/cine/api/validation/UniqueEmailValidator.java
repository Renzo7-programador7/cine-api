package com.cine.api.validation;

import com.cine.api.repository.UsuarioRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator
        implements ConstraintValidator<UniqueEmail, String> {

    private final UsuarioRepository usuarioRepository;

    public UniqueEmailValidator(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public boolean isValid(String email,
            ConstraintValidatorContext context) {

        if (email == null || email.isBlank()) {
            return true;
        }

        return !usuarioRepository.existsByEmailIgnoreCase(email.trim());
    }
}
