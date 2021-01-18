package com.dfq.coeffi.cbs.deposit.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLog;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.Dto.DepositApprovalDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositDto;
import com.dfq.coeffi.cbs.deposit.Dto.DepositNomineeDto;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.*;
import com.dfq.coeffi.cbs.deposit.service.DepositAccountNumberMasterService;
import com.dfq.coeffi.cbs.deposit.service.DepositInterestCalculationService;
import com.dfq.coeffi.cbs.deposit.service.DepositsApprovalService;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormat;
import com.dfq.coeffi.cbs.master.entity.accountformat.AccountFormatType;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.roi.fd.DepositRateOfInterest;
import com.dfq.coeffi.cbs.master.service.AccountFormatService;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.DepositRateOfInterestService;
import com.dfq.coeffi.cbs.member.entity.DividendIssue;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.NumberFormat;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;

@Slf4j
@RestController
public class SavingsBankDepositApi extends BaseController {

    private final SavingsBankDepositService savingsBankDepositService;
    private final DepositsApprovalService depositsApprovalService;
    private final MemberService memberService;
    private final ApplicationLogService applicationLogService;
    private final TransactionService transactionService;
    private final DepositAccountNumberMasterService depositAccountNumberMasterService;
    private final SavingBankTransactionService savingBankTransactionService;
    private final AccountHeadService accountHeadService;
    private final BODDateService bodDateService;
    public final BankService bankService;
    public final AccountFormatService accountFormatService;
    public final DepositRateOfInterestService depositRateOfInterestService;
    public final DepositInterestCalculationService depositInterestCalculationService;
    long defaultApplicationNumber = 201900000;

    private SavingsBankDepositApi(SavingsBankDepositService savingsBankDepositService, DepositsApprovalService depositsApprovalService,
                                  MemberService memberService, ApplicationLogService applicationLogService, final TransactionService transactionService,
                                  DepositAccountNumberMasterService depositAccountNumberMasterService,
                                  SavingBankTransactionService savingBankTransactionService,
                                  final AccountHeadService accountHeadService, BODDateService bodDateService,
                                  BankService bankService, final AccountFormatService accountFormatService,
                                  final DepositRateOfInterestService depositRateOfInterestService,
                                  final DepositInterestCalculationService depositInterestCalculationService) {
        this.savingsBankDepositService = savingsBankDepositService;
        this.depositsApprovalService = depositsApprovalService;
        this.memberService = memberService;
        this.applicationLogService = applicationLogService;
        this.transactionService = transactionService;
        this.depositAccountNumberMasterService = depositAccountNumberMasterService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.bankService = bankService;
        this.accountHeadService = accountHeadService;
        this.bodDateService = bodDateService;
        this.accountFormatService = accountFormatService;
        this.depositRateOfInterestService = depositRateOfInterestService;
        this.depositInterestCalculationService = depositInterestCalculationService;
    }

    @PostMapping("/saving-bank-deposit/unapprove-list")
    public ResponseEntity<List<SavingsBankDeposit>> getAllSavingsBankDeposits(@RequestBody DepositApprovalDto depositApprovalDto) {
        List<SavingsBankDeposit> savingsBankDepositList = savingsBankDepositService.getAllSavingsBankDeposit(depositApprovalDto.dateFrom, depositApprovalDto.dateTo);
        if (CollectionUtils.isEmpty(savingsBankDepositList)) {
            throw new EntityNotFoundException("No Saving Bank Accounts Found");
        }
        return new ResponseEntity<>(savingsBankDepositList, HttpStatus.OK);
    }

    @PostMapping("/saving-bank-deposit")
    public ResponseEntity<SavingsBankDeposit> createSavingsBankDeposit(@Valid @RequestBody final SavingsBankDeposit savingsBankDeposit, Principal principal) {
        bodDateService.checkBOD();
        SavingsBankDeposit persistSavingBankDeposit = null;

        NumberFormat numberFormat = memberService.getNumberFormatByType("Deposit_Receipt_Number");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need to add master data for auto generate receipt number");
        }
        String receiptNumber = numberFormat.getPrefix() + "-" + (numberFormat.getReceiptNumber() + 1);

        Optional<Member> memberObj = memberService.getMember(savingsBankDeposit.getMember().getId());
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", savingsBankDeposit.getMember().getId());
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();

        SavingsBankDeposit bankDepositForMember = savingsBankDepositService.getSavingsBankAccountsByMember(member);
        if (bankDepositForMember != null) {
            throw new EntityNotFoundException("Account already created for member : " + bankDepositForMember.getMember().getName() + " and Account Type : " + bankDepositForMember.getAccountType());
        }

        savingsBankDeposit.setMember(member);
        savingsBankDeposit.setBalance(savingsBankDeposit.getDepositAmount());
        Optional<DepositAccountNumberMaster> accountNumberMaster = depositAccountNumberMasterService.getDepositAccountNumberMasterById(1);
        savingsBankDeposit.setApplicationNumber(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberSB()) + defaultApplicationNumber));
        accountNumberMaster.get().setAccountNumberSB(String.valueOf(Integer.parseInt(accountNumberMaster.get().getAccountNumberSB()) + 1));
        depositAccountNumberMasterService.saveDepositAccountNumberMaster(accountNumberMaster.get());

        savingsBankDeposit.setReceiptNumber(receiptNumber);
        AccountFormat accountFormat = null;

        if (member.getOrganisationName().equalsIgnoreCase("Individual")) {
            accountFormat = accountFormatService.getAccountFormatByTypeAndSubType(AccountFormatType.DEPOSIT, "SAVING_BANK_DEPOSIT");
            if (accountFormat == null) {
                throw new EntityNotFoundException("Need to add master data for auto generate account number");
            }
            String accountNumber = accountFormat.getPrefix() + "-" + (accountFormat.getFromAccountNumber() + 1);
            savingsBankDeposit.setAccountNumber(accountNumber);

            Ledger ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
            if (ledger == null) {
                throw new EntityNotFoundException("Account head and ledger not found");
            }
            savingsBankDeposit.setAccountHead(ledger.getAccountHead());
            savingsBankDeposit.setLedger(ledger);
            savingsBankDeposit.setAccountType(AccountType.SAVING);

            persistSavingBankDeposit = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

        } else {
            throw new EntityNotFoundException("Saving bank account can not be created for the selected member");
        }

        if (persistSavingBankDeposit != null) {
            SavingBankTransaction savingBankTransaction = new SavingBankTransaction();
            savingBankTransaction.setCreditAmount(savingsBankDeposit.getDepositAmount());
            savingBankTransaction.setTransactionType("CREDIT");
            savingBankTransaction.setBalance(savingsBankDeposit.getDepositAmount());
            savingBankTransaction.setSavingsBankDeposit(persistSavingBankDeposit);
            savingBankTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            User user = getLoggedUser(principal);
            savingBankTransaction.setTransactionBy(user);
            SavingBankTransaction persistedDeposit = savingBankTransactionService.createSavingBankTransaction(savingBankTransaction);
            if (persistedDeposit != null) {
                User loggedU = getLoggedUser(principal);
                applicationLogService.recordApplicationLog(loggedU.getFirstName(), "SAVING BANK ACCOUNT CREATED", "SAVING BANK ACCOUNT CREATE POST METHOD", loggedU.getId());
                accountFormat.setFromAccountNumber(accountFormat.getFromAccountNumber() + 1);
                accountFormatService.saveAccountNumber(accountFormat);

                numberFormat.setReceiptNumber(numberFormat.getReceiptNumber() + 1);
                memberService.updateNumberFormat(numberFormat);
            }
        }
        return new ResponseEntity<>(persistSavingBankDeposit, HttpStatus.CREATED);
    }

    @PutMapping("/saving-bank-deposit/{id}")
    public ResponseEntity<SavingsBankDeposit> updateSavingsBankDeposit(@PathVariable long id, @Valid @RequestBody SavingsBankDeposit savingsBankDeposit, Principal principal) {
        Optional<SavingsBankDeposit> persistedSavingsBankDeposit = savingsBankDepositService.getSavingsBankDepositById(id);
        if (!persistedSavingsBankDeposit.isPresent()) {
            throw new EntityNotFoundException(FixedDeposit.class.getSimpleName());
        }
        savingsBankDeposit.setId(id);
        savingsBankDeposit.setCreatedOn(persistedSavingsBankDeposit.get().getCreatedOn());
        SavingsBankDeposit bankDeposit = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
        if (bankDeposit != null) {
            User loggedUser = getLoggedUser(principal);
            ApplicationLog applicationLog = applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "SAVING BANK ACCOUNT Modified", "SAVING BANK ACCOUNT MODIFIED POST METHOD", loggedUser.getId());
        }
        return new ResponseEntity<>(savingsBankDeposit, HttpStatus.OK);
    }

    @PostMapping("/SAVING_BANK_DEPOSIT/add-nominee/{id}")
    public ResponseEntity<SavingsBankDeposit> addNomineeDetails(@PathVariable long id, @Valid @RequestBody DepositNomineeDto depositNomineeDto) {
        Optional<SavingsBankDeposit> persistedDeposit = savingsBankDepositService.getSavingsBankDepositById(id);
        SavingsBankDeposit savingsBankDeposit = persistedDeposit.get();
        if (!persistedDeposit.isPresent()) {
            throw new EntityNotFoundException(SavingsBankDeposit.class.getSimpleName());
        }
        if (savingsBankDeposit.getDepositNomineeDetailsTwo() == null) {
            savingsBankDeposit.setDepositNomineeDetailsTwo(depositNomineeDto.getDepositNomineeDetailsTwo());
        } else if (savingsBankDeposit.getDepositNomineeDetailsThree() == null) {
            savingsBankDeposit.setDepositNomineeDetailsThree(depositNomineeDto.getDepositNomineeDetailsThree());
        }
        SavingsBankDeposit saveSavingsBankDeposit = savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);
        return new ResponseEntity<>(saveSavingsBankDeposit, HttpStatus.OK);
    }

    @PostMapping("/saving-bank-deposit/approval")
    public ResponseEntity<SavingsBankDeposit> savingsBankDepositApproval(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        DepositsApproval depositsApproval = saveDepositsApproval(principal);
        Optional<SavingsBankDeposit> persistedSavingsBankDeposit = null;
        for (long id : depositApprovalDto.ids) {
            persistedSavingsBankDeposit = savingsBankDepositService.getSavingsBankDepositById(id);
            if (!persistedSavingsBankDeposit.isPresent()) {
                throw new EntityNotFoundException(RecurringDeposit.class.getSimpleName());
            }
            persistedSavingsBankDeposit.get().setDepositsApproval(depositsApproval);
            persistedSavingsBankDeposit.get().setStatus(true);
            persistedSavingsBankDeposit.get().setApproved(true);
            persistedSavingsBankDeposit.get().setAccountStatus("ACTIVE");
            User loggedUser = getLoggedUser(principal);
            persistedSavingsBankDeposit.get().setTransactionBy(loggedUser);
            SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.saveSavingsBankDeposit(persistedSavingsBankDeposit.get());
            if (savingsBankDeposit != null) {
                transactionCreditEntry(savingsBankDeposit);
                TransactionUtil transactionUtil = new TransactionUtil(bankService);
                transactionUtil.getUpdateSocietyBalance(savingsBankDeposit.getDepositAmount(), "CREDIT");
                applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "SAVING BANK DEPOSIT ACCOUNT APPROVED", "SAVING BANK ACCOUNT APPROVAL POST METHOD", loggedUser.getId());
            }
        }
        return new ResponseEntity<>(persistedSavingsBankDeposit.get(), HttpStatus.OK);
    }

    public DepositsApproval saveDepositsApproval(Principal principal) {
        DepositsApproval depositsApproval = new DepositsApproval();
        depositsApproval.setApproved(true);
        depositsApproval.setApprovedBy(getLoggedUser(principal).getFirstName());
        depositsApprovalService.saveDepositsApproval(depositsApproval);
        return depositsApproval;
    }

    private void transactionCreditEntry(SavingsBankDeposit sbTransaction) {
        Ledger ledger = null;
        if (sbTransaction.getAccountType().equals(AccountType.SAVING)) {
            ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
        } else {
            ledger = accountHeadService.getLedgerByName("Current Account");
        }
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for SB Account " + sbTransaction.getId());
            creditTransaction.setCreditAmount(sbTransaction.getDepositAmount());
            creditTransaction.setTransactionBy(sbTransaction.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setTransferType("Cash");
            creditTransaction.setAccountName(sbTransaction.getMember().getName() + " (" + sbTransaction.getMember().getMemberNumber() + ")");
            creditTransaction.setAccountNumber(sbTransaction.getAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            String accHeadName = "DEPOSITS";
            AccountHead accountHead = accountHeadService.getAccountHeadByName(accHeadName, CREDIT);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            Transaction persistedDebitTransaction = transactionService.transactionEntry(creditTransaction);
        }
    }

    @GetMapping("/member-saving-bank-account/{memberId}")
    public ResponseEntity<List<SavingsBankDeposit>> getMemberSavingBankAccounts(@PathVariable("memberId") long memberId) {
        Optional<Member> memberObj = memberService.getMember(memberId);
        if (!memberObj.isPresent()) {
            throw new EntityNotFoundException("No such a member exists");
        }
        Member member = memberObj.get();
        List<SavingsBankDeposit> savingsBankAccounts = savingsBankDepositService.getAllSavingsBankAccountsByMember(member);
        if (CollectionUtils.isEmpty(savingsBankAccounts)) {
            throw new EntityNotFoundException("No Saving bank accounts for the provided member");
        }
        return new ResponseEntity<>(savingsBankAccounts, HttpStatus.OK);
    }

    @GetMapping("/saving-bank-account-member-number/{memberNumber}")
    public ResponseEntity<SavingsBankDeposit> getMemberSavingBankAccountsByMemberNumber(@PathVariable("memberNumber") String memberNumber) {
        SavingsBankDeposit savingsBankAccount = savingsBankDepositService.getSavingsBankAccountByMemberNumber(memberNumber);
        if (savingsBankAccount == null) {
            throw new EntityNotFoundException("Saving account not found for" + memberNumber);
        }
        return new ResponseEntity<>(savingsBankAccount, HttpStatus.OK);
    }

    @PostMapping("/saving-bank-deposit/deactivate")
    public ResponseEntity<SavingsBankDeposit> deleteSavingsBankDeposit(@RequestBody DepositApprovalDto depositApprovalDto, Principal principal) {
        SavingsBankDeposit sbAccount = savingsBankDepositService.getSavingsBankDepositByAccountNumber(depositApprovalDto.getAccountNumber());
        if (sbAccount == null) {
            throw new EntityNotFoundException("Account not found for the acc no. :" + sbAccount.getAccountNumber());
        }
        Member member = sbAccount.getMember();
        savingsBankDepositService.checkLoanForMember(member);
        savingsBankDepositService.checkDepositForMember(member);
        if (sbAccount.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new EntityNotFoundException("SB Account can not be closed !! Balance of SB is greaterthan 0 :" + sbAccount.getBalance());
        }
        sbAccount.setAccountStatus("CLOSED");
        sbAccount.setAccountClosedOn(new Date());
        SavingsBankDeposit persistedSavingBank = savingsBankDepositService.saveSavingsBankDeposit(sbAccount);
        if (persistedSavingBank != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Saving Bank Account De-activated, Account number" + persistedSavingBank.getAccountNumber(), "CHILDRENS DEPOSIT POSTED", loggedUser.getId());
        }
        return new ResponseEntity<>(sbAccount, HttpStatus.OK);
    }

    @GetMapping("/member-saving-bank-account-detail/{memberId}")
    public ResponseEntity<SavingsBankDeposit> getMemberSavingBankAccountByMember(@PathVariable("memberId") long memberId) {
        Optional<Member> memberObj = memberService.getMember(memberId);
        if (!memberObj.isPresent()) {
            throw new EntityNotFoundException("No such a member exists");
        }
        Member member = memberObj.get();
        SavingsBankDeposit savingsBankAccount = savingsBankDepositService.getAllSavingsBankAccountsByMemberId(member);
        return new ResponseEntity<>(savingsBankAccount, HttpStatus.OK);
    }

    @GetMapping("/saving-bank-account-daily-interest")
    public ResponseEntity<SavingsBankDeposit> getSBAccountDailyInterestAmount() {
        List<SavingsBankDeposit> savingsBankDepositList = savingsBankDepositService.getActiveSavingBankDeposit();
        if (CollectionUtils.isEmpty(savingsBankDepositList)) {
            throw new EntityNotFoundException("No Saving Bank Accounts Found");
        }
        for (SavingsBankDeposit savingsBankDeposit : savingsBankDepositList) {

            calculateRateOfInterest(savingsBankDeposit);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private BigDecimal calculateRateOfInterest(SavingsBankDeposit savingsBankDeposit) {

        DepositRateOfInterest depositRateOfInterest = depositRateOfInterestService.getRateOfInterestByDepositTypeAndStatus(DepositType.SAVING_BANK_DEPOSIT);

        BigDecimal principalAmount = savingsBankDeposit.getBalance();
        float value = depositRateOfInterest.getRegularRateOfInterest();
        BigDecimal rateOfInterest = new BigDecimal(Float.toString(value));
        rateOfInterest = rateOfInterest.divide(BigDecimal.valueOf(100));
        BigDecimal interestAmount;
        BigDecimal amount = principalAmount.multiply(rateOfInterest.add(BigDecimal.valueOf(1)));
        interestAmount = amount.subtract(principalAmount);

        BigDecimal day = new BigDecimal(365);
        BigDecimal interestAmountPerDay = interestAmount.divide(day, RoundingMode.HALF_UP);

        DepositInterestCalculation depositInterestCalculation = new DepositInterestCalculation();
        depositInterestCalculation.setAccountNumber(savingsBankDeposit.getAccountNumber());
        depositInterestCalculation.setDepositAmount(savingsBankDeposit.getDepositAmount());
        depositInterestCalculation.setDepositType(savingsBankDeposit.getDepositType().name());
        depositInterestCalculation.setInterestAmount(interestAmountPerDay);
        depositInterestCalculation.setInterestRate(depositRateOfInterest.getRegularRateOfInterest());

        DepositInterestCalculation persistedObject = depositInterestCalculationService.saveDepositInterestCalculation(depositInterestCalculation);

        return interestAmountPerDay;
    }

}