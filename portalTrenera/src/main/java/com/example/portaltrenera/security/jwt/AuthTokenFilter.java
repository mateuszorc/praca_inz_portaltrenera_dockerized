package com.example.portaltrenera.security.jwt;

import com.example.portaltrenera.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    private final DefaultRequiresCsrfMatcher requiresCsrfMatcher = new DefaultRequiresCsrfMatcher();

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    private static final Logger LOGGER = LogManager.getLogger(AuthTokenFilter.class);

    public AuthTokenFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            boolean loginPath = request.getServletPath().equals("/api/login");
            boolean logoutPath = request.getServletPath().equals("/api/logout");
            boolean registrationPath = request.getServletPath().equals("/api/registration/confirm");

            if (requiresCsrfMatcher.matches(request) && !loginPath && !logoutPath && !registrationPath) {
                if ( jwt != null && jwtUtils.validateJwtToken(jwt) && checkIfCsrfMatch(request)) {
                    String username = jwtUtils.getUserNameFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails,
                                    null,
                                    userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new CsrfException("Csrf tokens do not match!!!");
                }
            }
            else if (jwt != null && jwtUtils.validateJwtToken(jwt) ) {
                String username = jwtUtils.getUserNameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication: " + e);
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookies(request);
    }

    private boolean checkIfCsrfMatch(HttpServletRequest request) {
        String jwt = parseJwt(request);
        String csrfHeader = "";
        if (request.getHeader("X-XSRF-TOKEN") != null) {
            csrfHeader = request.getHeader("X-XSRF-TOKEN").replace("\"", "");
        }
        String csrfCookie = JwtUtils.GetClaims(jwt);

        return csrfHeader.equals(csrfCookie);
    }
}
