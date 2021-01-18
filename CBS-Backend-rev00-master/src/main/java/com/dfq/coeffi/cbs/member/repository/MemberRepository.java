package com.dfq.coeffi.cbs.member.repository;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByStatus(Boolean status);

    List<Member> findByApprovedStatusAndStatus(Boolean approval, Boolean status);

    List<Member> findByApprovedStatusAndStatusAndMemberType(Boolean approval, Boolean status, MemberType memberType);

    Member findByIdAndApprovedStatusAndStatus(long id, Boolean approval, Boolean status);

    Member findByApprovedStatusAndId(Boolean approvedStatus, Long id);

    Member findByApprovedStatusAndStatusAndMemberNumber(boolean approvedStatus, boolean status, String memberNumber);

    @Query("update Member m set m.status=false where m.id=:id")
    @Modifying
    void deleteMember(@Param("id") Long id);

    Member findByCustomer(Customer customer);

    Member findByMemberNumber(String memberNumber);

}