package pl.pollub.is.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
    @NotBlank(message = "Nazwa użytkownika jest wymagana")
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    private String password;
}
