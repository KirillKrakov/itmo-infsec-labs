package itmo.infsec.lab1.service;

import itmo.infsec.lab1.dto.AuthRequestDTO;
import itmo.infsec.lab1.dto.AuthResponseDTO;
import itmo.infsec.lab1.dto.RegisterRequestDTO;
import itmo.infsec.lab1.model.User;
import itmo.infsec.lab1.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.info("Registering new user: {}", request.getUsername());
        if (userService.existsByUsername(request.getUsername())) {
            log.warn("Username already taken: {}", request.getUsername());
            throw new RuntimeException("Username is already taken");
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            userService.saveUser(user);
            log.info("User saved successfully: {}", user.getUsername());
            String token = generateTokenForUser(request.getUsername());
            log.info("Token generated for new user: {}", user.getUsername());
            return new AuthResponseDTO(token, user.getUsername());
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        log.info("Processing login for user: {}", request.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User authenticated successfully: {}", request.getUsername());
            String token = generateTokenForUser(request.getUsername());
            log.info("Token generated for user: {}", request.getUsername());
            return new AuthResponseDTO(token, request.getUsername());
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }

    private String generateTokenForUser(String username) {
        log.debug("Generating token for user: {}", username);
        try {
            userService.loadUserByUsername(username);
            return jwtUtil.generateToken(username);
        } catch (Exception e) {
            log.error("Error generating token for user {}: {}", username, e.getMessage(), e);
            throw e;
        }
    }
}
