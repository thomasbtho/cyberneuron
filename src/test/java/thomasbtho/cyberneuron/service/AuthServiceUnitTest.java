package thomasbtho.cyberneuron.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import thomasbtho.cyberneuron.dto.LoginRequest;
import thomasbtho.cyberneuron.dto.SignupRequest;
import thomasbtho.cyberneuron.entity.User;
import thomasbtho.cyberneuron.exception.UserAlreadyExistsException;
import thomasbtho.cyberneuron.repository.UserRepository;
import thomasbtho.cyberneuron.service.impl.AuthServiceImpl;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String testEmail = "test@example.com";
    private final String testPassword = "Password123!";
    private final String testDisplayName = "testuser";
    private final String encodedPassword = "encodedPassword123";

    @Test
    void signup_WithNewUser_ReturnsSavedUser() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest(testEmail, testPassword, testDisplayName);
        User expectedUser = new User();
        expectedUser.setEmail(testEmail);
        expectedUser.setPassword(encodedPassword);
        expectedUser.setDisplayName(testDisplayName);
        expectedUser.setRoles(Set.of("ROLE_USER"));

        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = authService.signup(signupRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(testDisplayName, result.getDisplayName());
        assertTrue(result.getRoles().contains("ROLE_USER"));

        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ReturnsUser() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
        User expectedUser = new User();
        expectedUser.setEmail(testEmail);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void login_WithInvalidCredentials_ThrowsException() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(testEmail, "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    void signup_WithExistingEmail_ThrowsException() {
        // Arrange
        SignupRequest signupRequest = new SignupRequest(testEmail, testPassword, testDisplayName);

        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> authService.signup(signupRequest));
        verify(userRepository).existsByEmail(testEmail);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}