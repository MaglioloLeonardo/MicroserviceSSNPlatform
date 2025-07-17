package tassproject.authservice;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

public record RegisterRequest(
        /* Credenziali */
        @NotBlank @Email String email,
        @NotBlank           String password,
        @NotNull            Role   role,
        /* Campi anagrafica */
        @NotBlank String  cf,
        @NotBlank String  nome,
        @NotBlank String  cognome,
        @Past     LocalDate dataNascita,
        @NotBlank String  luogoNascita,
        @NotBlank String  cittaResidenza
) {}