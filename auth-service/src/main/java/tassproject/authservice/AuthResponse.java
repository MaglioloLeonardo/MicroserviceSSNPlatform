package tassproject.authservice;

public record AuthResponse(String tokenType, String accessToken) {
    public static AuthResponse bearer(String jwt) {
        return new AuthResponse("Bearer", jwt);
    }
}
