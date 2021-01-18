package com.dfq.coeffi.cbs.report.loanReports;

import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.dto.EmiOverdueDto;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanInstallments;
import com.dfq.coeffi.cbs.loan.entity.loan.LoanSchedule;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.report.PDFExcelFunction;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.EmiCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LoanReportApi extends BaseController {

    private final LoanReportService loanReportService;
    private final MemberService memberService;
    private final PDFExcelFunction pdfExcelFunction;

    @Autowired
    private LoanReportApi(LoanReportService loanReportService, PDFExcelFunction pdfExcelFunction, MemberService memberService) {
        this.loanReportService = loanReportService;
        this.pdfExcelFunction = pdfExcelFunction;
        this.memberService = memberService;
    }

    @PostMapping("report/loan/gold-loan-recovery")
    public ResponseEntity<InputStreamResource> getGoldLoanRecovery(@RequestBody LoanReportDto loanReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (loanReportDto.reportType.equalsIgnoreCase("list")) {
            List<Loan> goldLoanRecovery = loanReportService.getGoldLoanRecoveryDetailsByAccountNumber(loanReportDto.loanAccountNumberFrom, loanReportDto.loanAccountNumberTo, loanReportDto.exgLoanAccountNumberFrom, loanReportDto.exgLoanAccountNumberTo);
            List<GoldLoanReportDto> goldLoanReportDtos = new ArrayList<>();
            for (Loan loan : goldLoanRecovery) {
                EmiOverdueDto emiOverdueDto = getEmiOverdue(loan);
                GoldLoanReportDto goldLoanReportDto = new GoldLoanReportDto();
                goldLoanReportDto.setLoanAccountNumber(loan.getLoanAccountNumber());
                goldLoanReportDto.setCustomerId(loan.getLoanDetail().getCustomer().getId());
                goldLoanReportDto.setSanctionedDate(loan.getLoanDetail().getApprovedOn());
                goldLoanReportDto.setSanctionedAmount(loan.getLoanDisbursement().getDisbursedAmount());
                goldLoanReportDto.setBalanceAmount(loan.getLoanDetail().getBalanceAmount());
                goldLoanReportDto.setLoanType(String.valueOf(loan.getLoanType()));
                goldLoanReportDto.setName(loan.getLoanDetail().getCustomer().getName());
                goldLoanReportDto.setFamilyMemberName(loan.getLoanDetail().getCustomer().getFamilyMemberDetails().get(0).getName());
                goldLoanReportDto.setTotalRecoveredPrincipleAmount(emiOverdueDto.getTotalRecoveredPrincipleAmount());
                goldLoanReportDto.setTotalPrincipleAmountPending(emiOverdueDto.getTotalPrincipleAmountPending());
                goldLoanReportDto.setTotalOverdueAmount(emiOverdueDto.getTotalOverdueAmount());
                goldLoanReportDto.setOverdueDate(emiOverdueDto.getOverdueDate());
                goldLoanReportDto.setTotalInterestAmountPending(emiOverdueDto.getTotalInterestAmountPending());
                goldLoanReportDto.setTotalPenalInterestPending(emiOverdueDto.getTotalPenalInterestPending());
                goldLoanReportDtos.add(goldLoanReportDto);
            }
            if (CollectionUtils.isEmpty(goldLoanRecovery)) {
                return new ResponseEntity(goldLoanRecovery, HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(goldLoanReportDtos, HttpStatus.OK);
        } else if (loanReportDto.reportType.equalsIgnoreCase("pdf")) {
           /* try {
                return null;
               // return pdfExcelFunction.getCurrentAccountApplicationDetailsPdf(loanReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        } else if (loanReportDto.reportType.equalsIgnoreCase("excel")) {
          /*  try {
                return null;
                //return pdfExcelFunction.getCurrentAccountApplicationDetailsExcel(loanReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        }
        return null;
    }

    @PostMapping("report/loan/gold-loan-register-details")
    public ResponseEntity<InputStreamResource> getGoldLoanRegister(@RequestBody LoanReportDto loanReportDto, HttpServletRequest request, HttpServletResponse response) {
        if (loanReportDto.reportType.equalsIgnoreCase("list")) {
            List<Loan> goldLoanRecovery = loanReportService.getGoldLoanRecoveryDetailsByAccountNumber(loanReportDto.loanAccountNumberFrom, loanReportDto.loanAccountNumberTo, loanReportDto.exgLoanAccountNumberFrom, loanReportDto.exgLoanAccountNumberTo);
            List<GoldLoanReportDto> goldLoanReportDtos = new ArrayList<>();
            for (Loan loan : goldLoanRecovery) {
                EmiOverdueDto emiOverdueDto = getEmiOverdue(loan);
                GoldLoanReportDto goldLoanReportDto = new GoldLoanReportDto();
                goldLoanReportDto.setLoanAccountNumber(loan.getLoanAccountNumber());
                goldLoanReportDto.setCustomerId(loan.getLoanDetail().getCustomer().getId());
                goldLoanReportDto.setSanctionedAmount(loan.getLoanDisbursement().getDisbursedAmount());
                goldLoanReportDto.setLoanType(String.valueOf(loan.getLoanType()));
                goldLoanReportDto.setName(loan.getLoanDetail().getCustomer().getName());
                goldLoanReportDto.setTotalRecoveredPrincipleAmount(emiOverdueDto.getTotalRecoveredPrincipleAmount());
                goldLoanReportDto.setTotalPrincipleAmountPending(emiOverdueDto.getTotalPrincipleAmountPending());
                goldLoanReportDto.setTotalOverdueAmount(emiOverdueDto.getTotalOverdueAmount());
                goldLoanReportDto.setOverdueDate(emiOverdueDto.getOverdueDate());
                goldLoanReportDto.setTotalInterestAmountPending(emiOverdueDto.getTotalInterestAmountPending());
                goldLoanReportDto.setTotalPenalInterestPending(emiOverdueDto.getTotalPenalInterestPending());
                goldLoanReportDto.setSanctionedDate(loan.getLoanDetail().getApprovedOn());
                goldLoanReportDto.setRecoveryDate(emiOverdueDto.getRecoveryDate());
                goldLoanReportDtos.add(goldLoanReportDto);
            }
            if (CollectionUtils.isEmpty(goldLoanRecovery)) {
                return new ResponseEntity(goldLoanReportDtos, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(goldLoanReportDtos, HttpStatus.OK);
        } else if (loanReportDto.reportType.equalsIgnoreCase("pdf")) {
           /* try {
                return null;
               // return pdfExcelFunction.getCurrentAccountApplicationDetailsPdf(loanReportDto);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }*/
        } else if (loanReportDto.reportType.equalsIgnoreCase("excel")) {
          /*  try {
                return null;
                //return pdfExcelFunction.getCurrentAccountApplicationDetailsExcel(loanReportDto, request, response);
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        }
        return null;
    }

    @PostMapping("report/loan/loan-recovery")
    public ResponseEntity<List<LoanMemberDto>> getLoanRecoveryReport(@RequestBody LoanReportDto loanReportDto) {
        List<Loan> loans = null;
        List<LoanMemberDto> loanMemberDtos = new ArrayList<>();
        loans = loanReportService.getAllLoanRecoveryDetail(loanReportDto.getLoanAccountNumberFrom(), loanReportDto.getLoanAccountNumberTo());
        List<GoldLoanReportDto> goldLoanReportDtos = new ArrayList<>();
        if (loans != null && loans.size() > 0) {
            for (Loan loan : loans) {
                EmiOverdueDto emiOverdueDto = getEmiOverdue(loan);
                GoldLoanReportDto goldLoanReportDto = new GoldLoanReportDto();
                goldLoanReportDto.setLoanAccountNumber(loan.getLoanAccountNumber());
                goldLoanReportDto.setCustomerId(loan.getLoanDetail().getCustomer().getId());
                goldLoanReportDto.setSanctionedAmount(loan.getLoanDisbursement().getDisbursedAmount());
                goldLoanReportDto.setLoanType(String.valueOf(loan.getLoanType()));
                goldLoanReportDto.setName(loan.getLoanDetail().getCustomer().getName());
                goldLoanReportDto.setTotalRecoveredPrincipleAmount(emiOverdueDto.getTotalRecoveredPrincipleAmount());
                goldLoanReportDto.setTotalPrincipleAmountPending(emiOverdueDto.getTotalPrincipleAmountPending());
                goldLoanReportDto.setTotalOverdueAmount(emiOverdueDto.getTotalOverdueAmount());
                goldLoanReportDto.setOverdueDate(emiOverdueDto.getOverdueDate());
                goldLoanReportDto.setTotalInterestAmountPending(emiOverdueDto.getTotalInterestAmountPending());
                goldLoanReportDto.setTotalPenalInterestPending(emiOverdueDto.getTotalPenalInterestPending());
                goldLoanReportDto.setSanctionedDate(loan.getLoanDetail().getApprovedOn());
                goldLoanReportDto.setRecoveryDate(emiOverdueDto.getRecoveryDate());
                goldLoanReportDtos.add(goldLoanReportDto);
                System.out.println(goldLoanReportDto);
                if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.GOLD)) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer()));
                    loanMemberDtos.add(loanMemberDto);
                } else if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.LAD) && loan.getLoanDetail().getLadDetails() != null) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(loan.getLoanDetail().getLadDetails().getFixedDeposit().getMember());
                    loanMemberDtos.add(loanMemberDto);
                } else if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.TERM) && loan.getLoanDetail().getTermDetails() != null) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(loan.getLoanDetail().getTermDetails().getMember());
                    loanMemberDtos.add(loanMemberDto);
                }
            }
        }
        return new ResponseEntity<>(loanMemberDtos, HttpStatus.OK);
    }

    @PostMapping("report/loan/loan-disbursed-recovery")
    public ResponseEntity<List<Loan>> getLoanDisbursedRecoveryReport(@RequestBody LoanReportDto loanReportDto) {

        List<Loan> loans = null;
        List<Loan> filteredLoans = new ArrayList<>();

        loans = loanReportService.getAllLoanRecoveryDetail(loanReportDto.getLoanAccountNumberFrom(), loanReportDto.getLoanAccountNumberTo());

        List<Loan> filteredLoan = new ArrayList<>();
        if (loans != null && loans.size() > 0) {

            filteredLoans = loans.stream()
                    .filter(loan -> loan.getLoanStatus().equals(LoanStatus.WITHDRAWN)).collect(Collectors.toList());

        }
        return new ResponseEntity<>(filteredLoans, HttpStatus.OK);
    }

    @PostMapping("report/loan/loan-register")
    public ResponseEntity<List<GoldLoanReportDto>> getLoanRegisterReport(@RequestBody LoanReportDto loanReportDto) {
        List<GoldLoanReportDto> goldLoanReportDtos = new ArrayList<>();
        List<LoanMemberDto> loanMemberDtos = new ArrayList<>();

        List<Loan> loans = null;
        loans = loanReportService.getAllLoanRecoveryDetail(loanReportDto.getLoanAccountNumberFrom(), loanReportDto.getLoanAccountNumberTo());
        if (loans != null && loans.size() > 0) {
            for (Loan loan : loans) {
                EmiOverdueDto emiOverdueDto = getEmiOverdue(loan);
                GoldLoanReportDto goldLoanReportDto = new GoldLoanReportDto();
                goldLoanReportDto.setLoanAccountNumber(loan.getLoanAccountNumber());
                goldLoanReportDto.setCustomerId(loan.getLoanDetail().getCustomer().getId());
                goldLoanReportDto.setSanctionedAmount(loan.getLoanDisbursement().getDisbursedAmount());
                goldLoanReportDto.setLoanType(String.valueOf(loan.getLoanType()));
                goldLoanReportDto.setName(loan.getLoanDetail().getCustomer().getName());
                goldLoanReportDto.setTotalRecoveredPrincipleAmount(emiOverdueDto.getTotalRecoveredPrincipleAmount());
                goldLoanReportDto.setTotalPrincipleAmountPending(emiOverdueDto.getTotalPrincipleAmountPending());
                goldLoanReportDto.setTotalOverdueAmount(emiOverdueDto.getTotalOverdueAmount());
                goldLoanReportDto.setOverdueDate(emiOverdueDto.getOverdueDate());
                goldLoanReportDto.setTotalInterestAmountPending(emiOverdueDto.getTotalInterestAmountPending());
                goldLoanReportDto.setTotalPenalInterestPending(emiOverdueDto.getTotalPenalInterestPending());
                goldLoanReportDto.setSanctionedDate(loan.getLoanDetail().getApprovedOn());
                goldLoanReportDto.setRecoveryDate(emiOverdueDto.getRecoveryDate());
                goldLoanReportDtos.add(goldLoanReportDto);
                System.out.println(goldLoanReportDto);
                /*if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.GOLD)) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer()));
                    loanMemberDtos.add(loanMemberDto);
                } else if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.LAD) && loan.getLoanDetail().getLadDetails() != null) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(loan.getLoanDetail().getLadDetails().getFixedDeposit().getMember());
                    loanMemberDtos.add(loanMemberDto);
                } else if (loan != null && loan.getLoanDetail() != null && loan.getLoanType().equals(LoanType.TERM) && loan.getLoanDetail().getTermDetails() != null) {
                    LoanMemberDto loanMemberDto = new LoanMemberDto();
                    loanMemberDto.setGoldLoanReportDto(goldLoanReportDto);
                    loanMemberDto.setMember(loan.getLoanDetail().getTermDetails().getMember());
                    loanMemberDtos.add(loanMemberDto);
                }*/
            }
        }

        return new ResponseEntity<>(goldLoanReportDtos, HttpStatus.OK);
    }

    private EmiOverdueDto getEmiOverdue(Loan goldLoanRecovery) {
        BigDecimal totalPrincipleAmountPending = BigDecimal.ZERO;
        BigDecimal totalInterestAmountPending = BigDecimal.ZERO;
        BigDecimal totalPenalInterestPending = BigDecimal.ZERO;
        BigDecimal totalRecoveredPrincipleAmount = BigDecimal.ZERO;
        BigDecimal totalOverdueAmount = BigDecimal.ZERO;
        BigDecimal totalRecoveredInterestAmount = BigDecimal.ZERO;
        BigDecimal totalRecoveredPenalInterestAmount = BigDecimal.ZERO;
        BigDecimal totalOverdueInterestAmount = BigDecimal.ZERO;
        BigDecimal totalOverduePenalInterestAmount = BigDecimal.ZERO;
        Date overdueDate = new Date();
        Date recoveryDate = new Date();

        List<EmiOverdueDto> emiUnpaids = new ArrayList<>();
        EmiOverdueDto emiOverdueDto = new EmiOverdueDto();
        Loan loan = goldLoanRecovery;
        LoanSchedule loanSchedules = loan.getLoanSchedule();
        List<LoanInstallments> loanInstallments = null;
        if (loanSchedules != null) {
            loanInstallments = loanSchedules.getInstallments();
            for (LoanInstallments installments : loanInstallments) {
                if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && (installments.getDueDate().after(DateUtil.getTodayDate()) || installments.getDueDate().equals(DateUtil.getTodayDate()))) {
                    totalPrincipleAmountPending = totalPrincipleAmountPending.add(installments.getPrincipleAmount());
                    totalInterestAmountPending = totalInterestAmountPending.add(installments.getInterestAmount());
                    totalPenalInterestPending = BigDecimal.ZERO;
                } else if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("PAID")) {
                    totalRecoveredPrincipleAmount = totalRecoveredPrincipleAmount.add(installments.getPrincipleAmount());
                    totalRecoveredInterestAmount = totalRecoveredInterestAmount.add(installments.getInterestAmount());
                    totalRecoveredPenalInterestAmount = totalRecoveredPenalInterestAmount.add(installments.getInterestAmount());
                    recoveryDate = installments.getDueDate();
                } else if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && (installments.getDueDate().before(DateUtil.getTodayDate()))) {
                    totalOverdueAmount = totalOverdueAmount.add(installments.getPrincipleAmount());
                    totalOverdueInterestAmount = totalOverdueInterestAmount.add(installments.getInterestAmount());
                    totalOverduePenalInterestAmount = totalOverduePenalInterestAmount.add(installments.getInterestAmount());
                    overdueDate = installments.getDueDate();
                }
            }
            emiOverdueDto.setTotalPrincipleAmountPending(totalPrincipleAmountPending);
            emiOverdueDto.setTotalInterestAmountPending(totalInterestAmountPending);
            emiOverdueDto.setTotalPenalInterestPending(totalPenalInterestPending);
            emiOverdueDto.setRecoveryDate(recoveryDate);
            emiOverdueDto.setTotalRecoveredPrincipleAmount(totalRecoveredPrincipleAmount);
            emiOverdueDto.setTotalRecoveredInterestAmount(totalRecoveredInterestAmount);
            emiOverdueDto.setTotalRecoveredPenalInterestAmount(totalRecoveredPenalInterestAmount);
            emiOverdueDto.setTotalOverdueAmount(totalOverdueAmount);
            emiOverdueDto.setTotalOverdueInterestAmount(totalOverdueInterestAmount);
            emiOverdueDto.setTotalOverduePenalInterestAmount(totalOverduePenalInterestAmount);
            emiOverdueDto.setOverdueDate(overdueDate);
        }
        return emiOverdueDto;
    }
}

