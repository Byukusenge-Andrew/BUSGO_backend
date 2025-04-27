package com.multi.mis.busgo_backend.model;

public class CompanyStats {
    private int totalCompanies;
    private int activeCompanies;
    private int pendingCompanies;
    private int suspendedCompanies;
    private int totalBuses;
    private int totalRoutes;

    public CompanyStats() {
    }

    public CompanyStats(int totalCompanies, int activeCompanies, int pendingCompanies,
                        int suspendedCompanies, int totalBuses, int totalRoutes) {
        this.totalCompanies = totalCompanies;
        this.activeCompanies = activeCompanies;
        this.pendingCompanies = pendingCompanies;
        this.suspendedCompanies = suspendedCompanies;
        this.totalBuses = totalBuses;
        this.totalRoutes = totalRoutes;
    }

    public int getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(int totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public int getActiveCompanies() {
        return activeCompanies;
    }

    public void setActiveCompanies(int activeCompanies) {
        this.activeCompanies = activeCompanies;
    }

    public int getPendingCompanies() {
        return pendingCompanies;
    }

    public void setPendingCompanies(int pendingCompanies) {
        this.pendingCompanies = pendingCompanies;
    }

    public int getSuspendedCompanies() {
        return suspendedCompanies;
    }

    public void setSuspendedCompanies(int suspendedCompanies) {
        this.suspendedCompanies = suspendedCompanies;
    }

    public int getTotalBuses() {
        return totalBuses;
    }

    public void setTotalBuses(int totalBuses) {
        this.totalBuses = totalBuses;
    }

    public int getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(int totalRoutes) {
        this.totalRoutes = totalRoutes;
    }
}