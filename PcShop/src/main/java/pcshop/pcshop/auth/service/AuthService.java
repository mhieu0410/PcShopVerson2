package pcshop.pcshop.auth.dto.service;

import jakarta.servlet.http.HttpServletResponse;
import pcshop.pcshop.auth.dto.LoginRequest;
import pcshop.pcshop.auth.dto.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest req, String appBaseUrl);
    void verifyEmail(String rawToken);

    String login(LoginRequest req, String ip, String userAgent, HttpServletResponse response );
    String refresh(String rawRefresh, String ip, String userAgent, HttpServletResponse response);
    void logout(String rawRefresh, HttpServletResponse  response);
}
