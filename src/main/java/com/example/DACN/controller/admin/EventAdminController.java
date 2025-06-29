package com.example.DACN.controller.admin;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.EventDTO;
import com.example.DACN.service.impl.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class EventAdminController {

    @Autowired
    EventService eventService = new EventService();

    @GetMapping("/events/get-all")
    public ApiResponse getAllEvent(){
        return eventService.getAllEvent();
    }

    @PostMapping("/events/add")
    public ApiResponse addEvent(@RequestBody EventDTO eventDTO){
        return eventService.addEvent(eventDTO);
    }
}
