package pl.pollub.is.backend.auth;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.pollub.is.backend.auth.dto.LoginDto;
import pl.pollub.is.backend.auth.dto.RegisterDto;
import pl.pollub.is.backend.auth.jwt.JwtService;
import pl.pollub.is.backend.auth.user.Role;
import pl.pollub.is.backend.auth.user.User;
import pl.pollub.is.backend.exception.HttpException;
import pl.pollub.is.backend.util.SimpleJsonBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Map<?, ?> register(@Valid @RequestBody RegisterDto registerDto, HttpServletResponse res) {
        if (authService.isUsernameTaken(registerDto.getUsername())) {
            throw new HttpException(409, "Username is already taken");
        }

        String hashedPassword = authService.hashPassword(registerDto.getPassword());

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(hashedPassword);
        user.setRole(Role.USER);
        authService.getUsersRepository().save(user);

        res.setStatus(201);
        jwtService.addTokenToResponse(res, user);

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("role", user.getRole().name())
                .build();
    }

    @PostMapping("/login")
    public Map<?, ?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse res) {
        User user = authService.getUsersRepository().findByUsername(loginDto.getUsername()).orElseThrow(() -> new HttpException(404, "User not found"));

        if (!authService.verifyPassword(user.getPassword(), loginDto.getPassword())) {
            throw new HttpException(401, "Invalid password");
        }

        jwtService.addTokenToResponse(res, user);

        return SimpleJsonBuilder.of("id", user.getId())
                .add("username", user.getUsername())
                .add("role", user.getRole().name())
                .build();
    }

    @GetMapping("/logout")
    public void logout(HttpServletResponse res) {
        jwtService.invalidateToken(res);
    }
}
