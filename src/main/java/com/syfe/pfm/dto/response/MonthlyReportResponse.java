package com.syfe.pfm.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyReportResponse(int month, int year, Map<String, BigDecimal> totalIncome, Map<String, BigDecimal> totalExpenses, BigDecimal netSavings) {
}
