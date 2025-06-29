package com.example.DACN.controller;


import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.HealthMetrics;
import com.example.DACN.model.Appointment;
import com.example.DACN.model.status.AppointmentStatus;
import com.example.DACN.repository.AppointmentRepo;
import com.example.DACN.service.impl.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {


    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("")
    public ResponseEntity<ApiResponse> getAll(){
            return ResponseEntity.ok(appointmentService.getAllAppointment());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id){
        return  ResponseEntity.ok(appointmentService.getById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse> saveAppointment(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "eventId") String eventId,
            @RequestBody Map<String, Map<String, Boolean>> requestBody) {
        System.out.println("Username: " + username);
        System.out.println("Event ID: " + eventId);
        System.out.println("Health Metrics: " + requestBody);
        Map<String, Boolean> healthMetrics = requestBody.get("healthMetrics");
        HealthMetrics health  = new HealthMetrics(healthMetrics);

        ApiResponse response = appointmentService.saveAppointment(username, eventId, health);
        System.out.println("Kết quả: " + response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-user")
    public ResponseEntity<ApiResponse> getUserAppointment(@RequestParam String username){
        return ResponseEntity.ok(appointmentService.getUserAppointments(username));
    }
    @GetMapping("/by-user-pending")
    public ResponseEntity<ApiResponse> getAppointmentPendingUser(@RequestParam String username){
        return ResponseEntity.ok(appointmentService.getAppointmentPendingUser(username));
    }

    @PutMapping("/status")
    public ResponseEntity<ApiResponse> updateAppointmentStatus(@RequestParam Long id, @RequestParam String status){

        AppointmentStatus newStatus = AppointmentStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id,newStatus));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.deleteAppointment(id));

    }
}
