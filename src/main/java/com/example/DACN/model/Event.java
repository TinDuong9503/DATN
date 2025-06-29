package com.example.DACN.model;

import com.example.DACN.model.status.EventStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data // Đã bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode
@Builder
@AllArgsConstructor

public class Event {
    @Id
    @Column(unique = true, nullable = false)
    private String eventId;

    private String donateId;

    private String donatePlace;
    private String donateAddress;
    private String title;
    private String description;

    private LocalDate donateDate;
    private LocalTime donateStartTime; // Thay cho donateDate + donateTimeStart
    private LocalTime donateEndTime;   // Thay cho donateTimeEnd

    private int isUrgent; //1: cần gấp, 0: bình thường

    private int eventType;//1: hoạt động, 0: đã đóng

    private String ref;
    private String privateUrl;

    private int maxRegistrations;
    private Long currentRegistrations;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_unit_id", nullable = false)
    private DonationUnit donationUnit;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonateTimeSlot> donationTimeSlots = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "blood_quota_id", referencedColumnName = "id")
    private BloodQuota bloodQuota;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BloodRegistration bloodRegistration;


    public Event() {
        bloodRegistration = new BloodRegistration();
    }

    public String getEventName() {
        return donationUnit != null ? "Hiến máu " + donationUnit.getDonationPlace() : " ";
    }

    public String getEventByUnitName() {
        return donationUnit != null ? donationUnit.getUnit() : "Không tìm thấy ";
    }

    public String getLocation() {
        return donationUnit != null ? donationUnit.getLocation() : " ";
    }



}
