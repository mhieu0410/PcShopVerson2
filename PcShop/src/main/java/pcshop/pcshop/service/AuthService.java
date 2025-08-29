package pcshop.pcshop.service;

import pcshop.pcshop.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest req, String appBaseUrl);
    void verifyEmail(String rawToken);
}
