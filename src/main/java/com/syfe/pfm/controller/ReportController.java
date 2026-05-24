package com.syfe.pfm.controller;

import com.syfe.pfm.dto.response.MonthlyReportResponse;
import com.syfe.pfm.dto.response.YearlyReportResponse;
import com.syfe.pfm.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public MonthlyReportResponse getMonthlyReport(@PathVariable int year, @PathVariable int month, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        return reportService.getMonthlyReport(userId, year, month);
    }

    @GetMapping("/yearly/{year}")
    public YearlyReportResponse getYearlyReport(@PathVariable int year, javax.servlet.http.HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        return reportService.getYearlyReport(userId, year);
    }
}
