package com.example.DACN.dto;


import com.example.DACN.model.DonateTimeSlot;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDTO {
    private String id;

    private String title;
    private String donateId;
    private String donatePlace;
    private String donateAddress;
    private LocalDate donateDate;
    private LocalTime eventStartTime;
    private LocalTime eventEndTime;

    private int maxRegistrations;
    private Long currentRegistrations;
    private String status;
    private String unit;

    private BloodQuotaDTO bloodQuotaDTO;

    private DonationUnitDTO donationUnitDTO;
    private List<DonationTimeSlotDTO> donationTimeSlotDTO;
    private List<AppointmentDTO>  appointments;
}
