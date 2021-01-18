package com.dfq.coeffi.cbs.loan.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.issue.IssueTrackerService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.customer.service.CustomerService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.FixedDepositService;
import com.dfq.coeffi.cbs.deposit.service.PigmyDepositService;
import com.dfq.coeffi.cbs.deposit.service.RecurringDepositService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.document.FileStorageService;
import com.dfq.coeffi.cbs.exception.LoanNotScheduleException;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.dto.EmiOverdueDto;
import com.dfq.coeffi.cbs.loan.dto.LoanOtherChargesDto;
import com.dfq.coeffi.cbs.loan.dto.RepaymentDto;
import com.dfq.coeffi.cbs.loan.dto.ScheduleDto;
import com.dfq.coeffi.cbs.loan.entity.LoanAccountNumberFormat;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.loan.entity.loan.*;
import com.dfq.coeffi.cbs.loan.service.LoanAccountNumberFormatService;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.loan.service.LoanTransactionService;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.roi.loan.LoanRateOfInterest;
import com.dfq.coeffi.cbs.master.service.AccountFormatService;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.LoanRateOfInterestService;
import com.dfq.coeffi.cbs.member.entity.BoardMeeting;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.service.BoardMeetingService;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.user.service.UserService;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.EmiCalculator;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
import com.dfq.coeffi.cbs.utils.TransactionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Resource class which contains the list of API for loan application,
 * sanction, loan schedule, loan repayments, loan closer, transaction etc
 *
 * @author Kapil Kumar
 * @version 1.0
 * @see com.dfq.coeffi.cbs.loan.entity.loan.Loan
 * @since Feb-2019
 */

@RestController
@Slf4j
public class LoanApi extends BaseController {

    private final LoanService loanService;
    private final LoanRateOfInterestService loanRateOfInterestService;
    private final ApplicationLogService applicationLogService;
    private final AccountHeadService accountHeadService;
    private final TransactionService transactionService;
    private final CustomerService customerService;
    private final BODDateService bodDateService;
    private final FixedDepositService fixedDepositService;
    private final LoanTransactionService loanTransactionService;
    private final LoanAccountNumberFormatService loanAccountNumberFormatService;
    private final MemberService memberService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final BankService bankService;
    private final BoardMeetingService boardMeetingService;
    private final AccountFormatService accountFormatService;
    private final SavingsBankDepositService savingsBankDepositService;
    private final SavingBankTransactionService savingBankTransactionService;
    private final RecurringDepositService recurringDepositService;
    private final PigmyDepositService pigmyDepositService;
    private final IssueTrackerService issueTrackerService;

    @Autowired
    public LoanApi(final LoanService loanService, final LoanRateOfInterestService loanRateOfInterestService,
                   final ApplicationLogService applicationLogService, final AccountHeadService accountHeadService,
                   final TransactionService transactionService, final CustomerService customerService,
                   final BODDateService bodDateService, final FixedDepositService fixedDepositService, final LoanTransactionService loanTransactionService,
                   final LoanAccountNumberFormatService loanAccountNumberFormatService,
                   final MemberService memberService, final UserService userService, final FileStorageService fileStorageService,
                   final BankService bankService, final BoardMeetingService boardMeetingService,
                   final AccountFormatService accountFormatService, final SavingsBankDepositService savingsBankDepositService,
                   final SavingBankTransactionService savingBankTransactionService, final RecurringDepositService recurringDepositService,
                   final PigmyDepositService pigmyDepositService, final IssueTrackerService issueTrackerService) {
        this.loanService = loanService;
        this.loanRateOfInterestService = loanRateOfInterestService;
        this.applicationLogService = applicationLogService;
        this.accountHeadService = accountHeadService;
        this.transactionService = transactionService;
        this.customerService = customerService;
        this.bodDateService = bodDateService;
        this.fixedDepositService = fixedDepositService;
        this.loanTransactionService = loanTransactionService;
        this.loanAccountNumberFormatService = loanAccountNumberFormatService;
        this.memberService = memberService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.bankService = bankService;
        this.boardMeetingService = boardMeetingService;
        this.accountFormatService = accountFormatService;
        this.savingsBankDepositService = savingsBankDepositService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.recurringDepositService = recurringDepositService;
        this.pigmyDepositService = pigmyDepositService;
        this.issueTrackerService = issueTrackerService;
    }

    @GetMapping("/loan/gold-loan")
    public ResponseEntity<List<Loan>> getGoldLoans() {

        List<Loan> loans = loanService.getLoanByLoanType(LoanType.GOLD);
        if (CollectionUtils.isEmpty(loans)) {
            log.warn("No active gold loans found");
            throw new EntityNotFoundException("No gold loans found");
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/loan/term-loan")
    public ResponseEntity<List<Loan>> getTermLoans() {

        List<Loan> loans = loanService.getLoanByLoanType(LoanType.TERM);
        if (CollectionUtils.isEmpty(loans)) {
            log.warn("No active gold loans found");
            throw new EntityNotFoundException("No gold terms found");
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/loan/lad-loan")
    public ResponseEntity<List<Loan>> getLadLoans() {

        List<Loan> loans = loanService.getLoanByLoanType(LoanType.LAD);
        if (CollectionUtils.isEmpty(loans)) {
            log.warn("No active gold loans found");
            throw new EntityNotFoundException("No gold lads found");
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    @GetMapping("/loan/customer-loan/{cstId}")
    public ResponseEntity<List<Loan>> getCustomerLoans(@PathVariable long cstId) {

        List<Loan> availableLoans = new ArrayList<>();

        Optional<Customer> customerObj = customerService.getCustomer(cstId);
        if (!customerObj.isPresent()) {
            throw new EntityNotFoundException("No active customer found for the ID : " + cstId);
        }
        Customer customer = customerObj.get();

        List<Loan> loans = loanService.findLoanByCustomer(customer);

        if (loans != null) {
            availableLoans.addAll(loans);
        }
        return new ResponseEntity<>(availableLoans, HttpStatus.OK);
    }

    @GetMapping("/loan/transaction/{loanId}")
    public ResponseEntity<List<LoanTransaction>> getLoanTransaction(@PathVariable long loanId) {

        Optional<Loan> loanObj = loanService.appliedGoldLoan(loanId);
        if (!loanObj.isPresent()) {
            throw new EntityNotFoundException("No active loan found appln. no. " + loanId);
        }
        Loan loan = loanObj.get();

        List<LoanTransaction> transactions = loanTransactionService.getLoanTransaction(loan);
        if (CollectionUtils.isEmpty(transactions)) {
            log.warn("No transactions");
            throw new EntityNotFoundException("No transactions");
        }
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    /**
     * @param loan
     * @return API will create all kind of loan application including GOLD, LAD, TERM, SECURE, UNSECURE etc
     * and will return the submitted application details.
     */
    @PostMapping("/loan/gold-loan")
    public ResponseEntity<Loan> createNewGoldLoan(@RequestBody Loan loan, Principal principal) {

        loan.setActive(true);
        loan.setLoanStatus(LoanStatus.ACTIVE);
        loan.setSubmittedOn(DateUtil.getTodayDate());
        loan.getLoanDetail().setApproved(false);
        loan.getLoanDetail().setPayablePrincipalAmount(new BigDecimal(0));
        loan.getLoanDetail().setPayableInterestAmount(new BigDecimal(0));
        loan.getLoanDetail().setPaidPrincipalAmount(new BigDecimal(0));
        loan.getLoanDetail().setPaidInterestAmount(new BigDecimal(0));
        loan.getLoanDetail().setPaidPenalInterestAmount(new BigDecimal(0));

        if (loan.getLoanDetail().getLoanCode() > 0) {
            LoanRateOfInterest loanRateOfInterest = loanRateOfInterestService.getRateOfInterestById(loan.getLoanDetail().getLoanCode());
            if (loanRateOfInterest != null) {
                loan.getLoanDetail().setLoanRateOfInterest(loanRateOfInterest);
            }
        }

        if (loan.getLoanDetail().getCustomerId() > 0) {
            Optional<Customer> customerObj = customerService.getCustomer(loan.getLoanDetail().getCustomerId());
            if (customerObj.isPresent()) {
                Customer customer = customerObj.get();
                loan.getLoanDetail().setCustomer(customer);
            }
        }

        Member member = memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer());
        loan.setMember(member);

        if (loan.getLoanDetail().getLadDetails() != null && loan.getLoanDetail().getLadDetails().getDepositType().equals(DepositType.FIXED_DEPOSIT)) {

            Optional<FixedDeposit> fixedDepositObj = fixedDepositService.getFixedDepositById(loan.getLoanDetail().getLadDetails().getDepositId());
            if (fixedDepositObj.isPresent()) {
                FixedDeposit fixedDeposit = fixedDepositObj.get();
                loan.getLoanDetail().getLadDetails().setFixedDeposit(fixedDeposit);
            }
        } else if (loan.getLoanDetail().getLadDetails() != null && loan.getLoanDetail().getLadDetails().getDepositType().equals(DepositType.RECURRING_DEPOSIT)) {

            Optional<RecurringDeposit> recurringDepositObj = recurringDepositService.getRecurringDepositById(loan.getLoanDetail().getLadDetails().getDepositId());
            if (recurringDepositObj.isPresent()) {
                RecurringDeposit recurringDeposit = recurringDepositObj.get();
                loan.getLoanDetail().getLadDetails().setRecurringDeposit(recurringDeposit);
            }
        } else if (loan.getLoanDetail().getLadDetails() != null && loan.getLoanDetail().getLadDetails().getDepositType().equals(DepositType.PIGMY_DEPOSIT)) {

            Optional<PigmyDeposit> pigmyDepositObj = pigmyDepositService.getPigmyDepositById(loan.getLoanDetail().getLadDetails().getDepositId());
            if (pigmyDepositObj.isPresent()) {
                PigmyDeposit pigmyDeposit = pigmyDepositObj.get();
                loan.getLoanDetail().getLadDetails().setPigmyDeposit(pigmyDeposit);
            }
        }

        if (loan.getLoanDetail().getTermDetails() != null) {

            if (loan.getLoanDetail().getTermDetails().getTermLoanDocument() != null) {
                Document document = fileStorageService.getDocument(loan.getLoanDetail().getTermDetails().getTermLoanDocument().getId());
                loan.getLoanDetail().getTermDetails().setTermLoanDocument(document);
            }

            Optional<Member> memberObj = memberService.getMember(loan.getGuarantor().getMember().getId());
            if (!memberObj.isPresent()) {
                throw new EntityNotFoundException("Member not found");
            }
            Member guarantorMember = memberObj.get();
            loan.getGuarantor().setMember(guarantorMember);
        }
        if (loan.getLoanDetail().getTermDetails() != null && loan.getLoanDetail().getTermDetails().getTdrType().equalsIgnoreCase("Unsecured")) {

            if (loan.getLoanDetail().getTermDetails().getSuretyType().equalsIgnoreCase("PL")) {
                if (loan.getLoanDetail().getTermDetails().getMemberId() > 0) {
                    Optional<Member> plMemberObj = memberService.getMember(loan.getLoanDetail().getTermDetails().getMemberId());

                    Member plMember = plMemberObj.get();
                    loan.getLoanDetail().getTermDetails().setMember(plMember);
                }
            }

            if (loan.getLoanDetail().getTermDetails().getSuretyType().equalsIgnoreCase("SL")) {
                if (loan.getLoanDetail().getTermDetails().getUserId() > 0) {
                    // member id
                    //TODO
                    //User user = userService.getUser(loan.getLoanDetail().getTermDetails().getUserId());
                    Optional<Customer> customer = customerService.getCustomer(loan.getLoanDetail().getTermDetails().getUserId());
                    loan.getLoanDetail().getTermDetails().setStaff(customer.get());
                }
            }
        }
        LoanAccountNumberFormat loanAccountNumberFormat = loanAccountNumberFormatService.getAccountNumberFormat();

        loan.setApplicationNumber(loanAccountNumberFormat.getApplicationStartNumber());
        Loan persistedObject = loanService.applyLean(loan);
        if (persistedObject != null) {

            if (loan.getLoanDetail().getApplicationFee() != null && loan.getLoanDetail().getApplicationFee().intValue() > 0) {
                applicationFeeCreditEntry(loan);
            }

            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan application no. " + persistedObject.getId() + " submitted",
                    "LOAN APPLICATION SUBMIT", loggedUser.getId());
            loanAccountNumberFormat.setApplicationStartNumber(loanAccountNumberFormat.getApplicationStartNumber() + 1);
            loanAccountNumberFormatService.updateLoanAccountNumber(loanAccountNumberFormat);
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    @GetMapping("/loan/gold-loan/{id}")
    public ResponseEntity<Loan> getGoldLoan(@PathVariable long id) {

        Optional<Loan> goldLoanObj = loanService.appliedGoldLoan(id);
        if (!goldLoanObj.isPresent()) {
            log.warn("No active loan loan found");
            throw new EntityNotFoundException("gold-loan for id not found :" + id);
        }
        Loan loan = goldLoanObj.get();
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/loan/gold-loan-by-application/{applicationId}")
    public ResponseEntity<Loan> getGoldLoanByApplicationNumber(@PathVariable long applicationId) {

        Loan loan = null;
        try {
            Optional<Loan> goldLoanObj = loanService.findGoldLoanByApplicationNo(applicationId);
            if (!goldLoanObj.isPresent()) {
                log.warn("No active gold loan found for the provided application no.");
                throw new EntityNotFoundException("No active loan found for the provided application no :" + applicationId);
            }
            loan = goldLoanObj.get();
        } catch (NumberFormatException e) {
            throw new EntityNotFoundException("Please enter valid loan application number");
        }
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @GetMapping("/loan/loan-by-account-number/{accountNumber}")
    public ResponseEntity<Loan> getGoldLoanByAccountNumber(@PathVariable String accountNumber) {
        Loan loan = null;
        try {
            Optional<Loan> goldLoanObj = loanService.findLoanByAccountNo(accountNumber);
            if (!goldLoanObj.isPresent()) {
                log.warn("No active loan found for the provided Acc No.");
                throw new EntityNotFoundException("No active loan found for the provided Acc No :" + accountNumber);
            }
            loan = goldLoanObj.get();
        } catch (NumberFormatException e) {
            throw new EntityNotFoundException("Please enter valid loan application number");
        }
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    /**
     * @param accountNumber
     * @return Based on the loan account number loan will close &
     * will check the condition like recovery or paid amount
     * should be greater than or equal to loan disbursed amount i.e
     * including Principal & Interest.
     */
    @GetMapping("/loan/close/{accountNumber}")
    public ResponseEntity<Loan> loanClose(@PathVariable String accountNumber, Principal principal) {

        User loggedUser = getLoggedUser(principal);

        Loan persistedLoan = null;
        Loan loan = null;
        try {
            Optional<Loan> goldLoanObj = loanService.findLoanByAccountNo(accountNumber);
            if (!goldLoanObj.isPresent()) {
                log.warn("No active loan found for the provided Acc No.");
                throw new EntityNotFoundException("No active loan found for the provided Acc No :" + accountNumber);
            }
            loan = goldLoanObj.get();

            if (loan != null) {
                BigDecimal payableAmount = new BigDecimal(0);
                if (loan.getLoanSchedule() != null && loan.getLoanSchedule().getInstallments() != null && loan.getLoanSchedule().getInstallments().size() > 0) {
                    for (LoanInstallments installment : loan.getLoanSchedule().getInstallments()) {
                        payableAmount = payableAmount.add(installment.getEmiAmount());
                    }
                }

                if (loan.getLoanDetail().getRepaidAmount().intValue() >= payableAmount.intValue()) {
                    loan.setLoanStatus(LoanStatus.LOAN_CLOSED);
                    loan.getLoanDetail().setLoanClosedOn(DateUtil.getTodayDate());
                    loan.getLoanDetail().setLoanClosedBy(loggedUser);
                    loan.setActive(false);
                    persistedLoan = loanService.applyLean(loan);

                    if (persistedLoan != null) {
                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan Account No. " + loan.getLoanAccountNumber() + " closed by :  " + loggedUser.getFirstName(),
                                "PUT", loggedUser.getId());
                    }
                } else {
                    throw new EntityNotFoundException("Loan amount not recovered!!! You cannot close the loan");
                }
            }
        } catch (NumberFormatException e) {
            throw new EntityNotFoundException("Please enter valid loan account number");
        }
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @PostMapping("/loan/gold-loan-approval")
    public ResponseEntity<Loan> goldLoanApproval(@RequestBody final Loan goldLoan, Principal principal) {

        AccountFormat accountFormat = null;
        String accountNumber = " ";

        if (goldLoan.getLoanDetail() != null) {
            if (goldLoan.getBdrNo() > 0) {
                Loan isApproved = null;
                Optional<BoardMeeting> boardMeetingObj = boardMeetingService.getBoardMeeting(goldLoan.getBdrNo());
                if (boardMeetingObj.isPresent() && boardMeetingObj.get().getLoanApplications() != null) {
                    List<Loan> approvedLoans = boardMeetingObj.get().getLoanApplications();
                    for (Loan approvedLoan : approvedLoans) {
                        if (approvedLoan.getApplicationNumber() == goldLoan.getApplicationNumber()) {
                            isApproved = approvedLoan;
                        }
                    }
                    if (isApproved == null) {
                        throw new EntityNotFoundException("Loan is not approved in the board meeting");
                    }
                } else {
                    throw new EntityNotFoundException("No such BDR No created");
                }
            }
        }

        Optional<Loan> goldLoanObject = loanService.appliedGoldLoan(goldLoan.getId());

        if (!goldLoanObject.isPresent()) {
            throw new EntityNotFoundException("No loan found for the submitted application");
        }
        Loan loan = goldLoanObject.get();

        if (loan.getLoanType() == LoanType.GOLD) {
            accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.LOAN, "GOLD");
            if (accountFormat == null) {
                throw new EntityNotFoundException("Need master data for generate account number");
            }
            accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);
        } else if (loan.getLoanType() == LoanType.LAD) {
            accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.LOAN, "LAD");
            if (accountFormat == null) {
                throw new EntityNotFoundException("Need master data for generate account number");
            }
            accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);
        } else if (loan.getLoanType() == LoanType.TERM) {
            accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.LOAN, "TERM");
            if (accountFormat == null) {
                throw new EntityNotFoundException("Need master data for generate account number");
            }
            accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);
        }

        LoanDetail detail = loan.getLoanDetail();
        detail.setApproved(true);
        detail.setApprovedOn(new Date());
        detail.setSanctionedAmount(goldLoan.getLoanDetail().getSanctionedAmount());
        detail.setRateOfInterest(goldLoan.getLoanDetail().getRateOfInterest());
        detail.setPriority(goldLoan.getLoanDetail().isPriority());
        loan.setLoanDetail(detail);
        loan.setLoanStatus(LoanStatus.APPROVED);
        loan.setLoanAccountNumber(accountNumber);

        Loan persistedObject = loanService.applyLean(loan);

        if (persistedObject != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan (" + persistedObject.getLoanType() + ") application no. " + persistedObject.getId() + " sanctioned ",
                    "LOAN SANCTION", loggedUser.getId());

            accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
            accountFormatService.saveAccountNumber(accountFormat);
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    @PostMapping("/loan/schedule-gold-loan/{applicationId}")
    public ResponseEntity<Loan> scheduleGoldLoan(@PathVariable long applicationId, @RequestBody LoanSchedule loanSchedule, Principal principal) {

        Loan scheduledLoan = null;
        Optional<Loan> goldLoanObj = loanService.findGoldLoanByApplicationNo(applicationId);
        if (!goldLoanObj.isPresent()) {
            log.warn("No active loan found for the provided application no.");
            throw new EntityNotFoundException("No active loan found for the provided application no : " + applicationId);

        }
        Loan loan = goldLoanObj.get();

        if (loan.getLoanStatus().equals(LoanStatus.APPROVED)) {

            List<LoanInstallments> loanInstallments = loanSchedule.getInstallments();
            if (loanInstallments != null) {
                for (int i = 0; i < loanInstallments.size(); i++) {
                    loanInstallments.get(i).setLoanAccountNumber("" + loan.getLoanAccountNumber());
                    loanInstallments.get(i).setDueDate(loanInstallments.get(i).getDueDate());
                    loanInstallments.get(i).setPaymentDate(loanInstallments.get(i).getDueDate());
                    loanInstallments.get(i).setInstallmentNumber(i + 1);
                    loanInstallments.get(i).setLoanEmiStatus(LoanEmiStatus.UNPAID);
                }
            }
            loanSchedule.setInstallments(loanInstallments);

            loan.setLoanSchedule(loanSchedule);
            loan.setLoanStatus(LoanStatus.SCHEDULED);
            scheduledLoan = loanService.applyLean(loan);

            if (scheduledLoan != null) {
                User loggedUser = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan application no. " + loggedUser.getId() + " scheduled ",
                        "Loan Scheduled", loggedUser.getId());
            }
        } else {
            throw new EntityNotFoundException("Applied loan is not yet approved!!!! Please wait for approve " + applicationId);
        }
        return new ResponseEntity<>(scheduledLoan, HttpStatus.OK);
    }

    @PostMapping("/loan/loan-other-charges")
    public ResponseEntity<Loan> loanOtherCharges(@RequestBody LoanOtherChargesDto loanOtherChargesDto, Principal principal) {

        bodDateService.checkBOD();
        Loan persistedLoanObject = null;
        User loggedUser = getLoggedUser(principal);

        Optional<Loan> appliedLoanObj = loanService.findLoanByAccountNo(loanOtherChargesDto.getLoanAccountNo());
        if (!appliedLoanObj.isPresent()) {
            log.warn("No active loan found for the provided account no.");
            throw new EntityNotFoundException("No active loan found for the provided Account Number :" + loanOtherChargesDto.getLoanAccountNo());
        }
        Loan providedLoan = appliedLoanObj.get();
        if (providedLoan != null && providedLoan.getLoanStatus().equals(LoanStatus.WITHDRAWN)) {

            List<LoanOtherCharges> charges = null;
            if (providedLoan.getLoanOtherCharges() != null) {
                charges = providedLoan.getLoanOtherCharges();
            } else {
                charges = new ArrayList<>();
            }

            LoanOtherCharges loanOtherCharges = new LoanOtherCharges();
            loanOtherCharges.setAmount(loanOtherChargesDto.getAmount());
            loanOtherCharges.setRemark(loanOtherChargesDto.getRemark());
            loanOtherCharges.setChequeNo(loanOtherChargesDto.getChequeNo());
            loanOtherCharges.setLoanAccountNo(loanOtherChargesDto.getLoanAccountNo());
            loanOtherCharges.setDebitType(loanOtherChargesDto.getDebitType());

            if (loanOtherChargesDto.getCreditAccountId() > 0) {
                AccountHead creditAccountHead = accountHeadService.getAccountHead(loanOtherChargesDto.getCreditAccountId());
                loanOtherCharges.setCreditAccount(creditAccountHead);
            }

            if (loanOtherChargesDto.getDebitAccountId() > 0) {
                AccountHead debitAccountHead = accountHeadService.getAccountHead(loanOtherChargesDto.getDebitAccountId());
                loanOtherCharges.setDebitAccount(debitAccountHead);
            }

            if (providedLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanOtherCharges.setLedger(ledger);
            } else if (providedLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanOtherCharges.setLedger(ledger);
            } else if (providedLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanOtherCharges.setLedger(ledger);
            }

            charges.add(loanOtherCharges);
            providedLoan.setLoanOtherCharges(charges);
            persistedLoanObject = loanService.applyLean(providedLoan);

            if (persistedLoanObject != null) {
                otherChargesTransactionDebitEntry(loanOtherCharges, providedLoan, loggedUser);
                otherChargesTransactionCreditEntry(loanOtherCharges, providedLoan, loggedUser);

                // Logs loan transaction
                otherPaymentLoanTransactionEntry(providedLoan, loanOtherCharges, loggedUser);
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Other payment " + loggedUser.getId() + " added ",
                        "Other loan charges", loggedUser.getId());
            }
        } else {
            throw new EntityNotFoundException("Loan is not yet disbursed!!!! You cannot make any transactions");
        }
        return new ResponseEntity<>(persistedLoanObject, HttpStatus.OK);
    }

    @PostMapping("/loan/disburse-gold-loan/{applicationId}")
    public ResponseEntity<Loan> disburseGoldLoan(@PathVariable long applicationId, @RequestBody LoanDisbursement loanDisbursement, Principal principal) {

        bodDateService.checkBOD();

        TransactionValidation.checkSocietyBalance(loanDisbursement.getDisbursedAmount(), bankService.getActiveBank().get().getBalance());
        Loan disbursedLoan = null;
        User loggedUser = getLoggedUser(principal);

        Optional<Loan> goldLoanObj = loanService.findGoldLoanByApplicationNo(applicationId);
        if (!goldLoanObj.isPresent()) {
            log.warn("No active loan found for the provided application no.");
            throw new EntityNotFoundException("No active loan found for the provided application no :" + applicationId);
        }
        Loan loan = goldLoanObj.get();

        if (loan.getLoanStatus().equals(LoanStatus.SCHEDULED)) {

            loanDisbursement.setDisbursedAmount(loan.getLoanDetail().getSanctionedAmount());
            loan.getLoanDetail().setBalanceAmount(loan.getLoanDetail().getSanctionedAmount());
            loanDisbursement.setDisbursedOn(DateUtil.getTodayDate());
            loanDisbursement.setDisbursedBy(loggedUser);

            // Setting credit account for the loan
            setCreditAccountHead(loanDisbursement);

            // Setting debit account for the loan
            setDebitAccountHead(loanDisbursement);

            if (loan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanDisbursement.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanDisbursement.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                loanDisbursement.setLedger(ledger);
            }

            Member member = memberService.getMemberByCustomer(loan.getLoanDetail().getCustomer());

            loan.setLoanDisbursement(loanDisbursement);
            loan.setLoanStatus(LoanStatus.WITHDRAWN);

            //    ###########   NEW CODE  ##########

            BigDecimal sanctionedAmount = loan.getLoanDetail().getSanctionedAmount();

            List<LoanInstallments> loanInstallments = loan.getLoanSchedule().getInstallments();

            BigDecimal totalEmiAmount = new BigDecimal(0);
            for (LoanInstallments installment : loanInstallments) {

                BigDecimal emiAmount = installment.getEmiAmount();

                totalEmiAmount = totalEmiAmount.add(emiAmount);
            }
            BigDecimal payableInterestAmount = totalEmiAmount.subtract(sanctionedAmount);

            loan.getLoanDetail().setPayablePrincipalAmount(sanctionedAmount);
            loan.getLoanDetail().setPayableInterestAmount(payableInterestAmount);

//            ######################

            disbursedLoan = loanService.applyLean(loan);

            if (disbursedLoan != null) {

                // Invord entry for other charges

                if (loanDisbursement.getOtherCharges() != null) {
                    //Credit entry in case of other charges
                    disbursementOtherPaymentTransactionEntry(loan, loanDisbursement.getOtherCharges());
                }

                if (disbursedLoan.getLoanDisbursement().getCreditMode().equalsIgnoreCase("Cash")) {

                    debitFromCashAccount(disbursedLoan);

                    disburseLoanDebitEntry(disbursedLoan);
                } else if (disbursedLoan.getLoanDisbursement().getCreditMode().equalsIgnoreCase("Bank")) {
                    debitFromBankAccount(disbursedLoan);

                    disburseLoanDebitEntry(disbursedLoan);
                } else if (disbursedLoan.getLoanDisbursement().getCreditMode().equalsIgnoreCase("Transfer")) {
                    SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(member.getMemberNumber());
                    if (savingsBankDeposit == null) {
                        throw new EntityNotFoundException("Saving Bank Account Not Found ");
                    }
                    transactionEntry(disbursedLoan);
                }

                // Transaction entry in the main table
                disbursementLoanTransactionEntry(disbursedLoan, loanDisbursement);

                // For society balance update
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(disbursedLoan.getLoanDisbursement().getDisbursedAmount(), "CREDIT");

                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Loan account no. " + disbursedLoan.getLoanAccountNumber() + " disbursed ",
                        "Loan Disbursed", loggedUser.getId());
            }
        } else {
            throw new LoanNotScheduleException("Applied loan is not yet scheduled!!!! Please wait for schedule " + applicationId,
                    "User trying to disburse loan without scheduling", issueTrackerService, loggedUser);
        }
        return new ResponseEntity<>(disbursedLoan, HttpStatus.OK);
    }

    /**
     * @param repaymentDto <p> Method used for loan repayments and will update the credit entries at society level,
     *                     transaction level and will also update in loan transaction table</p>
     * @return
     * @see # repaymentTransactionCreditEntry(repayment, loggedUser);
     * @see # repaymentLoanTransactionEntry(providedLoan, repayment);
     * @see # getUpdateSocietyBalance(repayment.getReceivedAmount(), "CREDIT");
     * @see # recordApplicationLog(loggedUser.getFirstName(), message, "Loan Repayment", loggedUser.getId()
     */
    @PostMapping("/loan/loan-repayment")
    public ResponseEntity<Loan> loanRepayment(@RequestBody RepaymentDto repaymentDto, Principal principal) {

        SavingsBankDeposit savingsBankDeposit = null;
        Loan persistedLoanObject = null;
        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        Optional<Loan> appliedLoanObj = loanService.findLoanByAccountNo(repaymentDto.getLoanAccountNo());
        if (!appliedLoanObj.isPresent()) {
            log.warn("No active loan found for the provided account no.");
            throw new EntityNotFoundException("No active loan found for the provided Account Number :" + repaymentDto.getLoanAccountNo());
        }
        Loan providedLoan = appliedLoanObj.get();
        if (providedLoan != null && providedLoan.getLoanStatus().equals(LoanStatus.WITHDRAWN)) {

            if (repaymentDto.getCreditType().equalsIgnoreCase("Transfer")) {
                savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(providedLoan.getMember().getMemberNumber());
                if (savingsBankDeposit != null && savingsBankDeposit.getBalance().intValue() < repaymentDto.getReceivedAmount().intValue()) {
                    throw new EntityNotFoundException("Not enough amount in saving/current bank account");
                } else if (savingsBankDeposit == null) {
                    throw new EntityNotFoundException("No saving/current bank account found member");
                }

            }

            List<Repayment> repayments = null;
            if (providedLoan.getRepayments() != null) {
                repayments = providedLoan.getRepayments();
            } else {
                repayments = new ArrayList<>();
            }

            Repayment repayment = new Repayment();
            repayment.setRemark(repaymentDto.getRemark());
            repayment.setChequeNo(repaymentDto.getChequeNo());
            repayment.setLoanAccountNo(repaymentDto.getLoanAccountNo());
            repayment.setCreditType(repaymentDto.getCreditType());
            repayment.setRepaymentDoneBy(loggedUser);
            repayment.setRepaymentOn(DateUtil.getTodayDate());
            repayment.setPaidPrincipal(repaymentDto.getPrincipal());
            repayment.setPaidInterest(repaymentDto.getInterest());
            repayment.setPaidPenalInterest(repaymentDto.getPenalInterest());
            repayment.setReceivedAmount(repaymentDto.getReceivedAmount());

            if (repaymentDto.getCreditAccountId() > 0) {
                AccountHead creditAccountHead = accountHeadService.getAccountHead(repaymentDto.getCreditAccountId());
                repayment.setCreditAccount(creditAccountHead);
            }

            if (providedLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                repayment.setLedger(ledger);
                repayment.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                repayment.setLedger(ledger);
                repayment.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                repayment.setLedger(ledger);
                repayment.setAccountHead(ledger.getAccountHead());
            }

            repayments.add(repayment);
            providedLoan.setRepayments(repayments);

            BigDecimal balance = providedLoan.getLoanDetail().getBalanceAmount();
            balance = balance.subtract(repaymentDto.getPrincipal());
            providedLoan.getLoanDetail().setBalanceAmount(balance);

            BigDecimal repaidAmount;
            if (providedLoan.getLoanDetail().getRepaidAmount() != null) {
                repaidAmount = providedLoan.getLoanDetail().getRepaidAmount();
                repaidAmount = repaidAmount.add(repayment.getReceivedAmount());
            } else {
                repaidAmount = new BigDecimal(0);
                repaidAmount = repaidAmount.add(repayment.getReceivedAmount());
            }

            providedLoan.getLoanDetail().setRepaidAmount(repaidAmount);

//            ############ NEW CODE ###########

            BigDecimal repaymentPaidPrincipal = repaymentDto.getPrincipal();
            BigDecimal repaymentPaidInterest = repaymentDto.getInterest();
            BigDecimal repaymentPaidPenalInterest = repaymentDto.getPenalInterest();

            BigDecimal paidPrincipalAmount = providedLoan.getLoanDetail().getPaidPrincipalAmount();
            BigDecimal paidInterestAmount = providedLoan.getLoanDetail().getPaidInterestAmount();
            BigDecimal paidPenalInterestAmount = providedLoan.getLoanDetail().getPaidPenalInterestAmount();

            BigDecimal interestAmount;

            if (repaymentPaidPrincipal != null) {
                providedLoan.getLoanDetail().setPaidPrincipalAmount(paidPrincipalAmount.add(repaymentPaidPrincipal));
            }
            if (repaymentPaidInterest != null) {
                providedLoan.getLoanDetail().setPaidInterestAmount(paidInterestAmount.add(repaymentPaidInterest));
            }
            if (repaymentPaidPenalInterest != null) {
                providedLoan.getLoanDetail().setPaidPenalInterestAmount(paidPenalInterestAmount.add(repaymentPaidPenalInterest));
                interestAmount = repaymentPaidInterest.add(repaymentPaidPenalInterest);
            } else {
                interestAmount = repaymentPaidInterest;
            }

            BigDecimal principalAmount = repaymentPaidPrincipal;

//            ################################

            persistedLoanObject = loanService.applyLean(providedLoan);

            if (persistedLoanObject != null) {

                if (repaymentDto.getCreditType().equalsIgnoreCase("Cash")) {
                    //Credit Entry to Cash Account
                    creditToCashAccount(providedLoan, repayment);
                    repaymentTransactionDebitEntry(repayment, providedLoan, loggedUser, principalAmount);

                    repaymentLoanTransactionEntry(providedLoan, repayment);

                } else if (repaymentDto.getCreditType().equalsIgnoreCase("Bank")) {
                    //Credit Entry to Bank Account
                    creditToBankAccount(providedLoan, repayment);
                    repaymentTransactionDebitEntry(repayment, providedLoan, loggedUser, principalAmount);
                    repaymentLoanTransactionEntry(providedLoan, repayment);

                } else if (repaymentDto.getCreditType().equalsIgnoreCase("Transfer")) {
                    //Debit from to Saving/Current Bank Account
                    accountDebitAccountTransaction(providedLoan, savingsBankDeposit, repaymentDto.getReceivedAmount());
                    // Credit to bank acount
                    creditToBankAccount(providedLoan, repayment);
                    repayment.setCreditType("BankTransfer");
                    //repaymentTransactionDebitEntry(repayment, providedLoan, loggedUser, principalAmount);
                    loanTransactionEntry(providedLoan, repaymentDto.getReceivedAmount());


                }
                repaymentTransactionCreditEntryForInterestAmount(repayment, providedLoan, loggedUser, interestAmount);

                //to update loan installment table
//                saveEMIRepayment(persistedLoanObject.getLoanAccountNumber());

                // For society balance update
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(repayment.getReceivedAmount(), "CREDIT");

                String message = "Loan repayment against account number " + repayment.getLoanAccountNo() + " of amount " + repayment.getReceivedAmount() + " received";
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), message,
                        "Loan Repayment", loggedUser.getId());
            }
        } else {
            throw new EntityNotFoundException("Loan is not yet disbursed!!!! You cannot make any transactions");
        }
        return new ResponseEntity<>(persistedLoanObject, HttpStatus.OK);
    }

    @PostMapping("/loan/emi-installments")
    public ResponseEntity<LoanInstallments> getEmiInstallmentList(@RequestBody ScheduleDto scheduleDto) {
        Optional<Loan> loanObj = loanService.findLoanByAccountNo(scheduleDto.getLoanAccountNumber());
        Loan loan = loanObj.get();
        List<LoanInstallments> loanInstallments = EmiCalculator.calculateEmi(loan.getLoanDetail().getSanctionedAmount().doubleValue(), loan.getLoanDetail().getLoanRateOfInterest().getRegularRateOfInterest(), scheduleDto.getInstallments(), scheduleDto.getRepaymentDate());

        if (loanInstallments.isEmpty()) {
            log.warn("No active LoanInstallments found for the provided account number");
            throw new EntityNotFoundException("No active LoanInstallments found for the provided account number:");
        }
        return new ResponseEntity(loanInstallments, HttpStatus.OK);
    }

    @GetMapping("/loan/unapproved-loan")
    public ResponseEntity<List<Loan>> getUnApprovedLoans() {

        List<Loan> loans = loanService.appliedGoldLoans(false);
        if (CollectionUtils.isEmpty(loans)) {
            log.warn("No loan available for approval");
            throw new EntityNotFoundException("No loan available for approval");
        }
        return new ResponseEntity<>(loans, HttpStatus.OK);
    }

    private void setCreditAccountHead(LoanDisbursement loanDisbursement) {
        if (loanDisbursement != null && loanDisbursement.getCreditAccountCode() > 0) {
            AccountHead creditAccount = accountHeadService.getAccountHead(loanDisbursement.getCreditAccountCode());
            loanDisbursement.setCreditAccount(creditAccount);
        }
    }

    private void setDebitAccountHead(LoanDisbursement loanDisbursement) {
        if (loanDisbursement != null && loanDisbursement.getDebitAccountCode() > 0) {
            AccountHead debitAccount = accountHeadService.getAccountHead(loanDisbursement.getDebitAccountCode());
            loanDisbursement.setDebitAccount(debitAccount);
        }
    }

    private void disburseLoanDebitEntry(Loan scheduledLoan) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            debitTransaction.setCreditAmount(scheduledLoan.getLoanDisbursement().getDisbursedAmount());
            debitTransaction.setRemark("Amount debited for disbursement loan no. " + scheduledLoan.getLoanAccountNumber());
            debitTransaction.setDebitAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(scheduledLoan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("CREDIT");
            debitTransaction.setAccountNumber("" + scheduledLoan.getLoanAccountNumber());
            debitTransaction.setTransferType(scheduledLoan.getLoanDisbursement().getCreditMode());
            debitTransaction.setChequeDate(scheduledLoan.getLoanDisbursement().getChequeDate());
            debitTransaction.setChequeNo(scheduledLoan.getLoanDisbursement().getChequeNo());
            debitTransaction.setAccountName(scheduledLoan.getMember().getName() + " (" + scheduledLoan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(debitTransaction.getCreditAmount());
            debitTransaction.setBalance(balance);

            if (scheduledLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (scheduledLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (scheduledLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }
            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void transactionEntry(Loan scheduledLoan) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(scheduledLoan.getLoanDisbursement().getDisbursedAmount());
            debitTransaction.setRemark("Amount debited for disbursement loan no. " + scheduledLoan.getId());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(scheduledLoan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber("" + scheduledLoan.getLoanAccountNumber());
            debitTransaction.setTransferType(scheduledLoan.getLoanDisbursement().getCreditMode());
            debitTransaction.setAccountName(scheduledLoan.getMember().getName() + " (" + scheduledLoan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);

            if (scheduledLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (scheduledLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (scheduledLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }
            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);


            // For credit transaction against the loan

            Member member = memberService.getMemberByCustomer(scheduledLoan.getLoanDetail().getCustomer());

            SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(member.getMemberNumber());

            if (savingsBankDeposit == null) {
                throw new EntityNotFoundException("Saving Bank Account Not Found ");
            }

            Ledger creditLedger = null;
            if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                creditLedger = accountHeadService.getLedgerByName("Saving Bank Deposit");
            } else {
                creditLedger = accountHeadService.getLedgerByName("Current Account");
            }
            if (creditLedger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }

            SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

            if (savingBankTransaction != null) {

                BigDecimal depositBalance = savingBankTransaction.getBalance();
                depositBalance = depositBalance.add(scheduledLoan.getLoanDisbursement().getDisbursedAmount());

                SavingBankTransaction sbTransaction = new SavingBankTransaction();

                sbTransaction.setTransactionType("CREDIT");
                sbTransaction.setCreditAmount(scheduledLoan.getLoanDisbursement().getDisbursedAmount());
                sbTransaction.setBalance(depositBalance);
                sbTransaction.setDebitAmount(BigDecimal.ZERO);
                sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

                savingBankTransactionService.createSavingBankTransaction(sbTransaction);

                savingsBankDeposit.setBalance(depositBalance);

                savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
            }

            Transaction creditTransaction = new Transaction();
//            creditTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getCreditAccount());
            creditTransaction.setCreditAmount(scheduledLoan.getLoanDisbursement().getDisbursedAmount());
            creditTransaction.setRemark("Amount credited for disbursement loan no. " + scheduledLoan.getId());
            creditTransaction.setTransactionBy(scheduledLoan.getLoanDisbursement().getDisbursedBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber("" + savingsBankDeposit.getAccountNumber());
            creditTransaction.setTransferType(scheduledLoan.getLoanDisbursement().getCreditMode());
            creditTransaction.setAccountName(scheduledLoan.getMember().getName() + " (" + scheduledLoan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            if (persistedDebitTransaction != null) {
                BigDecimal balanceAsOf = persistedDebitTransaction.getBalance();
                balanceAsOf = balanceAsOf.add(creditTransaction.getCreditAmount());
                creditTransaction.setBalance(balanceAsOf);
            }

            creditTransaction.setAccountHead(creditLedger.getAccountHead());
            creditTransaction.setLedger(creditLedger);

            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void disbursementOtherPaymentTransactionEntry(Loan loan, BigDecimal serviceCharges) {

        Ledger ledger = accountHeadService.getLedgerByName("Loan Service Charges");

        Transaction latestTransaction = transactionService.latestTransaction();

        BigDecimal balance = latestTransaction.getBalance();
        balance = balance.add(serviceCharges);

        Transaction creditTransaction = new Transaction();

        creditTransaction.setCreditAmount(serviceCharges);
        creditTransaction.setRemark("Loan other service charged credited :  " + loan.getLoanAccountNumber());
        creditTransaction.setTransactionBy(loan.getLoanDisbursement().getDisbursedBy());
        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditTransaction.setTransactionType("CREDIT");
        creditTransaction.setDebitAmount(new BigDecimal(0));
        creditTransaction.setAccountNumber("" + loan.getLoanAccountNumber());
        creditTransaction.setTransferType(loan.getLoanDisbursement().getCreditMode());
        creditTransaction.setBalance(balance);
        creditTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");
        creditTransaction.setAccountHead(ledger.getAccountHead());
        creditTransaction.setLedger(ledger);

        transactionService.transactionEntry(creditTransaction);

        TransactionUtil transactionUtil = new TransactionUtil(bankService);
        transactionUtil.getUpdateSocietyBalance(serviceCharges, "CREDIT");
    }

    private void otherChargesTransactionDebitEntry(LoanOtherCharges loanOtherCharges, Loan providedLoan, User user) {
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {

            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(loanOtherCharges.getDebitAccount());
            debitTransaction.setDebitAmount(loanOtherCharges.getAmount());
            debitTransaction.setRemark("Amount debited for loan Account No. " + loanOtherCharges.getLoanAccountNo());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber("" + loanOtherCharges.getLoanAccountNo());
            debitTransaction.setTransferType(loanOtherCharges.getDebitType());
            debitTransaction.setAccountName(providedLoan.getMember().getName() + " (" + providedLoan.getMember().getMemberNumber() + ")");

            // Update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);

            if (providedLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }

            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);
            if (persistedDebitTransaction != null) {

                // For society balance update
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(loanOtherCharges.getAmount(), "DEBIT");

                String message = "Loan other charges for account no. " + loanOtherCharges.getLoanAccountNo() + " amount " + loanOtherCharges.getAmount() + " debited";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Transaction Module", user.getId());
            }
        }
    }

    private void otherChargesTransactionCreditEntry(LoanOtherCharges loanOtherCharges, Loan providedLoan, User user) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {

            // For credit transaction against the loan
            Transaction creditTransaction = new Transaction();
//            creditTransaction.setAccountHead(loanOtherCharges.getCreditAccount());
            creditTransaction.setCreditAmount(loanOtherCharges.getAmount());
            creditTransaction.setRemark("Loan other charges against account no " + loanOtherCharges.getLoanAccountNo() + "added");
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber("" + loanOtherCharges.getLoanAccountNo());
            creditTransaction.setTransferType(loanOtherCharges.getDebitType());
            creditTransaction.setAccountName(providedLoan.getMember().getName() + " (" + providedLoan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            if (latestTransaction != null) {
                BigDecimal balanceAsOf = latestTransaction.getBalance();
                balanceAsOf = balanceAsOf.add(creditTransaction.getCreditAmount());
                creditTransaction.setBalance(balanceAsOf);
            }

            if (providedLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                creditTransaction.setLedger(ledger);
                creditTransaction.setAccountHead(ledger.getAccountHead());
            }

            Transaction persistedCreditTransaction = transactionService.transactionEntry(creditTransaction);
            if (persistedCreditTransaction != null) {

                // For society balance update
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(loanOtherCharges.getAmount(), "CREDIT");

                String message = "Loan other charges for account no. " + loanOtherCharges.getLoanAccountNo() + " amount " + loanOtherCharges.getAmount() + " credited";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Transaction Module", user.getId());
            }
        }
    }

    private void repaymentTransactionDebitEntry(Repayment repayment, Loan providedLoan, User user, BigDecimal principalAmount) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {

            // For credit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            creditTransaction.setAccountHead(repayment.getCreditAccount());
            debitTransaction.setDebitAmount(principalAmount);
            debitTransaction.setRemark("Repayment amount against account no " + repayment.getLoanAccountNo() + " received");
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber("" + repayment.getLoanAccountNo());
            debitTransaction.setTransferType(repayment.getCreditType());
            debitTransaction.setAccountName(providedLoan.getMember().getName() + " (" + providedLoan.getMember().getMemberNumber() + ")");
            debitTransaction.setParticulars("Repayment");
            // Line to update the balance
            if (latestTransaction != null) {
                BigDecimal balanceAsOf = latestTransaction.getBalance();
                balanceAsOf = balanceAsOf.subtract(debitTransaction.getDebitAmount());
                debitTransaction.setBalance(balanceAsOf);
            }

            if (providedLoan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            } else if (providedLoan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                debitTransaction.setLedger(ledger);
                debitTransaction.setAccountHead(ledger.getAccountHead());
            }

            Transaction persistedCreditTransaction = transactionService.transactionEntry(debitTransaction);
            if (persistedCreditTransaction != null) {
                String message = "Loan repayment amount againts account no. " + repayment.getLoanAccountNo() + " amount " + repayment.getReceivedAmount() + " debited";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Transaction Module", user.getId());
            }
        }
    }

    private void repaymentTransactionCreditEntryForInterestAmount(Repayment repayment, Loan providedLoan, User user, BigDecimal interestAmount) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {

            // For credit transaction against the loan
            Transaction creditTransaction = new Transaction();
//            creditTransaction.setAccountHead(repayment.getCreditAccount());
            creditTransaction.setCreditAmount(interestAmount);
            creditTransaction.setRemark("Paid Interest amount against account no " + repayment.getLoanAccountNo() + " received");
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber("" + repayment.getLoanAccountNo());
            creditTransaction.setTransferType(repayment.getCreditType());
            creditTransaction.setAccountName(providedLoan.getMember().getName() + " (" + providedLoan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            if (latestTransaction != null) {
                BigDecimal balanceAsOf = latestTransaction.getBalance();
                balanceAsOf = balanceAsOf.add(creditTransaction.getCreditAmount());
                creditTransaction.setBalance(balanceAsOf);
            }

            Ledger ledger = accountHeadService.getLedgerByName("Loan Interest");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountHead(ledger.getAccountHead());

            Transaction persistedCreditTransaction = transactionService.transactionEntry(creditTransaction);
            if (persistedCreditTransaction != null) {
                String message = "Paid Loan interest amount againts account no. " + repayment.getLoanAccountNo() + " amount " + interestAmount + " credited";
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Transaction Module", user.getId());
            }
        }
    }

    // For credit disbursement transaction entry
    private void disbursementLoanTransactionEntry(Loan loan, LoanDisbursement loanDisbursement) {
        LoanTransaction loanTransaction = loanTransactionService.latestLoanTransaction(loan);
        if (loanDisbursement != null) {
            BigDecimal balanceAsOf;
            LoanTransaction transaction = new LoanTransaction();

            transaction.setLoan(loan);
            transaction.setCreditAmount(loanDisbursement.getDisbursedAmount());
            transaction.setTransactionBy(loanDisbursement.getDisbursedBy());
            transaction.setTransactionOn(DateUtil.getTodayDate());
            transaction.setTransactionType(TransactionType.CREDIT);

            if (loanTransaction != null) {
                balanceAsOf = loanTransaction.getBalance();
                balanceAsOf = balanceAsOf.add(loanDisbursement.getDisbursedAmount());
                transaction.setBalance(balanceAsOf);
            } else {
                balanceAsOf = new BigDecimal(0);
                balanceAsOf = balanceAsOf.add(loanDisbursement.getDisbursedAmount());
                transaction.setBalance(balanceAsOf);
            }

            if (loan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            }
            loanTransactionService.loanTransactionEntry(transaction);
        }
    }

    // For credit repayment transaction entry
    private void repaymentLoanTransactionEntry(Loan loan, Repayment repayment) {
        System.out.println("*********************** Loan Transaction entry");
        LoanTransaction loanTransaction = loanTransactionService.latestLoanTransaction(loan);

        if (loanTransaction != null && repayment != null) {
            System.out.println("*********************** Loan Transaction entry");
            LoanTransaction transaction = new LoanTransaction();
            transaction.setLoan(loan);
            transaction.setCreditAmount(repayment.getReceivedAmount());
            transaction.setTransactionBy(repayment.getRepaymentDoneBy());
            transaction.setTransactionOn(DateUtil.getTodayDate());
            transaction.setTransactionType(TransactionType.DEBIT);
            if (loanTransaction != null) {
                BigDecimal balanceAsOf = loanTransaction.getBalance();
                balanceAsOf = balanceAsOf.subtract(repayment.getReceivedAmount());
                transaction.setBalance(balanceAsOf);
            }

            if (loan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            }
            loanTransactionService.loanTransactionEntry(transaction);
        }
    }

    // For other payment transaction entry
    private void otherPaymentLoanTransactionEntry(Loan loan, LoanOtherCharges loanOtherCharges, User user) {
        LoanTransaction loanTransaction = loanTransactionService.latestLoanTransaction(loan);
        if (loanTransaction != null && loanOtherCharges != null) {

            LoanTransaction transaction = new LoanTransaction();
            transaction.setLoan(loan);
            transaction.setCreditAmount(loanOtherCharges.getAmount());
            transaction.setTransactionBy(user);
            transaction.setTransactionOn(DateUtil.getTodayDate());
            transaction.setTransactionType(TransactionType.CREDIT);

            if (loanTransaction != null) {
                BigDecimal balanceAsOf = loanTransaction.getBalance();
                balanceAsOf = balanceAsOf.add(loanOtherCharges.getAmount());
                transaction.setBalance(balanceAsOf);
            }

            if (loan.getLoanType() == LoanType.GOLD) {
                Ledger ledger = accountHeadService.getLedgerByName("Gold");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.LAD) {
                Ledger ledger = accountHeadService.getLedgerByName("Lad");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            } else if (loan.getLoanType() == LoanType.TERM) {
                Ledger ledger = accountHeadService.getLedgerByName("Term");
                if (ledger == null) {
                    throw new EntityNotFoundException("Account head or ledger not found");
                }
                transaction.setLedger(ledger);
            }
            loanTransactionService.loanTransactionEntry(transaction);
        }
    }

    @GetMapping("loan/deposite/{customerId}")
    public ResponseEntity<List<DepositDto>> getDepositsByCustomer(@PathVariable long customerId) {

        Optional<Customer> customerObj = customerService.getCustomer(customerId);
        if (!customerObj.isPresent()) {
            throw new EntityNotFoundException("No customer found");
        }
        Customer customer = customerObj.get();
        Member member = memberService.getMemberByCustomer(customer);

        List<FixedDeposit> fixedDeposits = fixedDepositService.getFixedDepositByMemberNumber(member.getMemberNumber());
        List<RecurringDeposit> recurringDeposits = recurringDepositService.getRecuringDepositByMemberNumber(member.getMemberNumber());
        List<PigmyDeposit> pigmyDeposits = pigmyDepositService.getPigmyDepositByMemberNumber(member.getMemberNumber());

        List<DepositDto> depositDtos = new ArrayList<>();

        if (fixedDeposits != null && fixedDeposits.size() > 0) {
            for (FixedDeposit fixedDeposit : fixedDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(fixedDeposit.getMember());
                depositDto.setMemberId(fixedDeposit.getMember().getId());
                depositDto.setDepositType(fixedDeposit.getDepositType());
                depositDto.setDepositId(fixedDeposit.getId());
                depositDto.setMemberNumber(member.getMemberNumber());
                depositDto.setApplicationNumber(fixedDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(fixedDeposit.getTransactionNumber());
                depositDto.setAccountNumber(fixedDeposit.getAccountNumber());
                depositDto.setDepositAmount(fixedDeposit.getDepositAmount());
                depositDto.setPeriodOfDeposit(fixedDeposit.getPeriodOfDeposit());
                depositDto.setRateOfInterest(fixedDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(fixedDeposit.getMaturityAmount());
                depositDto.setInterestAmount(fixedDeposit.getInterestAmount());
                depositDto.setMaturityDate(fixedDeposit.getMaturityDate());
                depositDto.setStatus(fixedDeposit.isStatus());
                depositDto.setWithDrawn(fixedDeposit.isWithDrawn());
                depositDto.setDepositsApproval(fixedDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(fixedDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(fixedDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(fixedDeposit.getDepositNomineeDetailsThree());
                depositDtos.add(depositDto);
            }
        }

        if (recurringDeposits != null && recurringDeposits.size() > 0) {
            for (RecurringDeposit recurringDeposit : recurringDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(recurringDeposit.getMember());
                depositDto.setMemberId(recurringDeposit.getMember().getId());
                depositDto.setDepositType(recurringDeposit.getDepositType());
                depositDto.setDepositId(recurringDeposit.getId());
                depositDto.setMemberNumber(member.getMemberNumber());
                depositDto.setApplicationNumber(recurringDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(recurringDeposit.getTransactionNumber());
                depositDto.setAccountNumber(recurringDeposit.getAccountNumber());
                depositDto.setDepositAmount(recurringDeposit.getDepositAmount());
                depositDto.setBalance(recurringDeposit.getBalance());
                depositDto.setRateOfInterest(recurringDeposit.getRateOfInterest());
                depositDto.setMaturityAmount(recurringDeposit.getMaturityAmount());
                depositDto.setInterestAmount(recurringDeposit.getInterestAmount());
                depositDto.setMaturityDate(recurringDeposit.getMaturityDate());
                depositDto.setStatus(recurringDeposit.isStatus());
                depositDto.setWithDrawn(recurringDeposit.isWithDrawn());
                depositDto.setDepositsApproval(recurringDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(recurringDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(recurringDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(recurringDeposit.getDepositNomineeDetailsThree());
                depositDto.setNumberOfInstallments(recurringDeposit.getNumberOfInstallments());

                depositDtos.add(depositDto);
            }
        }

        if (pigmyDeposits != null && pigmyDeposits.size() > 0) {
            for (PigmyDeposit pigmyDeposit : pigmyDeposits) {
                DepositDto depositDto = new DepositDto();
                depositDto.setMember(pigmyDeposit.getMember());
                depositDto.setMemberId(pigmyDeposit.getMember().getId());
                depositDto.setDepositType(pigmyDeposit.getDepositType());
                depositDto.setDepositId(pigmyDeposit.getId());
                depositDto.setMemberNumber(member.getMemberNumber());
                depositDto.setApplicationNumber(pigmyDeposit.getApplicationNumber());
                depositDto.setTransactionNumber(pigmyDeposit.getTransactionNumber());
                depositDto.setAccountNumber(pigmyDeposit.getAccountNumber());
                depositDto.setDepositAmount(pigmyDeposit.getDepositAmount());
                depositDto.setStatus(pigmyDeposit.isStatus());
                depositDto.setWithDrawn(pigmyDeposit.isWithDrawn());
                depositDto.setDepositsApproval(pigmyDeposit.getDepositsApproval());
                depositDto.setDepositNomineeDetails(pigmyDeposit.getDepositNomineeDetails());
                depositDto.setDepositNomineeDetailsTwo(pigmyDeposit.getDepositNomineeDetailsTwo());
                depositDto.setDepositNomineeDetailsThree(pigmyDeposit.getDepositNomineeDetailsThree());
                depositDto.setBalance(pigmyDeposit.getBalance());
                depositDto.setMaturityDate(pigmyDeposit.getMaturityDate());

                depositDtos.add(depositDto);
            }
        }
        if (CollectionUtils.isEmpty(depositDtos)) {
            throw new EntityNotFoundException("No deposits found");
        }
        return new ResponseEntity<>(depositDtos, HttpStatus.OK);
    }

    @GetMapping("loan/emi-over-due/{loanNumber}")
    public ResponseEntity<List<EmiOverdueDto>> getEmiOverdue(@PathVariable String loanNumber) {
        BigDecimal totalPenalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipleAmount = BigDecimal.ZERO;
        BigDecimal totalInterestAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        List<EmiOverdueDto> emiUnpaids = new ArrayList<>();
        EmiOverdueDto emiOverdueDto = new EmiOverdueDto();
        Optional<Loan> loanObj = loanService.findLoanByAccountNo(loanNumber);
        if (!loanObj.isPresent()) {
            throw new EntityNotFoundException("No Loan found");
        }
        Loan loan = loanObj.get();
        LoanSchedule loanSchedules = loan.getLoanSchedule();
        List<LoanInstallments> loanInstallments = null;
        if (loanSchedules != null) {
            loanInstallments = loanSchedules.getInstallments();
            for (LoanInstallments installments : loanInstallments) {
                if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && (installments.getDueDate().before(DateUtil.getTodayDate()) || installments.getDueDate().equals(DateUtil.getTodayDate()))) {
                    double overDueInterest = EmiCalculator.calculateEmiOverdueInterest(installments.getDueDate(), DateUtil.getTodayDate(), installments.getPrincipleAmount().doubleValue(), 4);
                    totalPrincipleAmount = totalPrincipleAmount.add(installments.getPrincipleAmount());
                    totalPenalInterest = totalPenalInterest.add(BigDecimal.valueOf(overDueInterest));
                    totalInterestAmount = totalInterestAmount.add(installments.getInterestAmount());
                } else if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && installments.getDueDate().after(DateUtil.getTodayDate())) {
                    EmiOverdueDto emiUnpaid = new EmiOverdueDto();
                    emiUnpaid.setTotalPrincipleAmount(installments.getPrincipleAmount());
                    emiUnpaid.setTotalPenalInterest(BigDecimal.ZERO);
                    emiUnpaid.setTotalInterestAmount(installments.getInterestAmount());
                    emiUnpaids.add(emiUnpaid);
                    totalPrincipleAmount = emiUnpaids.get(0).getTotalPrincipleAmount();
                    totalPenalInterest = emiUnpaids.get(0).getTotalPenalInterest();
                    totalInterestAmount = emiUnpaids.get(0).getTotalInterestAmount();
                    totalAmount = emiUnpaids.get(0).getEmiAmount();
                }
            }
            emiOverdueDto.setTotalPrincipleAmount(totalPrincipleAmount);
            emiOverdueDto.setTotalPenalInterest(totalPenalInterest);
            emiOverdueDto.setTotalInterestAmount(totalInterestAmount);
            emiOverdueDto.setLoanAccountNumber(loanNumber);
            totalAmount = totalPrincipleAmount.add(totalInterestAmount.add(totalPenalInterest));
            emiOverdueDto.setTotalAmount(totalAmount);
        }
        return new ResponseEntity(emiOverdueDto, HttpStatus.OK);
    }

    @GetMapping("loan/save-emi-over-due/{loanNumber}")
    public ResponseEntity<List<EmiOverdueDto>> saveEMIRepayment(@PathVariable String loanNumber) {

        BigDecimal totalPenalInterest = BigDecimal.ZERO;
        BigDecimal totalPrincipleAmount = BigDecimal.ZERO;
        BigDecimal totalInterestAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        EmiOverdueDto emiOverdueDto = new EmiOverdueDto();
        List<EmiOverdueDto> emiUnpaids = new ArrayList<>();

        Optional<Loan> loanObj = loanService.findLoanByAccountNo(loanNumber);
        if (!loanObj.isPresent()) {
            throw new EntityNotFoundException("No Loan found");
        }
        Loan loan = loanObj.get();
        LoanSchedule loanSchedules = loan.getLoanSchedule();
        List<LoanInstallments> loanInstallments = null;
        if (loanSchedules != null) {
            loanInstallments = loanSchedules.getInstallments();
            for (LoanInstallments installments : loanInstallments) {
                if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && (installments.getDueDate().before(DateUtil.getTodayDate()) || installments.getDueDate().equals(DateUtil.getTodayDate()))) {
                    double overDueInterest = EmiCalculator.calculateEmiOverdueInterest(installments.getDueDate(), DateUtil.getTodayDate(), installments.getPrincipleAmount().doubleValue(), 4);
                    totalPrincipleAmount = totalPrincipleAmount.add(installments.getPrincipleAmount());
                    totalPenalInterest = totalPenalInterest.add(BigDecimal.valueOf(overDueInterest));
                    totalInterestAmount = totalInterestAmount.add(installments.getInterestAmount());
                    totalAmount = totalPrincipleAmount.add(totalInterestAmount.add(totalPenalInterest));
                    installments.setPaidAmount(totalAmount);
                    installments.setPenalInterestAmount(totalInterestAmount);
                    installments.setLoanEmiStatus(LoanEmiStatus.PAID);
                } else if (installments.getLoanEmiStatus().toString().equalsIgnoreCase("UNPAID") && installments.getDueDate().after(DateUtil.getTodayDate())) {
                    EmiOverdueDto emiUnpaid = new EmiOverdueDto();
                    emiUnpaid.setTotalPrincipleAmount(installments.getPrincipleAmount());
                    emiUnpaid.setTotalPenalInterest(BigDecimal.ZERO);
                    emiUnpaid.setTotalInterestAmount(installments.getInterestAmount());
                    emiUnpaid.setLoanEmiStatus(LoanEmiStatus.PAID);
                    emiUnpaid.setId(installments.getId());
                    emiUnpaids.add(emiUnpaid);

                    totalPrincipleAmount = emiUnpaids.get(0).getTotalPrincipleAmount();
                    totalPenalInterest = emiUnpaids.get(0).getTotalPenalInterest();
                    totalInterestAmount = emiUnpaids.get(0).getTotalInterestAmount();
                    totalAmount = emiUnpaids.get(0).getEmiAmount();

                    if (emiUnpaids.get(0).getId() == installments.getId()) {
                        installments.setPaidAmount(emiUnpaids.get(0).getEmiAmount());
                        installments.setPenalInterestAmount(BigDecimal.ZERO);
                        installments.setLoanEmiStatus(LoanEmiStatus.PAID);
                        installments.setPaymentDate(DateUtil.getTodayDate());
                    }
                }
            }
            emiOverdueDto.setTotalPrincipleAmount(totalPrincipleAmount);
            emiOverdueDto.setTotalPenalInterest(totalPenalInterest);
            emiOverdueDto.setTotalInterestAmount(totalInterestAmount);
            emiOverdueDto.setLoanAccountNumber(loanNumber);

            totalAmount = totalPrincipleAmount.add(totalInterestAmount.add(totalPenalInterest));
            emiOverdueDto.setTotalAmount(totalAmount);
        }
        loanService.applyLean(loan);
        return new ResponseEntity(loan, HttpStatus.OK);
    }

    private void creditToCashAccount(Loan loan, Repayment repayment) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            debitTransaction.setCreditAmount(repayment.getReceivedAmount());
            debitTransaction.setRemark("Amount credited for repayment loan no. " + loan.getId());
            debitTransaction.setDebitAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(loan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("CREDIT");
            debitTransaction.setAccountNumber("" + loan.getLoanAccountNumber());
            debitTransaction.setTransferType(repayment.getCreditType());
            debitTransaction.setChequeNo(repayment.getChequeNo());
            debitTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(debitTransaction.getCreditAmount());
            debitTransaction.setBalance(balance);

            Ledger ledger = accountHeadService.getLedgerByName("Cash Account");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountHead(ledger.getAccountHead());

            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void debitFromCashAccount(Loan loan) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            debitTransaction.setDebitAmount(loan.getLoanDisbursement().getDisbursedAmount());
            debitTransaction.setRemark("Amount debited for disbursement loan no. " + loan.getLoanAccountNumber());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(loan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber("" + loan.getLoanAccountNumber());
            debitTransaction.setTransferType(loan.getLoanDisbursement().getCreditMode());
            debitTransaction.setChequeDate(loan.getLoanDisbursement().getChequeDate());
            debitTransaction.setChequeNo(loan.getLoanDisbursement().getChequeNo());
            debitTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);


            Ledger ledger = accountHeadService.getLedgerByName("Cash Account");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountHead(ledger.getAccountHead());

            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void creditToBankAccount(Loan loan, Repayment repayment) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction creditTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            creditTransaction.setCreditAmount(repayment.getReceivedAmount());
            creditTransaction.setRemark("Amount credited to bank account for repayment loan no. " + loan.getLoanAccountNumber());
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setTransactionBy(loan.getLoanDisbursement().getDisbursedBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setAccountNumber("" + loan.getLoanAccountNumber());
            creditTransaction.setTransferType(repayment.getCreditType());
            creditTransaction.setChequeNo(repayment.getChequeNo());
            creditTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);

            Ledger ledger = accountHeadService.getLedgerByName("Bank Accounts");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountHead(ledger.getAccountHead());

            transactionService.transactionEntry(creditTransaction);
        }
    }

    private void debitFromBankAccount(Loan loan) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            debitTransaction.setDebitAmount(loan.getLoanDisbursement().getDisbursedAmount());
            debitTransaction.setRemark("Amount debited for disbursement from bank account for loan no. " + loan.getLoanAccountNumber());
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(loan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber("" + loan.getLoanAccountNumber());
            debitTransaction.setTransferType(loan.getLoanDisbursement().getCreditMode());
            debitTransaction.setChequeDate(loan.getLoanDisbursement().getChequeDate());
            debitTransaction.setChequeNo(loan.getLoanDisbursement().getChequeNo());
            debitTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);

            Ledger ledger = accountHeadService.getLedgerByName("Bank Accounts");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountHead(ledger.getAccountHead());

            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void applicationFeeCreditEntry(Loan loan) {

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            // For debit transaction against the loan
            Transaction debitTransaction = new Transaction();
//            debitTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getDebitAccount());
            debitTransaction.setCreditAmount(loan.getLoanDetail().getApplicationFee());
            debitTransaction.setRemark("Application Fee amount credited for loan no. " + loan.getApplicationNumber());
            debitTransaction.setDebitAmount(new BigDecimal(0));
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("CREDIT");
            debitTransaction.setAccountNumber("" + loan.getApplicationNumber());
            debitTransaction.setTransferType("Cash");
            debitTransaction.setAccountName(loan.getMember().getName() + " (" + loan.getMember().getMemberNumber() + ")");

            // Line to update the balance
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(debitTransaction.getCreditAmount());
            debitTransaction.setBalance(balance);

            Ledger ledger = accountHeadService.getLedgerByName("Cash Account");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head or ledger not found");
            }
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountHead(ledger.getAccountHead());

            transactionService.transactionEntry(debitTransaction);
        }
    }

    private void accountDebitAccountTransaction(Loan disbursedLoan, SavingsBankDeposit savingsBankDeposit, BigDecimal paidAmount) {

        Ledger ledger;
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(paidAmount);
            debitTransaction.setRemark("Amount debited from account no. " + savingsBankDeposit.getAccountNumber() + " for loan repayment");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(disbursedLoan.getLoanDisbursement().getDisbursedBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setAccountNumber("" + disbursedLoan.getLoanAccountNumber());
            debitTransaction.setTransferType("Transfer");
            debitTransaction.setAccountName(disbursedLoan.getMember().getName() + " (" + disbursedLoan.getMember().getMemberNumber() + ")");

            if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
                ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
            } else {
                ledger = accountHeadService.getLedgerByName("Current Account");
            }
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountHead(ledger.getAccountHead());

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);
            Transaction persistedDebitTransaction = transactionService.transactionEntry(debitTransaction);

            if (persistedDebitTransaction != null) {
                SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

                if (savingBankTransaction != null) {

                    BigDecimal depositBalance = savingBankTransaction.getBalance();
                    depositBalance = depositBalance.subtract(paidAmount);
                    SavingBankTransaction bankTransaction = new SavingBankTransaction();
                    bankTransaction.setTransactionType("DEBIT");
                    bankTransaction.setDebitAmount(paidAmount);
                    bankTransaction.setBalance(depositBalance);
                    bankTransaction.setCreditAmount(BigDecimal.ZERO);
                    bankTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
                    bankTransaction.setSavingsBankDeposit(savingsBankDeposit);
                    savingsBankDeposit.setBalance(depositBalance);
                    savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
                    savingBankTransactionService.createSavingBankTransaction(bankTransaction);

                }
            }
        }
    }

    private void loanTransactionEntry(Loan loan, BigDecimal amount) {
        LoanTransaction loanTransaction = loanTransactionService.latestLoanTransaction(loan);
        if (loanTransaction != null && amount != null) {
            LoanTransaction transaction = new LoanTransaction();
            transaction.setLoan(loan);
            transaction.setCreditAmount(amount);
            transaction.setTransactionOn(DateUtil.getTodayDate());
            transaction.setTransactionType(TransactionType.DEBIT);
            if (loanTransaction != null) {
                BigDecimal balanceAsOf = loanTransaction.getBalance();
                balanceAsOf = balanceAsOf.subtract(amount);
                transaction.setBalance(balanceAsOf);
            }
            transaction.setLedger(loan.getLoanDisbursement().getLedger());
            transaction.setAccountHead(loan.getLoanDisbursement().getLedger().getAccountHead());

            loanTransactionService.loanTransactionEntry(transaction);
        }
    }
}