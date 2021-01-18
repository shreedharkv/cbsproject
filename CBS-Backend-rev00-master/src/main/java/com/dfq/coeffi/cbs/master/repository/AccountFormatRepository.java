package com.dfq.coeffi.cbs.master.repository;

import com.dfq.coeffi.cbs.deposit.entity.ChildrensDeposit;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountFormatRepository extends JpaRepository<AccountFormat, Long> {

    List<AccountFormat> findByActive(boolean active);

//    @Query("SELECT d FROM ChildrensDeposit d WHERE (d.accountNumber=:accountNumber AND d.depositsApproval.isApproved=true AND d.isWithDrawn=false )")
    AccountFormat findByAccountFormatTypeAndSubType(AccountFormatType accountFormatType, String subType);
}