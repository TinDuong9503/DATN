package com.example.DACN.dto.scapper;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonateAcceptTimeDTO {
    private Long donateLimitId;
    private String donateId;
    private Integer donateAcceptTime;
    private Integer donateStopTime;
    private Integer maxLimitDonate;
    private Integer currentReg;
}
