package com.example.DACN.dto.scapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BloodDonationEventDTO {
    private String donateId;
    private String donatePlace;
    private Long donateDate;
    private String donateTimeStart;
    private String donateTimeEnd;
    private List<DonateAcceptTimeDTO> donateAcceptTimes;
}
