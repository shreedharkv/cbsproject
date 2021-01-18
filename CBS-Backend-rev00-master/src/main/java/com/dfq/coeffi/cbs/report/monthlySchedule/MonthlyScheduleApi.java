package com.dfq.coeffi.cbs.report.monthlySchedule;

import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportDto;
import com.dfq.coeffi.cbs.report.depositReports.DepositReportService;
import com.dfq.coeffi.cbs.report.memberReports.MemberReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static com.dfq.coeffi.cbs.utils.DateUtil.getMonthName;

@RestController
public class MonthlyScheduleApi extends BaseController {

    private final MonthlyScheduleService monthlyScheduleService;
    private final DepositReportService depositReportService;
    private final MemberReportService memberReportService;

    @Autowired
    public MonthlyScheduleApi(final MonthlyScheduleService monthlyScheduleService,final DepositReportService depositReportService,
                              final MemberReportService memberReportService){
        this.monthlyScheduleService = monthlyScheduleService;
        this.depositReportService = depositReportService;
        this.memberReportService = memberReportService;
    }

    @PostMapping("report/monthly-schedule/deposit")
    public ResponseEntity<InputStreamResource> getDepositReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (depositReportDto.depositType.toString().equalsIgnoreCase("CHILDRENS_DEPOSIT")) {
            List<ChildrensDeposit> childrensDepositList = depositReportService.getChildrensDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<childrensDepositList.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setCustomerId(childrensDepositList.get(i).getMember().getCustomer().getId());
                monthlyScheduleDto.setName(childrensDepositList.get(i).getMember().getName());
                monthlyScheduleDto.setAccountNumber(childrensDepositList.get(i).getAccountNumber());
                monthlyScheduleDto.setDepositAmount(childrensDepositList.get(i).getDepositAmount());
                monthlyScheduleDto.setMemberNumber(childrensDepositList.get(i).getMember().getMemberNumber());
                String mName=getMonthName(childrensDepositList.get(i).getCreatedOn());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("TERM_DEPOSIT")) {
            List<TermDeposit> termDeposits = depositReportService.getTermDepositReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<termDeposits.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setCustomerId(termDeposits.get(i).getMember().getCustomer().getId());
                monthlyScheduleDto.setName(termDeposits.get(i).getMember().getName());
                monthlyScheduleDto.setAccountNumber(termDeposits.get(i).getAccountNumber());
                monthlyScheduleDto.setDepositAmount(termDeposits.get(i).getDepositAmount());
                monthlyScheduleDto.setMemberNumber(termDeposits.get(i).getMember().getMemberNumber());

                String mName=getMonthName(termDeposits.get(i).getCreatedOn());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("DOUBLE_SCHEME")) {
            List<DoubleScheme> doubleSchemes= depositReportService.getDoubleSchemeReportByDate(depositReportDto.inputDate, depositReportDto.depositType, depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<doubleSchemes.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setCustomerId(doubleSchemes.get(i).getMember().getCustomer().getId());
                monthlyScheduleDto.setName(doubleSchemes.get(i).getMember().getName());
                monthlyScheduleDto.setAccountNumber(doubleSchemes.get(i).getAccountNumber());
                monthlyScheduleDto.setDepositAmount(doubleSchemes.get(i).getDepositAmount());
                monthlyScheduleDto.setMemberNumber(doubleSchemes.get(i).getMember().getMemberNumber());

                String mName=getMonthName(doubleSchemes.get(i).getCreatedOn());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("FIXED_DEPOSIT")) {
            List<FixedDeposit> fixedDeposits = depositReportService.getFixedDepositReportByDate(depositReportDto.inputDate,depositReportDto.depositType,depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.accountNumber,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<fixedDeposits.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setCustomerId(fixedDeposits.get(i).getMember().getCustomer().getId());
                monthlyScheduleDto.setName(fixedDeposits.get(i).getMember().getName());
                monthlyScheduleDto.setAccountNumber(fixedDeposits.get(i).getAccountNumber());
                monthlyScheduleDto.setDepositAmount(fixedDeposits.get(i).getDepositAmount());
                monthlyScheduleDto.setMemberNumber(fixedDeposits.get(i).getMember().getMemberNumber());

                String mName=getMonthName(fixedDeposits.get(i).getCreatedOn());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }
        }else if (depositReportDto.depositType.toString().equalsIgnoreCase("RECURRING_DEPOSIT")) {
            List<RecurringDeposit> recurringDeposits= depositReportService.getRecurringDepositReportByDate(depositReportDto.inputDate,depositReportDto.depositType,depositReportDto.accountNumberFrom, depositReportDto.accountNumberTo, depositReportDto.dateFrom, depositReportDto.dateTo,depositReportDto.maturityDateFrom,depositReportDto.maturityDateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<recurringDeposits.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setCustomerId(recurringDeposits.get(i).getMember().getCustomer().getId());
                monthlyScheduleDto.setName(recurringDeposits.get(i).getMember().getName());
                monthlyScheduleDto.setAccountNumber(recurringDeposits.get(i).getAccountNumber());
                monthlyScheduleDto.setDepositAmount(recurringDeposits.get(i).getDepositAmount());
                monthlyScheduleDto.setMemberNumber(recurringDeposits.get(i).getMember().getMemberNumber());

                String mName=getMonthName(recurringDeposits.get(i).getCreatedOn());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }
        }
        return null;
    }

    @PostMapping("report/monthly-schedule/member")
    public ResponseEntity<InputStreamResource> getMemberReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
            List<Member> members = memberReportService.getMembersReportByDate(depositReportDto.dateFrom,depositReportDto.dateTo);
            List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
            for (int i=0;i<members.size();i++){
                MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
                monthlyScheduleDto.setNumber(i+1);
                monthlyScheduleDto.setMemberNumber(members.get(i).getMemberNumber());
                monthlyScheduleDto.setCustomerId(members.get(i).getCustomer().getId());
                monthlyScheduleDto.setName(members.get(i).getName());
                String mName=getMonthName(members.get(i).getApplicationDate());
                monthlyScheduleDto.setShareAmount(members.get(i).getSharesValue());
                monthlyScheduleDto.setDepositMonth(mName);
                monthlyScheduleDto.setRefundShares(members.get(i).getRefundShares());
                monthlyScheduleDtos.add(monthlyScheduleDto);
            }
            if (depositReportDto.reportType.equalsIgnoreCase("list")) {
                if (CollectionUtils.isEmpty(members)) {
                    return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
            }

        return null;
    }

    @PostMapping("report/monthly-schedule/loan")
    public ResponseEntity<InputStreamResource> getLoanReport(@RequestBody DepositReportDto depositReportDto, HttpServletRequest request, HttpServletResponse response) {
        List<Loan> loans = depositReportService.getLoanReportByDate(depositReportDto.dateFrom,depositReportDto.dateTo);
        List<MonthlyScheduleDto> monthlyScheduleDtos = new ArrayList<>();
        for (int i=0;i<loans.size();i++){
            MonthlyScheduleDto monthlyScheduleDto = new MonthlyScheduleDto();
            monthlyScheduleDto.setNumber(i+1);
            monthlyScheduleDto.setAccountNumber(String.valueOf(loans.get(i).getLoanAccountNumber()));
            monthlyScheduleDto.setName(String.valueOf(loans.get(i).getLoanDetail().getCustomer().getName()));

            String mName=getMonthName(loans.get(i).getLoanDetail().getApprovedOn());
            monthlyScheduleDto.setSanctionedAmount(loans.get(i).getLoanDetail().getSanctionedAmount());
            monthlyScheduleDto.setDepositMonth(mName);
            monthlyScheduleDto.setRepayments(loans.get(i).getRepayments());
            monthlyScheduleDtos.add(monthlyScheduleDto);
        }
        if (depositReportDto.reportType.equalsIgnoreCase("list")) {
            if (CollectionUtils.isEmpty(monthlyScheduleDtos)) {
                return new ResponseEntity(monthlyScheduleDtos, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity(monthlyScheduleDtos, HttpStatus.OK);
        }

        return null;
    }

}
