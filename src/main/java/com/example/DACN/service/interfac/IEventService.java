package com.example.DACN.service.interfac;

import com.example.DACN.dto.ApiResponse;
import com.example.DACN.dto.EventDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IEventService {

    //Lấy dữ liệu lịch hiến máu từ API Giọt Máu Vàng và lưu vào database.
//   ApiResponse fetchAndSaveEventsFromApi(long fromDate, long toDate);

    /*
     * @param eventDate Ngày cần tìm kiếm
     * @return ApiResponse chứa danh sách sự kiện
     */

    ApiResponse fetchAndSaveDonationEvents();
    ApiResponse getEventsByDate(LocalDate eventDate);

    ApiResponse getEventsByDateRange(LocalDate startDate, LocalDate endDate);

    ApiResponse getEventById(String id);
    ApiResponse getEventByUnit(Long unitId);
    ApiResponse updateEvent(String id,EventDTO eventDTO) ;
    ApiResponse addEvent(EventDTO eventDTO);
}
