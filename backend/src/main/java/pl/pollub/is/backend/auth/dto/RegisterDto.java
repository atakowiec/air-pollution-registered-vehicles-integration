package pl.pollub.is.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pl.pollub.is.backend.validation.PasswordsMatch;

@PasswordsMatch(field = "passwordConfirmation")
@Data
public class RegisterDto {
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    @Pattern(regexp = ".{4,}", message = "Nazwa użytkownika musi mieć co najmniej 4 znaki")
    private String username;

    @NotNull(message = "Hasło jest wymagane")
    @Pattern(regexp = ".{4,}", message = "Hasło musi mieć co najmniej 4 znaki")
    private String password;

    @NotNull(message = "Potwierdzenie hasła jest wymagane")
    private String passwordConfirmation;
}
