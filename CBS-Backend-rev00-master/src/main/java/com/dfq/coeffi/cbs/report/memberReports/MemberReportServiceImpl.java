package com.dfq.coeffi.cbs.report.memberReports;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.master.entity.occupation.Occupation;
import com.dfq.coeffi.cbs.master.service.OrganisationService;
import com.dfq.coeffi.cbs.member.entity.DividendIssue;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberType;
import com.dfq.coeffi.cbs.member.entity.RefundShare;
import com.dfq.coeffi.cbs.member.repository.DividendIssueRepository;
import com.dfq.coeffi.cbs.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.dfq.coeffi.cbs.utils.DateUtil.convertToAscii;

@Service
@Transactional
public class MemberReportServiceImpl implements MemberReportService {

    private final MemberReportRepository memberReportRepository;
    private final OrganisationService organisationService;
    private final DividendIssueRepository dividendIssueRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberReportServiceImpl(MemberReportRepository memberReportRepository, OrganisationService organisationService,
                                   DividendIssueRepository dividendIssueRepository, MemberRepository memberRepository) {
        this.memberReportRepository = memberReportRepository;
        this.organisationService = organisationService;
        this.dividendIssueRepository = dividendIssueRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<Member> getMembersReportByDate(Date inputDate) {
        return memberReportRepository.getMembersReportByDate(inputDate);
    }

    @Override
    public List<Member> getMembersReportByDate(Date dateFrom,Date dateTo) {
        return memberReportRepository.getMembersReportByDate(dateFrom,dateTo);
    }

    @Override
    public List<Member> getMemberApplicationDetailsByDate(Date dateFrom,Date dateTo,String numFrom,String numTo,String applicationFrom,String applicationTo,Date inputDate) {
        return memberReportRepository.getMemberApplicationDetailsByDate(dateFrom,dateTo,numFrom,numTo,applicationFrom,applicationTo,inputDate);
    }

    @Override
    public List<Member> getMemberApplicationDetailsByDate(String applicationFrom, String applicationTo) {
        List<Member> members = memberRepository.findAll();

        BigInteger mIntAccountNumberFrom = convertToAscii(applicationFrom.toUpperCase());
        BigInteger mIntAccountNumberTo = convertToAscii(applicationTo.toUpperCase());

        ArrayList<Member> returnDeposits = new ArrayList<>();
        for (Member member:members) {
            BigInteger mIntAccountNumber = convertToAscii(member.getApplicationNumber().toUpperCase());
            if(mIntAccountNumber.doubleValue() >= mIntAccountNumberFrom.doubleValue() && mIntAccountNumber.doubleValue() <= mIntAccountNumberTo.doubleValue()  ){
                returnDeposits.add(member);
            }
        }
        return returnDeposits;
    }

    @Override
    public List<Member> getMemberCasteDetailsByDate(Date inputDate) {
        return memberReportRepository.getMemberCasteDetailsByDate(inputDate);
    }

    @Override
    public List<Member> getNominalMemberList(String nominalMemberNumberFrom,String nominalMemberNumberTo) {
        return memberReportRepository.getNominalMemberList(nominalMemberNumberFrom,nominalMemberNumberTo);
    }

    @Override
    public List<RefundShare> getMemberShareRefundDetails(String memberNumberFrom, String memberNumberTo) {
        return memberReportRepository.getMemberShareRefundDetails(memberNumberFrom,memberNumberTo);
    }

    @Override
    public List<Member> getDividendRegisterDetailsByYear(String year) {
        return memberReportRepository.getDividendRegisterDetailsByYear(year);
    }

    @Override
    public List<Member> getMemberDetailsByMemberNumber(String memberNumberFrom, String memberNumberTo, MemberType memberType) {
        List<Member> members = memberRepository.findByApprovedStatusAndStatusAndMemberType(true,true,memberType);
        BigInteger mIntAccountNumberFrom = convertToAscii(memberNumberFrom.toUpperCase());
        BigInteger mIntAccountNumberTo = convertToAscii(memberNumberTo.toUpperCase());
        ArrayList<Member> returnDeposits = new ArrayList<>();
        for (Member member:members) {
            BigInteger mIntAccountNumber = convertToAscii(member.getMemberNumber().toUpperCase());
            if(mIntAccountNumber.doubleValue() >= mIntAccountNumberFrom.doubleValue() && mIntAccountNumber.doubleValue() <= mIntAccountNumberTo.doubleValue()  ){
                returnDeposits.add(member);
            }
        }
        return returnDeposits;
    }

    @Override
    public List<Member> getMemberByOccupation(String occupationCode,String memberNumberFrom, String memberNumberTo) {
        List<Occupation> occupations = organisationService.getOccupations();
        List<Member> membersOccupation = null;
        ArrayList al = new ArrayList();
        for (int i = 0; i <occupations.size() ; i++) {
            membersOccupation =memberReportRepository.getMemberByOccupation(occupations.get(i).getCode(),memberNumberFrom,memberNumberTo);
            if(membersOccupation != null && membersOccupation.size() > 0 ){
                MemberOccupationDto memberOccupationDto = new MemberOccupationDto();
                memberOccupationDto.setOccupationCode(occupations.get(i).getCode());
                memberOccupationDto.setCount(membersOccupation.size());
                al.add(memberOccupationDto);
            }
        }
        return al;
    }



    /* @Override
      public List<Member> getLoaneeMember() {
          return memberReportRepository.getLoaneeMember();
      }
  */
    @Override
    public List<Customer> getCustomerDetailsListByDate(Date dateFrom,Date dateTo,long customerIdFrom,long customerIdTo) {
        return memberReportRepository.getCustomerDetailsListByDate(dateFrom,dateTo,customerIdFrom,customerIdTo);
    }

    @Override
    public List<DividendIssue> getDividendIssueByDividendYear(String dividendYear) {
        return dividendIssueRepository.findByDividendYear(dividendYear);
    }
}
