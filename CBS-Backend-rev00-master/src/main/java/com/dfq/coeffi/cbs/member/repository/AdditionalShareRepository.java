package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.member.entity.AdditionalShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface AdditionalShareRepository extends JpaRepository<AdditionalShare, Long> {

    @Query("SELECT additionalShare FROM AdditionalShare additionalShare WHERE date(additionalShare.appliedDate) BETWEEN :dateFrom AND :dateTo")
    List<AdditionalShare> getAdditionalShareDetailsByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);
}