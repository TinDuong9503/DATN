package com.example.DACN.dto   ;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BloodQuotaDTO {
    private Integer goalIBloodBag;
    private Integer minIBloodBag;
    private Integer maxIBloodBag;
    private Integer additionalIBloodBag;
}
