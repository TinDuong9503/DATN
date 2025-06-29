package com.example.DACN.repository;

import com.example.DACN.dto.statisticsDTO.BloodTypeInventoryDTO;
import com.example.DACN.dto.statisticsDTO.EventPerformanceDTO;
import com.example.DACN.dto.statisticsDTO.GenderStatsDTO;
import com.example.DACN.dto.statisticsDTO.UserDonationStatsDTO;
import com.example.DACN.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<User, String> {

    @Query("SELECT new com.example.DACN.dto.statisticsDTO.UserDonationStatsDTO(u.username, ui.fullName, COUNT(h)) " +
            "FROM BloodDonationHistory h " +
            "JOIN h.user u " +
            "JOIN u.userInfo ui " +
            "GROUP BY u.username, ui.fullName")
    List<UserDonationStatsDTO> getUserDonationStats();

    @Query("SELECT new com.example.DACN.dto.statisticsDTO.GenderStatsDTO(ui.sex, COUNT(ui)) " +
            "FROM UserInfo ui GROUP BY ui.sex")
    List<GenderStatsDTO> getGenderDistribution();

    @Query("SELECT new com.example.DACN.dto.statisticsDTO.BloodTypeInventoryDTO(b.bloodType, COUNT(b)) " +
            "FROM BloodInventory b WHERE b.expirationDate >= CURRENT_DATE GROUP BY b.bloodType")
    List<BloodTypeInventoryDTO> getBloodInventoryStats();

    @Query("SELECT new com.example.DACN.dto.statisticsDTO.EventPerformanceDTO(e.eventId, e.title, q.goalBloodBag, COUNT(a)) " +
            "FROM Event e LEFT JOIN e.bloodQuota q LEFT JOIN Appointment a ON a.event.eventId = e.eventId " +
            "GROUP BY e.eventId, e.title, q.goalBloodBag")
    List<EventPerformanceDTO> getEventPerformance();
}
