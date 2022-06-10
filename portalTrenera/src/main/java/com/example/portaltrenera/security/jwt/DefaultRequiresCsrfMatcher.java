package com.example.portaltrenera.security.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;


public class DefaultRequiresCsrfMatcher implements RequestMatcher {
    private final Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
    @Override
    public boolean matches(HttpServletRequest request) {
        return !allowedMethods.matcher(request.getMethod()).matches();
    }
}
