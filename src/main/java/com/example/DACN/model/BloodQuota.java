package com.example.DACN.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodQuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "bloodQuota")
    private Event event;

    private Integer goalBloodBag;
    private Integer minBloodBag;
    private Integer maxBloodBag;
    private Integer additionalBloodBag;
}
