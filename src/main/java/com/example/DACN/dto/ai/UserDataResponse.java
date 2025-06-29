package com.example.DACN.dto.ai;

import com.example.DACN.model.Appointment;
import com.example.DACN.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDataResponse {
    private UserInfo userInfo;

    private List<Appointment> upcomingAppointments;

    // Constructors, getters, setters
}