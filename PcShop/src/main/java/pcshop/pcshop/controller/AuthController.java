package pcshop.pcshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcshop.pcshop.dto.ApiMessage;
import pcshop.pcshop.dto.RegisterRequest;
import pcshop.pcshop.dto.VerifyEmailRequest;
import pcshop.pcshop.service.AuthService;

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
}
