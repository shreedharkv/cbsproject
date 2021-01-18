package com.dfq.coeffi.cbs.transaction.repository;


import com.dfq.coeffi.cbs.transaction.entity.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@EnableJpaRepositories
public interface FundTransferRepository extends JpaRepository<FundTransfer,Long> {

    @Query("SELECT ft from FundTransfer ft WHERE date(ft.transactionDate) =:inputDate")
    List<FundTransfer> getFundTransferByTransactionDate(@Param("inputDate") Date inputDate);
}
