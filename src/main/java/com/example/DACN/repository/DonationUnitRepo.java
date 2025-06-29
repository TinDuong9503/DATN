package com.example.DACN.repository;

import com.example.DACN.model.DonationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonationUnitRepo  extends JpaRepository<DonationUnit, Long> {
        Optional<DonationUnit> findByUnit (String unit);
}
