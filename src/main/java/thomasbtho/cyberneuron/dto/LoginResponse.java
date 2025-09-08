package thomasbtho.cyberneuron.dto;

public record LoginResponse(String token, long expiresIn) {
}
