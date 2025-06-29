package com.example.DACN.repository;


import com.example.DACN.model.BloodRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloodRegistrationRepository extends JpaRepository<BloodRegistration, Long> {
//    public BloodRegistrationRepository() {}

    @Query("SELECT b FROM BloodRegistration b WHERE b.event.id = :eventId")
    Optional<BloodRegistration> findByEventId(String eventId);

}
