package pl.pollub.is.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.pollub.is.backend.auth.dto.RegisterDto;

import java.util.Objects;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, RegisterDto> {
    @Override
    public void initialize(PasswordsMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(RegisterDto value, ConstraintValidatorContext context) {
        return Objects.equals(value.getPassword(), value.getPasswordConfirmation());
    }
}
