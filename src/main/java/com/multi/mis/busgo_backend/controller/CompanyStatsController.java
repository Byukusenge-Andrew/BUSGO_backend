package com.multi.mis.busgo_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multi.mis.busgo_backend.model.CompanyStats;
import com.multi.mis.busgo_backend.service.CompanyStatsService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CompanyStatsController {

    @Autowired
    private CompanyStatsService companyStatsService;

    @GetMapping("/company-stats")
    public ResponseEntity<CompanyStats> getCompanyStats() {
        return ResponseEntity.ok(companyStatsService.getCompanyStats());
    }
}