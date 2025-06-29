package com.example.DACN.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonateTimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private Integer donateAcceptTime;  // Giờ mở đăng ký (giây từ 00:00)
    private Integer donateStopTime;    // Giờ đóng đăng ký (giây từ 00:00)

    private Integer maxLimitDonate;
    private Integer currentReg;
}
