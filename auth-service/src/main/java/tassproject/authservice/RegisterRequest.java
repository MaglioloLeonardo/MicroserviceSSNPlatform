package tassproject.authservice;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tassproject.authservice.Role;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull Role role) {}
