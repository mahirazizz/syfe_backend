@Service
package com.syfe.pfm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.syfe.pfm.dto.response.MonthlyReportResponse;
import com.syfe.pfm.dto.response.YearlyReportResponse;
import com.syfe.pfm.entity.Transaction;
import com.syfe.pfm.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlyReportResponse getMonthlyReport(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Transaction> txns = transactionRepository.findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(userId, start, end);

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : txns) {
            String cat = t.getCategory().getName();
            BigDecimal amt = t.getAmount();
            if ("INCOME".equalsIgnoreCase(t.getType())) {
                incomeByCategory.merge(cat, amt, BigDecimal::add);
                totalIncome = totalIncome.add(amt);
            } else {
                expensesByCategory.merge(cat, amt, BigDecimal::add);
                totalExpense = totalExpense.add(amt);
            }
        }

        BigDecimal net = totalIncome.subtract(totalExpense);
        return new MonthlyReportResponse(month, year, incomeByCategory, expensesByCategory, net);
    }

    public YearlyReportResponse getYearlyReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Transaction> txns = transactionRepository.findByUserIdAndTransactionDateBetweenAndDeletedFalseOrderByTransactionDateDesc(userId, start, end);

        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        Map<String, BigDecimal> expensesByCategory = new HashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction t : txns) {
            String cat = t.getCategory().getName();
            BigDecimal amt = t.getAmount();
            if ("INCOME".equalsIgnoreCase(t.getType())) {
                incomeByCategory.merge(cat, amt, BigDecimal::add);
                totalIncome = totalIncome.add(amt);
            } else {
                expensesByCategory.merge(cat, amt, BigDecimal::add);
                totalExpense = totalExpense.add(amt);
            }
        }

        BigDecimal net = totalIncome.subtract(totalExpense);
        return new YearlyReportResponse(year, incomeByCategory, expensesByCategory, net);
    }
}
