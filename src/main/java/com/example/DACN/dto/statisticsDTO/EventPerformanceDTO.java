package com.example.DACN.dto.statisticsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventPerformanceDTO {
    private String eventId;
    private String title;
    private Integer goalBloodBag;
    private Long actualDonations;

    public EventPerformanceDTO(String eventId, String title, Integer goalBloodBag, Long actualDonations) {
        this.eventId = eventId;
        this.title = title;
        this.goalBloodBag = goalBloodBag;
        this.actualDonations = actualDonations;
    }

    // Getters and setters
}
