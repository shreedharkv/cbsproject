package com.dfq.coeffi.cbs.report.memberReports;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.member.entity.RefundShare;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Transactional
@EnableJpaRepositories
public interface MemberReportRepository extends JpaRepository<Member,Long> {

    @Query("SELECT m FROM Member m WHERE date(m.applicationDate) BETWEEN '1947-01-01' AND :inputDate ")
    List<Member> getMembersReportByDate(@Param("inputDate") Date inputDate);

    @Query("SELECT m FROM Member m WHERE date(m.applicationDate) BETWEEN :dateFrom AND :dateTo")
    List<Member> getMembersReportByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

    @Query("SELECT m FROM Member m " +
            "WHERE ((date(m.applicationDate) BETWEEN :dateFrom AND :dateTo ) OR " +
            "(m.memberNumber BETWEEN :numFrom AND :numTo ) OR " +
            "(m.applicationNumber BETWEEN :applicationFrom AND :applicationTo) OR " +
            "(date(m.applicationDate) BETWEEN '1947-01-01' AND :inputDate))")
    List<Member> getMemberApplicationDetailsByDate(@Param("dateFrom") Date dateFrom,@Param("dateTo") Date dateTo,
                                                   @Param("numFrom") String numFrom,@Param("numTo") String numTo,
                                                   @Param("applicationFrom")String applicationFrom,@Param("applicationTo") String applicationTo,
                                                   @Param("inputDate")Date inputDate);

    @Query("SELECT m.memberPersonalDetail.subCaste,count(*) FROM Member m " +
            "WHERE date(m.applicationDate) BETWEEN '1947-01-01' AND :inputDate group by m.memberPersonalDetail.subCaste")
    List<Member> getMemberCasteDetailsByDate(@Param("inputDate") Date inputDate);

    @Query("SELECT m FROM Member m " +
            "WHERE (m.memberNumber BETWEEN :nominalMemberNumberFrom AND :nominalMemberNumberTo) AND (m.memberType='NOMINAL')")
    List<Member> getNominalMemberList(@Param("nominalMemberNumberFrom") String nominalMemberNumberFrom,
                                      @Param("nominalMemberNumberTo") String nominalMemberNumberTo);

    @Query("SELECT c FROM Customer c " +
            "WHERE (date(c.applicationDate) BETWEEN :dateFrom AND :dateTo ) OR " +
            "(c.id BETWEEN :customerIdFrom AND :customerIdTo )")
    List<Customer> getCustomerDetailsListByDate(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                                @Param("customerIdFrom") long customerIdFrom, @Param("customerIdTo") long customerIdTo);

    @Query("SELECT c FROM RefundShare c WHERE (c.memberNumber BETWEEN :memberNumberFrom AND :memberNumberTo)")
    List<RefundShare> getMemberShareRefundDetails(@Param("memberNumberFrom") String memberNumberFrom, @Param("memberNumberTo") String memberNumberTo);

    @Query("SELECT d FROM DividendIssue d WHERE (:year is null OR d.dividendYear =:year)")
    List<Member> getDividendRegisterDetailsByYear(@Param("year") String year);

    @Query("SELECT m FROM Member m WHERE ((m.memberNumber BETWEEN :memberNumberFrom AND :memberNumberTo)) AND m.approvedStatus=true AND m.memberType=:memberType")
    List<Member> getMemberDetailsByMemberNumber(@Param("memberNumberFrom") String memberNumberFrom, @Param("memberNumberTo") String memberNumberTo, @Param("memberType") MemberType memberType);


    @Query("SELECT m FROM Member m WHERE ((m.id BETWEEN :fromId AND :toId)) AND m.approvedStatus=true AND m.memberType=:memberType")
    List<Member> getMemberBetweenIds(@Param("fromId") long fromId, @Param("toId") long toId, @Param("memberType") MemberType memberType);

    @Query("SELECT m FROM Member m " +
            "WHERE (m.occupationCode =:occupationCode AND (m.memberNumber BETWEEN :memberNumberFrom AND :memberNumberTo))")
    List<Member> getMemberByOccupation(@Param("occupationCode") String occupationCode, @Param("memberNumberFrom") String memberNumberFrom, @Param("memberNumberTo") String memberNumberTo);

    @Query("SELECT m FROM Member m WHERE (m.applicationNumber BETWEEN :applicationFrom AND :applicationTo)")
    List<Member> getMemberApplicationDetailsByApplicationNumber(@Param("applicationFrom") String applicationFrom,@Param("applicationTo")String applicationTo);
}