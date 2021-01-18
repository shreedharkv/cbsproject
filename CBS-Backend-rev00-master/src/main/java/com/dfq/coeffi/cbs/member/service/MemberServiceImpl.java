package com.dfq.coeffi.cbs.member.service;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.member.entity.*;
import com.dfq.coeffi.cbs.member.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@Service
public class MemberServiceImpl implements MemberService{

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberFeeRepository memberFeeRepository;

    @Autowired
    private AdditionalShareRepository additionalShareRepository;

    @Autowired
    private RefundShareRepository refundShareRepository;

    @Autowired
    private DividendIssueRepository dividendIssueRepository;

    @Autowired
    private DividendPaymentRepository dividendPaymentRepository;

    @Autowired
    private ShareMasterRepository shareMasterRepository;

    @Autowired
    private NumberFormatRepository numberFormatRepository;

    @Override
    public List<Member> members() {
        return memberRepository.findByStatus(true);
    }

    @Override
    public List<Member> getApprovedMembers() {
        return memberRepository.findByApprovedStatusAndStatus(true, true);
    }

    @Override
    public List<Member> getApprovedMembers(MemberType memberType) {
        return memberRepository.findByApprovedStatusAndStatusAndMemberType(true, true, memberType);
    }

    @Override
    public List<Member> getUnApprovedMembers(MemberType memberType) {
        return memberRepository.findByApprovedStatusAndStatusAndMemberType(false, true, memberType);
    }

    @Override
    public Optional<Member> getMember(long id) {
        return ofNullable(memberRepository.findByIdAndApprovedStatusAndStatus(id, true, true));
    }

    @Override
    public Member getUnApprovedMember(long id) {
        return memberRepository.findOne(id);
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(long id) {
        memberRepository.delete(id);
    }

    @Override
    public List<Member> getMemberForApprove() {
        return memberRepository.findByApprovedStatusAndStatus(false, true);
    }

    @Override
    public Optional<Member> getMemberByApprovedStatus(long id) {
        return ofNullable(memberRepository.findByApprovedStatusAndId(true,id));
    }

    @Override
    public Optional<Member>  findMemberByMemberNumber(String memberNumber) {
        return ofNullable(memberRepository.findByApprovedStatusAndStatusAndMemberNumber(true, true, memberNumber));
    }

    @Override
    public Member getMemberByCustomer(Customer customer) {
        return memberRepository.findByCustomer(customer);
    }

    @Override
    public List<MemberFee> memberFees() {
        return memberFeeRepository.findAll();
    }

    @Override
    public List<AdditionalShare> additionalShares() {
        return additionalShareRepository.findAll();
    }

    @Override
    public List<RefundShare> refundShares() {
        return refundShareRepository.findAll();
    }

    @Override
    public List<DividendIssue> getDividendIssues() {
        return dividendIssueRepository.findAll();
    }

    @Override
    public DividendIssue getDividendIssue(long id) {
        return dividendIssueRepository.findOne(id);
    }

    @Override
    public DividendIssue saveDividendIssue(DividendIssue dividendIssue) {
        return dividendIssueRepository.save(dividendIssue);
    }

    @Override
    public List<DividendIssue> getCurrentYearDividendIssues(FinancialYear financialYear) {
        return dividendIssueRepository.getCurrentYearDividendIssues(financialYear);
    }

    @Override
    public List<DividendPayment> getDividendPayments() {
        return dividendPaymentRepository.findAll();
    }

    @Override
    public DividendPayment getDividendPayment(long id) {
        return dividendPaymentRepository.findOne(id);
    }

    @Override
    public DividendPayment saveDividendPayment(DividendPayment dividendPayment) {
        return dividendPaymentRepository.save(dividendPayment);
    }

    @Override
    public List<ShareMaster> getShareMasters() {
        return shareMasterRepository.findAll();
    }

    @Override
    public ShareMaster saveShareMaster(ShareMaster shareMaster) {
        return shareMasterRepository.save(shareMaster);
    }

    @Override
    public ShareMaster getShareMaster(long id) {
        return shareMasterRepository.findOne(id);
    }

    @Override
    public Member getMemberForApproval(long memberId) {
        return memberRepository.findByIdAndApprovedStatusAndStatus(memberId, false, true);
    }

    @Override
    public NumberFormat getNumberFormatByType(String type) {
        return numberFormatRepository.findByType(type);
    }

    @Override
    public NumberFormat updateNumberFormat(NumberFormat numberFormat) {
        return numberFormatRepository.save(numberFormat);
    }

    @Override
    public List<AdditionalShare> getAdditionalSharesByDate(Date dateFrom, Date dateTo) {
        return additionalShareRepository.getAdditionalShareDetailsByDate(dateFrom, dateTo);
    }
}