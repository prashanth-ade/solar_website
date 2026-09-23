package com.solarflow.security;
import jakarta.servlet.*; import jakarta.servlet.http.*; import io.jsonwebtoken.JwtException; import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;
@Component public class JwtAuthFilter extends OncePerRequestFilter {
    final JwtService jwt; JwtAuthFilter(JwtService j){jwt=j;}
    protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
        String h=req.getHeader("Authorization");
        if(h!=null&&h.startsWith("Bearer ")) {
            try {
                var c=jwt.parse(h.substring(7));
                var a=new UsernamePasswordAuthenticationToken(c.getSubject(),null,List.of(new SimpleGrantedAuthority("ROLE_"+c.get("role",String.class))));
                SecurityContextHolder.getContext().setAuthentication(a);
            } catch(JwtException e) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"timestamp\":\""+java.time.Instant.now()+"\",\"error\":\"Invalid or expired token\",\"status\":401}");
                return;
            }
        }
        chain.doFilter(req,res);
    }
}
