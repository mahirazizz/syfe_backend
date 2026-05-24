package com.syfe.pfm.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record YearlyReportResponse(int year, Map<String, BigDecimal> totalIncome, Map<String, BigDecimal> totalExpenses, BigDecimal netSavings) {
}
