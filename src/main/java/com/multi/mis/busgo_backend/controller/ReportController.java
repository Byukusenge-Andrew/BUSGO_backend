package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.BusCompany;
import com.multi.mis.busgo_backend.model.User;
import com.multi.mis.busgo_backend.service.BusCompanyService;
import com.multi.mis.busgo_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private UserService userService;

    @Autowired
    private BusCompanyService busCompanyService;

    @GetMapping("/users-companies")
    public ResponseEntity<String> generateUsersCompaniesReport() {
        List<User> users = userService.getAllUsers();
        List<BusCompany> companies = busCompanyService.getAllCompanies();

        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);

        // Add CSV header
        writer.println("Type,ID,Username,Email,FirstName,LastName,PhoneNumber,Role,IsActive,CreatedAt,CompanyName,ContactPerson,ContactEmail,ContactPhone,Address,LicenseNumber,RegistrationDate");

        // Add user data
        for (User user : users) {
            writer.printf("User,%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%b,%s,,,,,,,\n",
                    user.getId(),
                    escapeCsv(user.getUsername()),
                    escapeCsv(user.getEmail()),
                    escapeCsv(user.getFirstName()),
                    escapeCsv(user.getLastName()),
                    escapeCsv(user.getPhoneNumber()),
                    escapeCsv(user.getRole()),
                    user.isActive(),
                    user.getCreatedAt()
            );
        }

        // Add company data
        for (BusCompany company : companies) {
            writer.printf("Company,%d,,,,,,,,,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s\n",
                    company.getId(),
                    escapeCsv(company.getCompanyName()),
                    escapeCsv(company.getContactPerson()),
                    escapeCsv(company.getContactEmail()),
                    escapeCsv(company.getContactPhone()),
                    escapeCsv(company.getAddress()),
                    escapeCsv(company.getLicenseNumber()),
                    company.getRegistrationDate()
            );
        }

        writer.flush();
        String csvContent = sw.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "users_companies_report.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape double quotes by doubling them and wrap the field in double quotes
        return value.replace("\"", "\"\"");
    }
} 