package pcshop.pcshop.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpHeaders;


import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(auth != null && auth.startsWith("Bearer ")){
            String token = auth.substring(7);
            try{
                Jws<Claims> jws = jwtService.parse(token);
                Claims c = jws.getPayload();
                String userId = c.getSubject();
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) c.get("roles");
                var authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
                var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception ignored){
                /* invalid token -> no auth set */
            }
            filterChain.doFilter(request, response);
        }
    }
}
