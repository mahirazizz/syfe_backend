package com.syfe.pfm.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record GoalRequest(
        @NotBlank String goalName,
        @NotNull BigDecimal targetAmount,
        @NotNull @PositiveOrZero BigDecimal currentAmount,
        @NotNull LocalDate targetDate,
        java.time.LocalDate startDate) {
}
