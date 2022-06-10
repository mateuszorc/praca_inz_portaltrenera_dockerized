package com.example.portaltrenera.registration;

import com.example.portaltrenera.model.User;
import com.example.portaltrenera.payload.request.LoginRequest;
import com.example.portaltrenera.payload.response.MessageResponse;
import com.example.portaltrenera.payload.response.UserInfoResponse;
import com.example.portaltrenera.registration.service.RegistrationService;
import com.example.portaltrenera.security.jwt.JwtUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = {"/api"})
public class RegistrationController {

    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private static final Logger LOGGER = LogManager.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationService registrationService,
                                  AuthenticationManager authenticationManager,
                                  JwtUtils jwtUtils) {
        this.registrationService = registrationService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();

        String token = UUID.randomUUID().toString().replace("-","");
        CsrfToken csrfToken = new DefaultCsrfToken("X-XSRF-TOKEN", "_csrf", token);
        ResponseCookie responseJwtCookie = jwtUtils.generateJwtCookie(userDetails, csrfToken);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        LOGGER.info(userDetails.getAuthorities()
                + " - (id): " + userDetails.getId()
                + " email: " + userDetails.getUsername()
                + " logged in.");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseJwtCookie.toString())
                .header(csrfToken.getHeaderName(), csrfToken.getToken())
                .body(new UserInfoResponse(
                        userDetails.getId(),
                        userDetails.getFirstName(),
                        userDetails.getLastName(),
                        userDetails.getUsername(),
                        roles)
                );
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(new MessageResponse("You have been logged out!"));
    }

    @PostMapping("/registration")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = {"/registration/confirm"})
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

}