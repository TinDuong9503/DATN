package com.example.DACN.service.impl;

import com.example.DACN.dto.statisticsDTO.BloodTypeInventoryDTO;
import com.example.DACN.dto.statisticsDTO.EventPerformanceDTO;
import com.example.DACN.dto.statisticsDTO.GenderStatsDTO;
import com.example.DACN.dto.statisticsDTO.UserDonationStatsDTO;
import com.example.DACN.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public List<UserDonationStatsDTO> getDonationsByUser() {
        return statisticsRepository.getUserDonationStats();
    }

    public List<GenderStatsDTO> getGenderDistribution() {
        return statisticsRepository.getGenderDistribution();
    }

    public List<BloodTypeInventoryDTO> getBloodInventoryStats() {
        return statisticsRepository.getBloodInventoryStats();
    }

    public List<EventPerformanceDTO> getEventPerformance() {
        return statisticsRepository.getEventPerformance();
    }
}