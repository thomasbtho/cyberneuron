package thomasbtho.cyberneuron.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thomasbtho.cyberneuron.dto.LoginResponse;
import thomasbtho.cyberneuron.dto.SignupResponse;
import thomasbtho.cyberneuron.dto.LoginRequest;
import thomasbtho.cyberneuron.dto.SignupRequest;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.security.JwtUtil;
import thomasbtho.cyberneuron.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtUtil jwtService;
    private final AuthService authService;

    public AuthController(JwtUtil jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        User newUser = authService.signup(signupRequest);
        SignupResponse response = new SignupResponse("User registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        User authUser = authService.login(loginRequest);

        String jwtToken = jwtService.generateToken(authUser);

        LoginResponse response = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(response);
    }
}
