package com.example.DACN.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloodRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;


    private Integer regBloodA = 0;
    private Integer regBloodB = 0;
    private Integer regBloodAB = 0;
    private Integer regBloodO = 0;

    private Integer regNegativeBloodA = 0;
    private Integer regNegativeBloodB = 0;
    private Integer regNegativeBloodAB = 0;
    private Integer regNegativeBloodO = 0;
}
