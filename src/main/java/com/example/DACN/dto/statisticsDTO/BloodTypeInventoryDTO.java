package com.example.DACN.dto.statisticsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodTypeInventoryDTO {
    private String bloodType;
    private Long quantity;

    public BloodTypeInventoryDTO(String bloodType, Long quantity) {
        this.bloodType = bloodType;
        this.quantity = quantity;
    }

    // Getters and setters
}