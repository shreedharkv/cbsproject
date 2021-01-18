package com.dfq.coeffi.cbs.deposit.repository;

import com.dfq.coeffi.cbs.deposit.entity.DoubleScheme;
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
public interface DoubleSchemeRepository extends JpaRepository<DoubleScheme,Long> {

    @Query("SELECT ds FROM DoubleScheme ds WHERE " +
            "(date(ds.createdOn) BETWEEN :dateFrom AND :dateTo and ds.status=false)")
    List<DoubleScheme> getAllDoubleSchemeDeposits(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT ds FROM DoubleScheme ds WHERE (ds.accountNumber=:accountNumber AND ds.depositsApproval.isApproved=true AND ds.isWithDrawn=false )")
    Optional<DoubleScheme> getDoubleSchemeByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT ds FROM DoubleScheme ds WHERE ds.member.memberNumber=:memberNumber AND ds.depositsApproval.isApproved=true AND ds.isWithDrawn=false")
    List<DoubleScheme> getDoubleSchemeByMemberNumber(@Param("memberNumber") String memberNumber);
}
