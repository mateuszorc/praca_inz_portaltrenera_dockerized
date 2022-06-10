package com.example.portaltrenera.registration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RegistrationRequest {
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @NotNull
    private final String email;
    @NotBlank
    private final String password;

    public RegistrationRequest(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}