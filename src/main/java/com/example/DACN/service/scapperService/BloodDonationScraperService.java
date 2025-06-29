package com.example.DACN.service.scapperService;

import com.example.DACN.dto.EventDTO;
import com.example.DACN.model.DonationUnit;
import com.example.DACN.model.Event;
import com.example.DACN.model.status.EventStatus;
import com.example.DACN.repository.DonationUnitRepo;
import com.example.DACN.repository.EventRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class BloodDonationScraperService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "https://api.giotmauvang.org.vn/public/calendar";
    private final EventRepo eventRepo;
    private final DonationUnitRepo unitRepo;
    public BloodDonationScraperService(EventRepo eventRepo, DonationUnitRepo unitRepo) {
        this.eventRepo = eventRepo;
        this.unitRepo = unitRepo;
    }

    public List<EventDTO> fetchBloodDonationEvents(long fromDate, long toDate) {
        // Tạo body request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("what", "donateCalendar");
        requestBody.put("from", 0);
        requestBody.put("limit", 100);
        requestBody.put("order", "donateDate");
        requestBody.put("by", "asc");
        requestBody.put("fromDate", fromDate);
        requestBody.put("toDate", toDate);

        // Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode events = root.path("data").path("list");

            List<EventDTO> eventList = new ArrayList<>();
            if (events.isArray()) {
                for (JsonNode node : events) {
                    try {
                        EventDTO eventDto = objectMapper.treeToValue(node, EventDTO.class);
                        eventList.add(eventDto);
                    } catch (Exception e) {
                        System.out.println("Lỗi ánh xạ 1 record: " + node.toPrettyString());
                        e.printStackTrace();
                    }
                }
            }
            saveEvents(eventList);
            return eventList;

        } catch (Exception e) {
            System.out.println("Lỗi gọi API hoặc parse JSON:");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void saveEvents(List<EventDTO> dtoList) {

        List<Event> entities = new ArrayList<>();
        for (EventDTO dto : dtoList) {
            Event event = new Event();
            event.setEventId(dto.getId());
            event.setDonateId(dto.getDonateId());
            event.setDonatePlace(dto.getDonatePlace());
            event.setDonateAddress(dto.getDonateAddress());
            event.setDonateDate(dto.getDonateDate());
            event.setDonateStartTime(dto.getEventStartTime());
            event.setDonateEndTime(dto.getEventEndTime());
            event.setEventStatus(EventStatus.ACTIVE);
            event.setMaxRegistrations(dto.getMaxRegistrations());
            event.setDonateDate(dto.getDonateDate());
            event.setCurrentRegistrations(dto.getCurrentRegistrations());

            Optional<DonationUnit>  unit = unitRepo.findByUnit(dto.getUnit());
            unit.ifPresent(event::setDonationUnit);

            entities.add(event);
        }

        eventRepo.saveAll(entities);
    }

}
