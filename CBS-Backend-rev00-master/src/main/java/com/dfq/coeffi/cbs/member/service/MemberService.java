package com.dfq.coeffi.cbs.member.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.member.entity.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberService {

    List<Member> members();
    List<Member> getApprovedMembers();
    Optional<Member> getMember(long id);
    Member getUnApprovedMember(long id);

    Member saveMember(Member member);
    void deleteMember(long id);
    List<Member> getMemberForApprove();
    Optional<Member> getMemberByApprovedStatus(long id);

    Optional<Member>  findMemberByMemberNumber(String memberNumber);

    Member getMemberByCustomer(Customer customer);

    List<MemberFee> memberFees();

    List<AdditionalShare> additionalShares();

    List<RefundShare> refundShares();

    List<DividendIssue> getDividendIssues();
    DividendIssue getDividendIssue(long id);
    DividendIssue saveDividendIssue(DividendIssue dividendIssue);
    List<DividendIssue> getCurrentYearDividendIssues(FinancialYear financialYear);


    List<DividendPayment> getDividendPayments();
    DividendPayment getDividendPayment(long id);
    DividendPayment saveDividendPayment(DividendPayment dividendPayment);

    List<ShareMaster> getShareMasters();
    ShareMaster saveShareMaster(ShareMaster shareMaster);
    ShareMaster getShareMaster(long id);

    Member getMemberForApproval(long memberId);

    List<Member> getApprovedMembers(MemberType memberType);

    List<Member> getUnApprovedMembers(MemberType memberType);

    NumberFormat getNumberFormatByType(String type);
    NumberFormat updateNumberFormat(NumberFormat numberFormat);

    List<AdditionalShare> getAdditionalSharesByDate(Date dateFrom, Date dateTo);

}