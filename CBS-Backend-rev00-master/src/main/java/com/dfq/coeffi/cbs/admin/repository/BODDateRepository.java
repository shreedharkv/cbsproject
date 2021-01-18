package com.dfq.coeffi.cbs.admin.repository;

import com.dfq.coeffi.cbs.admin.entity.BODDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
public interface BODDateRepository extends JpaRepository<BODDate,Long> {

    @Query("SELECT bod FROM BODDate bod WHERE bod.bodDate = date(now()) AND bod.bodStatus = true")
    BODDate getBODDateByStatus();

    List<BODDate> findByBodDate(Date bod);
}