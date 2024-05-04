package pl.pollub.is.backend.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.auth.user.UsersRepository;
import pl.pollub.is.backend.exception.HttpException;

@Service
@Getter
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UsersRepository usersRepository;

    public boolean isUsernameTaken(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new HttpException(404, "User not found"));
    }

    public String hashPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public boolean verifyPassword(String hashedPassword, String currentPassword) {
        return bCryptPasswordEncoder.matches(currentPassword, hashedPassword);
    }
}
