package com.example.DACN.dto.statisticsDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDonationStatsDTO {
    private String cccd;
    private String fullName;
    private Long totalDonations;

    public UserDonationStatsDTO(String cccd, String fullName, Long totalDonations) {
        this.cccd = cccd;
        this.fullName = fullName;
        this.totalDonations = totalDonations;
    }

    // Getters and setters
}