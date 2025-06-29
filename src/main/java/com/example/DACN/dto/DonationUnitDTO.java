package com.example.DACN.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DonationUnitDTO {
    private long id;
    private String unit ;

    private String donationPlace ;
    private String location;

    private String phone ;

    private String email;

    private String unitPhotoUrl;

    private List<EventDTO> events ;

}
