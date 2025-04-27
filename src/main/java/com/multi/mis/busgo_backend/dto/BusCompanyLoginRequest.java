package com.multi.mis.busgo_backend.dto;

public class BusCompanyLoginRequest {
    private String email;
    private String password;

    public BusCompanyLoginRequest() {}

    public BusCompanyLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}