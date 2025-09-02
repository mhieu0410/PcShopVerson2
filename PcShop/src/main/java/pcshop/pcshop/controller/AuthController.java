package pcshop.pcshop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcshop.pcshop.dto.*;
import pcshop.pcshop.service.AuthService;
import pcshop.pcshop.web.CookieUtil;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    // Đăng ký
    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiMessage register(@Valid @RequestBody RegisterRequest req,
                               @RequestHeader(value = "X-App-Base-Url", required = false) String appBaseUrl){
        // appBaseUrl: http://localhost:5173 (FE) hoặc http://localhost:8080 (API) tuỳ bạn muốn link tới đâu
        if(appBaseUrl == null || appBaseUrl.isBlank()){
            appBaseUrl = "http://localhost:5173";
        }
        auth.register(req, appBaseUrl);
        return new ApiMessage("Đăng ký thành công. Vui lòng kiểm tra email để xác minh.");
    }

    // Xác minh email
    @PostMapping("/verify-email")
    public ApiMessage verify(@Valid @RequestBody VerifyEmailRequest req){
        auth.verifyEmail(req.token());
        return new ApiMessage("Email đã được xác minh. Tài khoản ACTIVE.");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req,
                              HttpServletRequest httpReq,
                              HttpServletResponse httpRes){
        String ip = httpReq.getRemoteAddr();
        String ua = httpReq.getHeader("User-Agent");
        String access = auth.login(req, ip, ua, httpRes);
        // expires_in = TTL access (giây)
        return AuthResponse.bearer(access, 900);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(HttpServletRequest req, HttpServletResponse res){
        String raw = null;
        if(req.getCookies() != null){
            for(Cookie c : req.getCookies()){
                if(CookieUtil.REFRESH_COOKIE.equals(c.getName())){
                    raw = c.getValue();
                    break;
                }
            }
        }
        String ip = req.getRemoteAddr();
        String ua = req.getHeader("User-Agent");
        String access = auth.refresh(raw, ip, ua, res);
        return AuthResponse.bearer(access, 900);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest req, HttpServletResponse res){
        String raw = null;
        if(req.getCookies() != null){
            for(Cookie c : req.getCookies()){
                if(CookieUtil.REFRESH_COOKIE.equals(c.getName())){
                    raw = c.getValue();
                    break;
                }
            }
            auth.logout(raw, res);
        }
    }
}
