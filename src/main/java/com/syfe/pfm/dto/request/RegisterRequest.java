package com.syfe.pfm.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @Email @NotBlank String username,
        @NotBlank String password,
        @NotBlank String fullName,
        @NotBlank String phoneNumber) {
}
