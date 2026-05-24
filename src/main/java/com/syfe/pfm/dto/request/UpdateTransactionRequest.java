package com.syfe.pfm.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateTransactionRequest(
        @NotNull @Positive BigDecimal amount,
        @NotBlank String description) {
}
