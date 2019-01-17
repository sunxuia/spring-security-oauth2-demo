package net.sunxu.study.cb.client3;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtils {

    @Autowired
    private JwtProperties jwtProperties;

    public Claims getClaims(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
        return claims;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).parseClaimsJws(token).getBody();
            return true;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }
}
