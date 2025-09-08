package thomasbtho.cyberneuron.service;

import thomasbtho.cyberneuron.dto.LoginRequest;
import thomasbtho.cyberneuron.dto.SignupRequest;
import thomasbtho.cyberneuron.entity.User;

public interface AuthService {
    User signup(SignupRequest signupRequest);
    User login(LoginRequest loginRequest);
}
