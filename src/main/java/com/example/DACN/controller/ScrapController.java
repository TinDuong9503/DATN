package com.example.DACN.controller;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.EventDTO;
import com.example.DACN.model.Event;
import com.example.DACN.service.interfac.IDonationUnitService;
import com.example.DACN.service.scapperService.BloodDonationScraperService;
import com.google.api.gax.rpc.StatusCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrap")
public class ScrapController {
    private final IDonationUnitService donationUnitService;
    private final BloodDonationScraperService scraperService;

    public ScrapController(IDonationUnitService donationUnitService, BloodDonationScraperService scraperService) {
        this.donationUnitService = donationUnitService;
        this.scraperService = scraperService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> fetchAndSaveEvents(
            @RequestParam long fromDate,
                @RequestParam long toDate) {
        List<EventDTO> savedEvents = scraperService.fetchBloodDonationEvents(fromDate, toDate);
        return ResponseEntity.ok(savedEvents);
    }

    @GetMapping("/units")
    public ApiResponse getUnit(
    ) {
        return donationUnitService.fetchAndSaveDonationUnits();
    }
}
