package com.example.DACN.controller;


import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.EventDTO;
import com.example.DACN.model.Appointment;
import com.example.DACN.model.Event;
import com.example.DACN.service.impl.EventService;
import jakarta.validation.Valid;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/fetch")
    public ApiResponse fetchEvents() {
        return eventService.fetchAndSaveDonationEvents();
    }
//    @GetMapping("/by-date")
//    public ApiResponse getEventsByDate(@RequestParam String eventDate) {
//        LocalDate date = LocalDate.parse(eventDate);
//        return eventService.getEventsByDate(date);
//    }
    @GetMapping("/by-date-range")
    public ApiResponse getEventsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate
            ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return eventService.getEventsByDateRange(start, end);
    }
    @GetMapping("/by-unit")
    public ApiResponse getEventsByUnit(@RequestParam Long unitId) {
        return eventService.getEventByUnit(unitId);
    }

    @GetMapping("/get/{id}")
    public ApiResponse getEventById(@PathVariable String id) {
        return eventService.getEventById(id);
    }

    @PutMapping("/update/{id}")
    public ApiResponse updateEvent(@PathVariable String id ,@RequestBody @Valid EventDTO eventDTO) {
        return eventService.updateEvent(id, eventDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteEvent(@PathVariable String id ){
        return eventService.removeEvent(id);
    }

}
