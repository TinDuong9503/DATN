package com.example.DACN.controller;

import com.example.DACN.dto.statisticsDTO.BloodTypeInventoryDTO;
import com.example.DACN.dto.statisticsDTO.EventPerformanceDTO;
import com.example.DACN.dto.statisticsDTO.GenderStatsDTO;
import com.example.DACN.dto.statisticsDTO.UserDonationStatsDTO;
import com.example.DACN.service.impl.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {


    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/donations-by-user")
    public ResponseEntity<List<UserDonationStatsDTO>> getDonationsByUser() {
        List<UserDonationStatsDTO> data = statisticsService.getDonationsByUser();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/gender-distribution")
    public ResponseEntity<List<GenderStatsDTO>> getGenderDistribution() {
        List<GenderStatsDTO> data = statisticsService.getGenderDistribution();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/blood-inventory")
    public ResponseEntity<List<BloodTypeInventoryDTO>> getBloodInventory() {
        List<BloodTypeInventoryDTO> data = statisticsService.getBloodInventoryStats();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/event-performance")
    public ResponseEntity<List<EventPerformanceDTO>> getEventPerformance() {
        List<EventPerformanceDTO> data = statisticsService.getEventPerformance();
        return ResponseEntity.ok(data);
    }
}