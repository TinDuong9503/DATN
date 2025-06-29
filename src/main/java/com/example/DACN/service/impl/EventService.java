
package com.example.DACN.service.impl;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.EventDTO;
import com.example.DACN.exception.OurException;
import com.example.DACN.model.*;
import com.example.DACN.model.status.EventStatus;
import com.example.DACN.repository.*;
import com.example.DACN.service.interfac.IEventService;
import com.example.DACN.service.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EventService implements IEventService {
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private DonationUnitRepo donationUnitRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private BloodInventoryRepo bloodInventoryRepo;

    @Autowired
    private BloodQuotaRepository bloodQuotaRepo;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://api.giotmauvang.org.vn/public/calendar";


    public ApiResponse getAllEvent(){
        ApiResponse response = new ApiResponse();
        List<Event> events = eventRepo.findAll();
        try{
            if (events.toArray().length > 0){
                List<EventDTO> dto = Utils.mapEventListEntityToDTO(events);
                response.setCode(200);
                response.setMessage("Success");
                response.setEventDTOList(dto);
                return response;
            }

        }catch(Exception e){
            response.setCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
        return response;
    }



    @Override
    public ApiResponse fetchAndSaveDonationEvents() {
        ApiResponse apiResponse = new ApiResponse();

        try {
            // Tạo headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Referer", "https://giotmauvang.org.vn/");
            headers.set("Origin", "https://giotmauvang.org.vn");
            headers.set("User-Agent", "Mozilla/5.0");

            // Tính toán thời gian fromDate và toDate (hiện tại -> +30 ngày)
            long fromDate = Instant.now().toEpochMilli();
            long toDate = fromDate + Duration.ofDays(30).toMillis();

            // Tạo JSON request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("what", "donateCalendar");
            requestBody.put("from", 0);
            requestBody.put("limit", 0);
            requestBody.put("by", "asc");
            requestBody.put("order", "donateDate");
            requestBody.put("fromDate", fromDate);
            requestBody.put("toDate", toDate);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gửi request POST
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            // Kiểm tra response
            if (response.getBody() == null || response.getBody().isEmpty()) {
                apiResponse.setCode(400);
                apiResponse.setMessage("Empty response body");
                return apiResponse;
            }

            // Chuyển response JSON thành Java object
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode eventsJson = root.path("data").path("list");

            // Kiểm tra JSON hợp lệ
            if (eventsJson.isMissingNode() || !eventsJson.isArray()) {
                apiResponse.setCode(400);
                apiResponse.setMessage("Invalid JSON format: missing 'data.list' array");
                return apiResponse;
            }

            // Lưu dữ liệu vào database
            List<Event> eventsToSave = new ArrayList<>();
            for (JsonNode eventJson : eventsJson) {
                Event event = mapJsonToEvent(eventJson);

                List<Event> existingEvents = eventRepo.findEventByEventId(event.getEventId());
                if (existingEvents.size() > 1) {
                    eventRepo.deleteAll(existingEvents.subList(1, existingEvents.size())); // Xóa bản ghi dư thừa
                }
                // Lấy danh sách time slots
                JsonNode timeSlots = eventJson.path("donateAcceptTimes");

                if (timeSlots.isArray() && timeSlots.size() > 0) {
                    // Giả sử lấy giá trị `maxLimitDonate` từ slot đầu tiên
                    JsonNode firstTimeSlot = timeSlots.get(0);
                    int maxLimitDonate = firstTimeSlot.path("maxLimitDonate").asInt(0);

                    event.setMaxRegistrations(maxLimitDonate);
                } else {
                    event.setMaxRegistrations(0); // Giá trị mặc định nếu không có slot nào
                }

                eventsToSave.add(event);
            }

            eventRepo.saveAll(eventsToSave);

            apiResponse.setCode(200);
            apiResponse.setMessage("Successfully fetched and saved " + eventsToSave.size() + " events.");

        } catch (Exception e) {
            apiResponse.setCode(400);
            apiResponse.setMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return apiResponse;
    }

    private Event mapJsonToEvent(JsonNode jsonNode) {
        DonationUnit unit = donationUnitRepo.findByUnit(jsonNode.get("unit").asText()).orElseThrow();

        Event event = new Event();

        event.setEventId(jsonNode.get("eventId").asText());
        event.setDonatePlace(jsonNode.get("donatePlace").asText());
        event.setDonateAddress(jsonNode.get("donateAddress").asText());
        event.setTitle(jsonNode.get("title").asText());
//        event.setMaxRegistrations(jsonNode.get("maxBloodBag").asInt());

        event.setDescription(jsonNode.get("description").asText());
        event.setCurrentRegistrations(jsonNode.get("currentReg").asLong());
        event.setIsUrgent(jsonNode.get("isUrgent").asInt());
        event.setEventType(jsonNode.get("eventType").asInt());
        event.setDonationUnit(unit);
        // 🕒 Chuyển đổi timestamp sang LocalDateTime
        long donateDateMillis = jsonNode.get("donateDate").asLong();
        event.setDonateDate(Instant.ofEpochMilli(jsonNode.get("donateDate").asLong())
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDate());
        event.setDonateStartTime(LocalTime.parse(jsonNode.get("donateTimeStart").asText()));
        event.setDonateEndTime(LocalTime.parse(jsonNode.get("donateTimeEnd").asText()));
        BloodQuota bloodQuota = new BloodQuota();
        bloodQuota.setMinBloodBag(jsonNode.get("minBloodBag").asInt());
        bloodQuota.setMaxBloodBag(jsonNode.get("maxBloodBag").asInt());
        bloodQuota.setGoalBloodBag(jsonNode.get("goalBloodBag").asInt());
        bloodQuota.setAdditionalBloodBag(jsonNode.get("additionalBloodBag").asInt());
        event.setBloodQuota(bloodQuota);
        bloodQuota.setEvent(event);


        List<DonateTimeSlot> timeSlots = new ArrayList<>();
        if (jsonNode.has("donateAcceptTimes")) {
            for (JsonNode slotNode : jsonNode.get("donateAcceptTimes")) {
                DonateTimeSlot slot = new DonateTimeSlot();
                slot.setDonateAcceptTime(slotNode.get("donateAcceptTime").asInt());
                slot.setDonateStopTime(slotNode.get("donateStopTime").asInt());
                slot.setMaxLimitDonate(slotNode.get("maxLimitDonate").asInt());
                slot.setCurrentReg(slotNode.get("currentReg").asInt());
                slot.setEvent(event);
                timeSlots.add(slot);
            }
        }
        event.setDonationTimeSlots(timeSlots);
        return event;
    }

    @Override
    public ApiResponse getEventsByDate(LocalDate eventDate) {
        ApiResponse response = new ApiResponse();
        try {
            List<Event> events = eventRepo.findByDonateStartTimeBetween(
                    eventDate, eventDate);

            if (events.isEmpty()) {
                response.setCode(404);
                response.setMessage("No events found for this date.");
                return response;
            }
            response.setCode(200);
            response.setMessage("Successful");
            response.setEventDTOList(Utils.mapEventListEntityToDTO(events));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public ApiResponse getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        ApiResponse response = new ApiResponse();
        try {
            List<Event> events = eventRepo.findEventBetweenStartDateAndEndDate(
                    startDate,endDate);

            if (events.isEmpty()) {
                response.setCode(404);
                response.setMessage("No events found in this date range.");
                return response;
            }
            response.setCode(200);
            response.setMessage("Successful");
            response.setEventDTOList(Utils.mapEventListEntityToDTO(events));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public ApiResponse getEventById(String id) {
        ApiResponse apiResponse = new ApiResponse();
        Event event = eventRepo.findById(id).orElseThrow();
        try{
            EventDTO eventDTO  = Utils.mapEventEntityToEventDTO(event);
            apiResponse.setCode(200);
            apiResponse.setMessage("Successful");
            apiResponse.setEventDTO(eventDTO);
            return apiResponse;
        }catch (Exception e){
            apiResponse.setCode(500);
            apiResponse.setMessage(e.getMessage());
        }
        return apiResponse;
    }


    /**
     *  🏥 Lấy danh sách sự kiện theo đơn vị hiến máu
     */
    @Override
    public ApiResponse getEventByUnit(Long unitId) {
        ApiResponse response = new ApiResponse();
        try {
            DonationUnit unit = donationUnitRepo.findById(unitId)
                    .orElseThrow(() -> new OurException("No unit found"));
            List<Event> events = unit.getEvents();
            if (events.isEmpty()) {
                response.setCode(404);
                response.setMessage("No events found for this unit.");
                return response;
            }
            response.setCode(200);
            response.setMessage("Successful");
            response.setEventDTOList(Utils.mapEventListEntityToDTO(events));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    public ApiResponse updateEvent(String id,EventDTO eventDTO) {
        ApiResponse response = new ApiResponse();
        try {
            Event event = eventRepo.findById(id).orElseThrow();

            event.setTitle(eventDTO.getTitle());
            event.setDonateDate(eventDTO.getDonateDate());
            event.setDonateStartTime(eventDTO.getEventStartTime());
            event.setDonateEndTime(eventDTO.getEventEndTime());
            event.setMaxRegistrations(eventDTO.getMaxRegistrations());
            event.setEventStatus(EventStatus.valueOf(eventDTO.getStatus()));

            eventRepo.save(event);

            response.setCode(200);
            response.setMessage("Successful");
            response.setEventDTO(Utils.mapEventEntityToEventDTO(event));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage("Error: " + e.getMessage());
        }
        return response;
    }

    @Override
    public ApiResponse addEvent(EventDTO eventDTO) {
            ApiResponse apiResponse = new ApiResponse();
        try{
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString().replace("-", "");
            Optional<DonationUnit> unit = donationUnitRepo.findById(Long.valueOf(eventDTO.getUnit()));
            if (unit.isPresent()) {
                DonationUnit donationUnit = unit.get();
                Event event = new Event();
                event.getBloodRegistration().setEvent(event);
                event.setEventId(id);
                event.setTitle(eventDTO.getTitle());
                event.setDonateDate(eventDTO.getDonateDate());

                if (eventDTO.getEventEndTime().isBefore(eventDTO.getEventStartTime())) {
                    throw new Exception();
                }

                event.setDonateStartTime(eventDTO.getEventStartTime());
                event.setDonateEndTime(eventDTO.getEventEndTime());
                event.setMaxRegistrations(eventDTO.getMaxRegistrations());
                event.setDonateAddress(eventDTO.getDonateAddress());
                event.setDonationUnit(donationUnit);
                event.setCurrentRegistrations(0L);
                event.setEventStatus(EventStatus.ACTIVE);
                eventRepo.save(event);

                BloodQuota bloodQuota = new BloodQuota();
                bloodQuota.setGoalBloodBag(eventDTO.getBloodQuotaDTO().getGoalIBloodBag());
                bloodQuota.setMinBloodBag(eventDTO.getBloodQuotaDTO().getMinIBloodBag());
                bloodQuota.setMaxBloodBag(eventDTO.getBloodQuotaDTO().getMaxIBloodBag());
                bloodQuota.setMaxBloodBag(eventDTO.getBloodQuotaDTO().getAdditionalIBloodBag());
                bloodQuota.setEvent(event);
                event.setBloodQuota(bloodQuota);
                bloodQuotaRepo.save(bloodQuota);

                apiResponse.setCode(200);
                apiResponse.setMessage("Successful");
                apiResponse.setEventDTO(Utils.mapEventEntityToEventDTO(event));
            }
        } catch (Exception e) {
            apiResponse.setCode(500);
            apiResponse.setMessage(e.getMessage());
        }
        return apiResponse;
    }


    public ApiResponse removeEvent(String eventId) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            eventRepo.deleteById(eventId);
            apiResponse.setCode(200);
            apiResponse.setMessage("Successfully");
        } catch (Exception ex ) {
            apiResponse.setCode(500);
            apiResponse.setMessage("Not Success");
        }
        return apiResponse;
    }
}
