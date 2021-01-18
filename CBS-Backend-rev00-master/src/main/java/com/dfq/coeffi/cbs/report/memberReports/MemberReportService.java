package com.dfq.coeffi.cbs.report.memberReports;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.member.entity.DividendIssue;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.member.entity.RefundShare;

import java.util.Date;
import java.util.List;

public interface MemberReportService {

    List<Member> getMembersReportByDate(Date inputDate);

    List<Member> getMembersReportByDate(Date dateFrom, Date dateTo);

    List<Member> getMemberApplicationDetailsByDate(Date dateFrom,Date dateTo,String numFrom,String numTo,String applicationFrom,String applicationTo,Date inputDate);

    List<Member> getMemberApplicationDetailsByDate(String applicationFrom,String applicationTo);

    List<Member> getMemberCasteDetailsByDate(Date inputDate);

    List<Member> getNominalMemberList(String nominalMemberNumberFrom,String nominalMemberNumberTo);

    List<RefundShare> getMemberShareRefundDetails(String memberNumberFrom, String memberNumberTo);

    List<Member> getDividendRegisterDetailsByYear(String year);
    // List<Member> getLoaneeMember();

    List<Member> getMemberDetailsByMemberNumber(String memberNumberFrom, String memberNumberTo, MemberType memberType);

    List<Member> getMemberByOccupation(String occupationCode, String memberNumberFrom, String memberNumberTo);


    /**
     * Customer Details
     */
    List<Customer> getCustomerDetailsListByDate(Date dateFrom, Date dateTo, long customerIdFrom, long customerIdTo);

    List<DividendIssue> getDividendIssueByDividendYear(String dividendYear);

}
