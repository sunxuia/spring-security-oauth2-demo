package net.sunxu.study.cb.client3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(jwtProperties.getTokenHeaderName());
        if (authHeader != null && authHeader.startsWith(jwtProperties.getTokenPrefix())) {
            final String authToken = authHeader.substring(jwtProperties.getTokenPrefix().length()); // The part after "Bearer "
            if (jwtTokenUtils.isTokenValid(authToken)) {
                var claims = jwtTokenUtils.getClaims(authToken);
                var userName = claims.get("user_name", String.class);
                var auths = claims.get("authorities", List.class);
                for (int i = 0; i < auths.size(); i++) {
                    auths.set(i, new SimpleGrantedAuthority((String) auths.get(i)));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userName, null, auths);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
