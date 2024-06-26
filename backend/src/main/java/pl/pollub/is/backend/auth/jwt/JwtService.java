package pl.pollub.is.backend.auth.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.pollub.is.backend.auth.user.User;

import java.util.Date;

@Component
public class JwtService {
    public static final String AUTHORIZATION_COOKIE = "credentials";

    private final String secret;
    private final long expiration;
    private final JwtParser jwtParser;

    public JwtService(@Value("${is.security.jwt.secret}") String secret,
                      @Value("${is.security.jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
        this.jwtParser = Jwts.parser().setSigningKey(secret);
    }

    public String createToken(User user) {
        Claims claims = Jwts.claims();
        claims.setSubject(user.getUsername());
        claims.put("id", user.getId());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(AUTHORIZATION_COOKIE)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void addTokenToResponse(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AUTHORIZATION_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) expiration / 1000);
        response.addCookie(cookie);
    }

    public void addTokenToResponse(HttpServletResponse response, User user) {
        addTokenToResponse(response, createToken(user));
    }

    public void invalidateToken(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTHORIZATION_COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
