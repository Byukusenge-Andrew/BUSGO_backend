package com.multi.mis.busgo_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.multi.mis.busgo_backend.model.CompanyStats;
import com.multi.mis.busgo_backend.repository.BusCompanyRepository;
import com.multi.mis.busgo_backend.repository.BusRepository;
import com.multi.mis.busgo_backend.repository.RouteRepository;

@Service
public class CompanyStatsService {

    @Autowired
    private BusCompanyRepository busCompanyRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RouteRepository routeRepository;

    public CompanyStats getCompanyStats() {
        CompanyStats stats = new CompanyStats();

        // Count total companies
        stats.setTotalCompanies((int) busCompanyRepository.count());

        // Count active companies
        stats.setActiveCompanies(busCompanyRepository.findByActive(true).size());

        // For pending and suspended companies, you might need to add status field to BusCompany
        // For now, we'll set them to 0
        stats.setPendingCompanies(0);
        stats.setSuspendedCompanies(0);

        // Count total buses
        stats.setTotalBuses((int) busRepository.count());

        // Count total routes
        stats.setTotalRoutes((int) routeRepository.count());

        return stats;
    }
}