package com.dfq.coeffi.cbs.report.monthlySchedule;


import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface MonthlyScheduleRepository extends JpaRepository<FixedDeposit,Long> {
}
