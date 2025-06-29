package com.example.DACN.repository;

import com.example.DACN.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepo extends JpaRepository<Event, String> {
    //Lấy danh sách sự kiện theo ngày
    List<Event> findByDonateStartTimeBetween(LocalDate start, LocalDate end);


    //Lấy danh sách sự kiện theo khoảng ngày và đơn vị hiến máu
    List<Event> findByDonateStartTimeBetweenAndDonationUnit_Id(
            LocalDateTime startDate, LocalDateTime endDate, Long donationUnitId);
    @Query("SELECT e FROM Event e WHERE e.donateDate >= :startDate AND e.donateDate <= :endDate")
    List<Event> findEventBetweenStartDateAndEndDate(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    //Lấy danh sách sự kiện theo đơn vị hiến máu
    List<Event> findByDonationUnit_Id(Long donationUnitId);
    List<Event> findEventByEventId(String id);
    //Xóa tất cả các sự kiện liên quan đến một đơn vị hiến máu
    void deleteByDonationUnit_Id(Long donationUnitId);

    //Tìm các sự kiện trùng lặp về thời gian tại cùng một địa điểm
    @Query("SELECT e FROM Event e WHERE e.donateAddress = :location " +
            "AND e.donateStartTime < :endTime " +
            "AND e.donateEndTime > :startTime")
    List<Event> findConflictingEvents(
            @Param("location") String location,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);


}
