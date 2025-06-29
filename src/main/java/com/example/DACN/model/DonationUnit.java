package com.example.DACN.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DonationUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String unit ;


    private String donationPlace;

    private String location;

    private String phone ;

    private String email;

    private String unitPhotoUrl;

    @OneToMany(mappedBy = "donationUnit", fetch = FetchType.LAZY)
    private List<Event> events;

}
