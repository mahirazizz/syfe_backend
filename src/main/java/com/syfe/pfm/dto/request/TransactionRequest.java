package com.syfe.pfm.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransactionRequest(
        @NotNull @Positive BigDecimal amount,
        String description,
        @NotBlank String type,
        @NotNull LocalDate date,
        @NotBlank String category) {
}
