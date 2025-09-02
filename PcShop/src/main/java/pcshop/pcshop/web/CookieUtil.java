package pcshop.pcshop.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtil {
    public static final String REFRESH_COOKIE = "refresh_token";

    public static void serRefresh(HttpServletResponse response, String value, int maxAgeSec){
        Cookie c = new Cookie(REFRESH_COOKIE, value);
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setPath("/api/auth");
        c.setMaxAge(maxAgeSec);
        c.setAttribute("SameSite","Lax");
        response.addCookie(c);
    }

    public static void clearRefresh(HttpServletResponse response){
        Cookie c = new Cookie(REFRESH_COOKIE, "");
        c.setHttpOnly(true);
        c.setSecure(false);
        c.setPath("/api/auth");
        c.setMaxAge(0);
        c.setAttribute("SameSite","Lax");
        response.addCookie(c);
    }

    private CookieUtil(){}
}

