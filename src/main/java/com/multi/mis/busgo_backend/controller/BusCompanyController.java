package com.multi.mis.busgo_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.service.BusCompanyService;

@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "*")
public class BusCompanyController {

    @Autowired
    private BusCompanyService busCompanyService;
    
    @PostMapping
    public ResponseEntity<BusCompany> createCompany(@RequestBody BusCompany company) {
        return ResponseEntity.ok(busCompanyService.createCompany(company));
    }
    
    @GetMapping
    public ResponseEntity<List<BusCompany>> getBusCompanies() {
        return ResponseEntity.ok(busCompanyService.getAllCompanies());
    }
    
    @GetMapping("/{companyId}")
    public ResponseEntity<?> getBusCompanyById(@PathVariable Long companyId) {
        return busCompanyService.getCompanyById(companyId)
                .map(company -> ResponseEntity.ok(company))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable Long companyId, @RequestBody BusCompany company) {
        return busCompanyService.updateCompany(companyId, company)
                .map(updatedCompany -> ResponseEntity.ok(updatedCompany))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> deleteBusCompany(@PathVariable Long companyId) {
        boolean deleted = busCompanyService.deleteCompany(companyId);

        if (deleted) {
            return ResponseEntity.ok(
                java.util.Collections.singletonMap("message", "Company deleted successfully")
            );
        } else {
            return ResponseEntity.status(404).body(
                java.util.Collections.singletonMap("message", "Company not found")
            );
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody BusCompany company) {
        return busCompanyService.login(company.getCompanyName(), company.getPassword())
                .map(c -> ResponseEntity.ok().body(Map.of("message", "Login successful")))
                .orElse(ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials")));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BusCompany>> searchCompanies(@RequestParam String query) {
        return ResponseEntity.ok(busCompanyService.searchCompanies(query));
    }
}