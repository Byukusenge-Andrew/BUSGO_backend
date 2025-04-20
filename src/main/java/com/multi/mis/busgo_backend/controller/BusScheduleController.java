package com.multi.mis.busgo_backend.controller;

import com.multi.mis.busgo_backend.model.BusSchedule;
import com.multi.mis.busgo_backend.service.BusScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class BusScheduleController {

    @Autowired
    private BusScheduleService busScheduleService;
    
    @GetMapping
    public ResponseEntity<List<BusSchedule>> getBusSchedules() {
        return ResponseEntity.ok(busScheduleService.getAllSchedules());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<BusSchedule>> searchBus(
            @RequestParam Long sourceId,
            @RequestParam Long destId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBus(sourceId, destId, departureDate));
    }
    
    @GetMapping("/search-by-city")
    public ResponseEntity<List<BusSchedule>> searchBusByCity(
            @RequestParam String sourceCity,
            @RequestParam String destCity,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date departureDate) {
        return ResponseEntity.ok(busScheduleService.searchBusByCity(sourceCity, destCity, departureDate));
    }
    
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getBusScheduleById(@PathVariable Long scheduleId) {
        return busScheduleService.getScheduleById(scheduleId)
                .map(schedule -> ResponseEntity.ok(schedule))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{scheduleId}")
    public ResponseEntity<?> updateBusSchedule(
            @PathVariable Long scheduleId, 
            @RequestBody BusSchedule schedule) {
        return busScheduleService.updateSchedule(scheduleId, schedule)
                .map(updatedSchedule -> ResponseEntity.ok(updatedSchedule))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<BusSchedule> createBusSchedule(@RequestBody BusSchedule schedule) {
        return ResponseEntity.ok(busScheduleService.createSchedule(schedule));
    }
    
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<?> deleteBusSchedule(@PathVariable Long scheduleId) {
        boolean deleted = busScheduleService.deleteSchedule(scheduleId);
        
        if (deleted) {
            return ResponseEntity.ok("Schedule deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 