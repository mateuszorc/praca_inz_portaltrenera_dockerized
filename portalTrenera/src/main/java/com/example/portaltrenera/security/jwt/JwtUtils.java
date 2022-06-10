package com.example.portaltrenera.security.jwt;

import com.example.portaltrenera.model.User;
import com.example.portaltrenera.onstartup.CommandLineAppStartupRunner;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger LOGGER = LogManager.getLogger(JwtUtils.class);

    @Value("${portalTrenera.jwt.secret}")
    private String jwtSecret;
    @Value("${portalTrenera.jwt.expiration}")
    private int jwtExpiration;
    @Value("${portalTrenera.jwt.cookieName}")
    private String jwtCookie;

    public static String GetClaims(String token) {
        String claims = Jwts.parser().setSigningKey("jwtStudentSecretKey").parseClaimsJws(token).getBody().toString();
        int index = claims.indexOf("token=");
        return claims.substring(index + 6, index + 38);
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public ResponseCookie generateJwtCookie(User userPrincipal, CsrfToken csrfToken) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername(), csrfToken);

        return ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(60 * 60).httpOnly(true).build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, "").path("/api").build();
    }

    public boolean validateJwtToken(String jwtAuthToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtAuthToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("JWT token is malformed: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.info("JWT token has expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.warn("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String getUserNameFromToken(String jwtToken) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken).getBody().getSubject();
    }

    public String generateTokenFromUsername(String username, CsrfToken csrfToken) {
        return Jwts.builder()
                .setSubject(username)
                .claim("_csrf", csrfToken)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

}
