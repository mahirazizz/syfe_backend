package com.syfe.pfm.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateGoalRequest(
        @NotNull BigDecimal targetAmount,
        @NotNull LocalDate targetDate) {
}
