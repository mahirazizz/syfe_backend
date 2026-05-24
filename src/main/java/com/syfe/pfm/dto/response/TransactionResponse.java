package com.syfe.pfm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(Long id, BigDecimal amount, LocalDate date, String category, String description, String type) {
}
package com.syfe.pfm.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        String type,
        LocalDate transactionDate,
        Long categoryId,
        Long userId) {
}
