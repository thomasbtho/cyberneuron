package thomasbtho.cyberneuron.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import thomasbtho.cyberneuron.dto.LoginRequest;
import thomasbtho.cyberneuron.dto.SignupRequest;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.exception.UserAlreadyExistsException;
import thomasbtho.cyberneuron.repository.UserRepository;
import thomasbtho.cyberneuron.service.AuthService;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public User signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User newUser = new User();
        newUser.setDisplayName(signupRequest.getDisplayName());
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
        newUser.setRoles(Set.of("ROLE_USER"));
        return userRepository.save(newUser);
    }

    @Override
    public User login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.password()
                )
        );

        return userRepository.findByEmail(loginRequest.email()).orElseThrow();
    }
}
