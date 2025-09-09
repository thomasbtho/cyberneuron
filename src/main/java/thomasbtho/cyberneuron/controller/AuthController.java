package thomasbtho.cyberneuron.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Authentication")
public class AuthController {
    private final JwtUtil jwtService;
    private final AuthService authService;

    public AuthController(JwtUtil jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
    })
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User newUser = authService.signup(signupRequest);
        SignupResponse response = new SignupResponse("User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        User authUser = authService.login(loginRequest);

        String jwtToken = jwtService.generateToken(authUser);

        LoginResponse response = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        return ResponseEntity.ok(response);
    }
}
