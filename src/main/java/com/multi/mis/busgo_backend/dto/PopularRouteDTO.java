package com.multi.mis.busgo_backend.dto;

public class PopularRouteDTO {
    private String routeName;
    private Long bookingCount;

    public PopularRouteDTO(String routeName, Long bookingCount) {
        this.routeName = routeName;
        this.bookingCount = bookingCount;
    }

    // Getters and Setters
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Long getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(Long bookingCount) {
        this.bookingCount = bookingCount;
    }
}