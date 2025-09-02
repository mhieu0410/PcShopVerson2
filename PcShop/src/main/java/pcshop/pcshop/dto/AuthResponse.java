package pcshop.pcshop.dto;

public record AuthResponse (String access_token, String token_type, long expires_ind){
    public static AuthResponse bearer(String token, long expiresInSec){
        return new AuthResponse(token, "Bearer", expiresInSec);
    }
}
