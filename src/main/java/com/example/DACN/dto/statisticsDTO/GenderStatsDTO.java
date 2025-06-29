package com.example.DACN.dto.statisticsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenderStatsDTO {
    private String sex;
    private Long total;

    public GenderStatsDTO(String sex, Long total) {
        this.sex = sex;
        this.total = total;
    }

    // Getters and setters
}