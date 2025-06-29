package com.example.DACN.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DonationTimeSlotDTO {
    private Integer donateAcceptTime;
    private Integer donateStopTime  ;
    private Integer maxLimitDonate;
    private Integer currentReg;
}
