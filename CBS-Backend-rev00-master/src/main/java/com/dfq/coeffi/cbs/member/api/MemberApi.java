package com.dfq.coeffi.cbs.member.api;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.customer.service.CustomerService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.savingBankTransaction.SavingBankTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.AccountType;
import com.dfq.coeffi.cbs.deposit.entity.SavingsBankDeposit;
import com.dfq.coeffi.cbs.deposit.service.SavingsBankDepositService;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.document.FileStorageService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import com.dfq.coeffi.cbs.loan.service.LoanService;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.entity.financialyear.FinancialYear;
import com.dfq.coeffi.cbs.master.entity.organisation.Organisation;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.master.service.FinancialYearService;
import com.dfq.coeffi.cbs.master.service.OrganisationService;
import com.dfq.coeffi.cbs.member.dto.MemberDto;
import com.dfq.coeffi.cbs.member.entity.*;
import com.dfq.coeffi.cbs.member.service.MemberService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class MemberApi extends BaseController {

    private final MemberService memberService;
    private final CustomerService customerService;
    private final ApplicationLogService applicationLogService;
    private final FinancialYearService financialYearService;
    private final LoanService loanService;
    private final FileStorageService fileStorageService;
    private final BankService bankService;
    private final TransactionService transactionService;
    private final SavingsBankDepositService savingsBankDepositService;
    private final SavingBankTransactionService savingBankTransactionService;
    private final BODDateService bodDateService;
    private final AccountHeadService accountHeadService;
    private final OrganisationService organisationService;

    @Autowired
    private MemberApi(final MemberService memberService, final CustomerService customerService,
                      final ApplicationLogService applicationLogService, final FinancialYearService financialYearService,
                      final LoanService loanService, final FileStorageService fileStorageService,
                      final BankService bankService,
                      final TransactionService transactionService, final SavingsBankDepositService savingsBankDepositService,
                      final SavingBankTransactionService savingBankTransactionService, final BODDateService bodDateService,
                      final AccountHeadService accountHeadService, final OrganisationService organisationService) {
        this.memberService = memberService;
        this.customerService = customerService;
        this.applicationLogService = applicationLogService;
        this.financialYearService = financialYearService;
        this.loanService = loanService;
        this.fileStorageService = fileStorageService;
        this.bankService = bankService;
        this.transactionService = transactionService;
        this.savingsBankDepositService = savingsBankDepositService;
        this.savingBankTransactionService = savingBankTransactionService;
        this.bodDateService = bodDateService;
        this.accountHeadService = accountHeadService;
        this.organisationService = organisationService;
    }

    @GetMapping("/member")
    public ResponseEntity<List<Member>> getMembers() {
        List<Member> members = memberService.getApprovedMembers(MemberType.MEMBER);
        System.out.println("Member : " + members);
        if (CollectionUtils.isEmpty(members)) {
            log.warn("No active members found");
            throw new EntityNotFoundException("members");
        }
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/member/nominal-type")
    public ResponseEntity<List<Member>> getMembersByMemberType() {
        List<Member> members = memberService.getApprovedMembers(MemberType.NOMINAL);
        if (CollectionUtils.isEmpty(members)) {
            log.warn("No active members found");
            throw new EntityNotFoundException("members");
        }
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/member/all-member")
    public ResponseEntity<List<Member>> getAllMembers() {

        List<Member> memberList = new ArrayList<>();
        List<Member> nominalMember = memberService.getApprovedMembers(MemberType.NOMINAL);
        List<Member> members = memberService.getApprovedMembers(MemberType.MEMBER);

        memberList.addAll(nominalMember);
        memberList.addAll(members);

        if (CollectionUtils.isEmpty(members)) {
            log.warn("No active members found");
            throw new EntityNotFoundException("members");
        }
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

    @PostMapping("/member")
    public ResponseEntity<Member> createMember(@RequestBody Member member, Principal principal) {

        NumberFormat numberFormat = memberService.getNumberFormatByType("Member");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need master data for generate member number and application number");
        }

        if (member.getId() <= 0) {
            String applicationNumber = numberFormat.getPrefix() + "-" + (numberFormat.getApplicationNumber() + 1);
            String memberNumber = numberFormat.getPrefix() + "-" + (numberFormat.getMemberNumber() + 1);
            member.setApplicationNumber(applicationNumber);
            member.setMemberNumber(memberNumber);

            numberFormat.setApplicationNumber(numberFormat.getApplicationNumber() + 1);
            numberFormat.setMemberNumber(numberFormat.getMemberNumber() + 1);
            memberService.updateNumberFormat(numberFormat);
        } else {
            member.setApplicationNumber(member.getApplicationNumber());
            member.setMemberNumber(member.getMemberNumber());
        }

        Optional<Customer> customerObj = customerService.getCustomer(member.getCustomer().getId());
        if (!customerObj.isPresent()) {
            log.warn("No active members found");
            throw new EntityNotFoundException(Customer.class.getName());
        }
        Customer customer = customerObj.get();
        Member persistedMember = memberService.getMemberByCustomer(customer);

        if (member.getId() <= 0) {
            if (persistedMember != null) {
                throw new EntityNotFoundException("Member with this customer id already exists");
            }
        }
        customer.setName(member.getName());//edit memberName
        member.setCustomer(customer);

        List<MemberFamilyDetails> memberFamilyDetails = member.getMemberFamilyDetails();
        List<MemberNomineeDetails> memberNomineeDetails = member.getMemberNomineeDetails();

        if (memberFamilyDetails != null && memberFamilyDetails.size() > 0) {
            for (int i = 0; i < memberFamilyDetails.size(); i++) {
                memberFamilyDetails.get(i).setMember(member);
            }
        }
        if (memberNomineeDetails != null && memberNomineeDetails.size() > 0) {
            for (int i = 0; i < memberNomineeDetails.size(); i++) {
                memberNomineeDetails.get(i).setMember(member);
            }
        }

        if (member.getDocuments() != null) {
            List<Document> documents = new ArrayList<>();

            for (Document documentObj : member.getDocuments()) {
                Document document1 = fileStorageService.getDocument(documentObj.getId());

                if (document1 != null) {
                    documents.add(document1);
                }
            }
            member.setDocuments(documents);
        }

        member.setSharesValue((new BigDecimal(0)));
        member.setSharesApplied((new BigDecimal(0)));
        member.setMemberFamilyDetails(memberFamilyDetails);
        member.setMemberNomineeDetails(memberNomineeDetails);
        member.setStatus(true);
        member.setMembershipWithdrawStatus(false);

        Organisation organisation = organisationService.findOrganisationByCOde(member.getOrganisationCode());
        member.setOrganisationName(organisation.getName());

        if (member.getMemberType() == MemberType.MEMBER && member.getId() <= 0) {
            member.setApprovedStatus(false);
        } else if (member.getMemberType() == MemberType.MEMBER && member.getId() > 0) {
            member.setApprovedStatus(true);
        } else if (member.getMemberType() == MemberType.NOMINAL) {
            member.setApprovedStatus(true);
        }

        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getApplicationNumber() + " submitted",
                    "MEMBER APPLICATION SUBMIT", loggedUser.getId());
        }
        return new ResponseEntity(persistedObject, HttpStatus.OK);
    }

    @PostMapping("/member/approve-member")
    public ResponseEntity<Member> approveMember(@RequestBody MemberDto memberDto, Principal principal) {
        bodDateService.checkBOD();

        Member member = memberService.getMemberForApproval(memberDto.getMemberId());
        if (member == null) {
            log.warn("Unable to find member with ID : {} not found", member);
            throw new EntityNotFoundException(Member.class.getName());
        }
        member.setApprovedStatus(true);
        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getId() + " submitted",
                    "MEMBER APPROVED", loggedUser.getId());
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        Optional<Member> member = memberService.getMember(id);
        if (!member.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", id);
            throw new EntityNotFoundException(Member.class.getName());
        }
        return new ResponseEntity<>(member.get(), HttpStatus.OK);
    }

    @GetMapping("/member-by-member-number/{memberNumber}")
    public ResponseEntity<Member> getMemberByMemberNumber(@PathVariable String memberNumber) {
        Optional<Member> memberObj = memberService.findMemberByMemberNumber(memberNumber);

        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", memberNumber);
            throw new EntityNotFoundException(Member.class.getName());
        }
        return new ResponseEntity<>(memberObj.get(), HttpStatus.OK);
    }

    @DeleteMapping("/member/{id}")
    public ResponseEntity<Member> deleteMember(@PathVariable long id, Principal principal) {

        User loggedUser = getLoggedUser(principal);
        Optional<Member> memberObj = memberService.getMember(id);
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member : {} not found", id);
            throw new EntityNotFoundException(Member.class.getSimpleName());
        }
        Member member = memberObj.get();
        member.setStatus(false);
        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null && loggedUser != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Delete member", "Delete Member", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    @PostMapping("/member/member-fee/{memberId}")
    public ResponseEntity<Member> createMemberFee(@RequestBody MemberFee memberFee, @PathVariable long memberId, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();

        Optional<Member> memberObj = memberService.getMemberByApprovedStatus(memberId);
        if (!memberObj.isPresent()) {
            log.warn("No active member found");
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        Optional<Customer> customerObj = customerService.getCustomer(member.getCustomer().getId());
        if (!customerObj.isPresent()) {
            log.warn("No active customers found");
            throw new EntityNotFoundException(Customer.class.getName());
        }
        Customer customer = customerObj.get();
        memberFee.setCustomer(customer);

        memberFee.setApplicationNumber(member.getApplicationNumber());
        memberFee.setMemberName(member.getName());
        memberFee.setCustomer(member.getCustomer());
        memberFee.setMemberFeeStatus(true);

        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.add(memberFee.getAmount());
        bankMaster.setBalance(balanceAmount);
        bankService.saveBankMaster(bankMaster);

        List<MemberFee> memberFees = member.getMemberFees();
        if (memberFees != null) {
            memberFees.add(memberFee);
        } else {
            memberFees = new ArrayList<>();
            memberFees.add(memberFee);
        }

        member.setMemberFees(memberFees);
        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {
            memberFeeTransactionEntry(memberFee, loggedUser);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getId() + " submitted",
                    "MEMBER FEE SUBMIT", loggedUser.getId());
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @GetMapping("/member/member-fee")
    public ResponseEntity<List<MemberFee>> getMemberFees() {
        List<MemberFee> memberFees = memberService.memberFees();
        if (CollectionUtils.isEmpty(memberFees)) {
            throw new EntityNotFoundException("memberFees");
        }
        return new ResponseEntity<>(memberFees, HttpStatus.OK);
    }

    @GetMapping("/member/additional-share")
    public ResponseEntity<List<AdditionalShare>> getAdditionalShares() {
        List<AdditionalShare> additionalShares = memberService.additionalShares();
        if (CollectionUtils.isEmpty(additionalShares)) {
            throw new EntityNotFoundException("additionalShares");
        }
        return new ResponseEntity<>(additionalShares, HttpStatus.OK);
    }

    @GetMapping("/member/refund-share")
    public ResponseEntity<List<RefundShare>> getRefundShares() {
        List<RefundShare> refundShares = memberService.refundShares();
        if (CollectionUtils.isEmpty(refundShares)) {
            throw new EntityNotFoundException("refundShares");
        }
        return new ResponseEntity<>(refundShares, HttpStatus.OK);
    }

    @GetMapping("/member/unapproved-member")
    public ResponseEntity<List<Member>> getMemberForApprove() {
        List<Member> members = memberService.getUnApprovedMembers(MemberType.MEMBER);
        if (CollectionUtils.isEmpty(members)) {
            throw new EntityNotFoundException("members");
        }
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    @GetMapping("/member/dividend-issue/{id}")
    public ResponseEntity<DividendIssue> getDividendIssue(@PathVariable Long id) {
        DividendIssue dividendIssue = memberService.getDividendIssue(id);
        if (dividendIssue == null) {
            log.warn("Unable to find dividend issue with ID : {} not found", id);
            throw new EntityNotFoundException(DividendIssue.class.getName());
        }
        return new ResponseEntity<>(dividendIssue, HttpStatus.OK);
    }

    @GetMapping("/member/dividend-issue")
    public ResponseEntity<List<DividendIssue>> getDividendIssues() {
        List<DividendIssue> dividendIssues = memberService.getDividendIssues();
        return new ResponseEntity<>(dividendIssues, HttpStatus.OK);
    }

    @GetMapping("/member/current-year-dividend-issue")
    public ResponseEntity<List<DividendIssue>> getCurrentYearDividendIssues() {

        FinancialYear financialYear = financialYearService.getCurrentFinancialYear();

        List<DividendIssue> dividendIssues = memberService.getCurrentYearDividendIssues(financialYear);
        return new ResponseEntity<>(dividendIssues, HttpStatus.OK);
    }

    @PostMapping("/member/dividend-issue")
    public ResponseEntity<DividendIssue> issueMemberDividend(@RequestBody DividendIssue dividendIssue, Principal principal) {

        FinancialYear financialYear = financialYearService.getCurrentFinancialYear();
        DividendIssue persistedObject = null;
        List<Member> members = memberService.getApprovedMembers();

        if (members != null && members.size() > 0) {
            for (Member member : members) {
                if (member != null && member.getSharesValue() != null) {
                    DividendIssue issue = new DividendIssue();
                    issue.setDividendYear(dividendIssue.getDividendYear());
                    issue.setRateOfInterest(dividendIssue.getRateOfInterest());
                    issue.setCustomer(member.getCustomer());
                    issue.setMember(member);
                    issue.setDividendAmount(calculateRateOfInterest(member, dividendIssue));
                    issue.setSummary(dividendIssue.getSummary());
                    issue.setFinancialYear(financialYear);
                    issue.setDividendYear(financialYear.getRunningYear());

                    persistedObject = memberService.saveDividendIssue(issue);

                    if (persistedObject != null) {
                        User loggedUser = getLoggedUser(principal);
                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getId() + " submitted",
                                "DIVIDEND ISSUE", loggedUser.getId());
                    }
                }
            }
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    private BigDecimal calculateRateOfInterest(Member member, DividendIssue dividendIssue) {

        BigDecimal shareAmountP = member.getSharesValue();
        BigDecimal rateOfInterestR = dividendIssue.getRateOfInterest();
        rateOfInterestR = rateOfInterestR.divide(BigDecimal.valueOf(100));
        BigDecimal dividendAmount = dividendIssue.getDividendAmount();
        BigDecimal interestAmount = shareAmountP.multiply(rateOfInterestR.add(BigDecimal.valueOf(1)));
        dividendAmount = interestAmount.subtract(shareAmountP);
        return dividendAmount;
    }

    @GetMapping("/member/dividend-payment/{id}")
    public ResponseEntity<DividendPayment> getDividendPayment(@PathVariable Long id) {
        DividendPayment dividendPayment = memberService.getDividendPayment(id);
        if (dividendPayment == null) {
            log.warn("Unable to find dividend payment with ID : {} not found", id);
            throw new EntityNotFoundException(DividendPayment.class.getName());
        }
        return new ResponseEntity<>(dividendPayment, HttpStatus.OK);
    }

    @GetMapping("/member/dividend-payment")
    public ResponseEntity<List<DividendPayment>> getDividendPayments() {
        List<DividendPayment> dividendPayments = memberService.getDividendPayments();

        return new ResponseEntity<>(dividendPayments, HttpStatus.OK);
    }

    @PostMapping("/member/dividend-payment")
    public ResponseEntity<DividendPayment> paymentMemberDividend(@RequestBody List<DividendPayment> dividendPaymentList, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);

        Ledger ledger = accountHeadService.getLedgerByName("Share Capital");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();

        DividendPayment persistedObject = null;
        FinancialYear financialYear = financialYearService.getCurrentFinancialYear();
        for (DividendPayment dividendPayment : dividendPaymentList) {

            if (dividendPayment.getModeOfPayment().equalsIgnoreCase("Cash")) {
                dividendPaymentDebitTransactionEntry(dividendPayment, bankMaster, loggedUser);
            } else if (dividendPayment.getModeOfPayment().equalsIgnoreCase("Bank")) {
                dividendPaymentDebitTransactionEntry(dividendPayment, bankMaster, loggedUser);
            } else if (dividendPayment.getModeOfPayment().equalsIgnoreCase("Transfer")) {
                dividendPaymentDebitTransactionEntry(dividendPayment, bankMaster, loggedUser);
                dividendPaymentCreditTransactionEntry(dividendPayment, bankMaster, loggedUser);
            }

            dividendPayment.setFinancialYear(financialYear);
            dividendPayment.setLedger(ledger);
            dividendPayment.setAccountHead(ledger.getAccountHead());
            persistedObject = memberService.saveDividendPayment(dividendPayment);
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    // DIVIDEND PAYMENT CREDIT TRANSACTION
    private void dividendPaymentCreditTransactionEntry(DividendPayment dividendPayment, BankMaster bankMaster, User user) {

        SavingsBankDeposit savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(dividendPayment.getMemberNumber());

        if (savingsBankDeposit == null) {
            throw new EntityNotFoundException("Saving Bank Account Not Found ");
        }

        Ledger ledger = null;
        if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
            ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
        } else {
            ledger = accountHeadService.getLedgerByName("Current Account");
        }
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

        if (savingBankTransaction != null) {

            BigDecimal depositBalance = savingBankTransaction.getBalance();
            depositBalance = depositBalance.add(dividendPayment.getDividendAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(dividendPayment.getDividendAmount());
            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);

            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if (savingBankTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credited to saving bank Acc No." + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
            BigDecimal bankBalance = bankMaster.getBalance();
            bankBalance = bankBalance.add(dividendPayment.getDividendAmount());
            bankMaster.setBalance(bankBalance);
            bankService.saveBankMaster(bankMaster);
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the dividend payment
            Transaction creditTransaction = new Transaction();

            creditTransaction.setCreditAmount(dividendPayment.getDividendAmount());
            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + savingBankTransaction.getAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(savingBankTransaction.getAccountNumber());
            creditTransaction.setTransferType(dividendPayment.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            creditTransaction.setAccountName(dividendPayment.getMember().getName() + " (" + dividendPayment.getMember().getMemberNumber() + ")");

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(dividendPayment.getDividendAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + savingBankTransaction.getAccountNumber() + "From : " + dividendPayment.getMemberName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Dividend payment Module", user.getId());
            }
            if (creditTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + savingBankTransaction.getAccountNumber() + " submitted",
                        "DIVIDEND PAYMENT", user.getId());
            }
        }
    }

    // DIVIDEND PAYMENT DEBIT TRANSACTION
    private void dividendPaymentDebitTransactionEntry(DividendPayment dividendPayment, BankMaster bankMaster, User user) {

        //Check society balance
        TransactionValidation.checkSocietyBalance(dividendPayment.getDividendAmount(), bankMaster.getBalance());

        Ledger ledger = accountHeadService.getLedgerByName("Share Capital");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        BigDecimal bankBalance = bankMaster.getBalance();
        bankBalance = bankBalance.subtract(dividendPayment.getDividendAmount());
        bankMaster.setBalance(bankBalance);

        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the dividend payment
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(dividendPayment.getDividendAmount());
            debitTransaction.setRemark("Amount debited from society Acc No. : " + bankMaster.getAccountNumber());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(bankMaster.getAccountNumber());
            debitTransaction.setTransferType(dividendPayment.getModeOfPayment());
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountName(dividendPayment.getMember().getName() + " (" + dividendPayment.getMember().getMemberNumber() + ")");

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(dividendPayment.getDividendAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount debited from society  Acc No. : " + bankMaster.getAccountNumber() + "From : " + dividendPayment.getMemberName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Dividend Payment Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from society bank Acc No. " + bankMaster.getAccountNumber() + " submitted",
                        "DIVIDEND PAYMENT", user.getId());
            }
        }
    }

    @GetMapping("/member/share-master")
    public ResponseEntity<List<ShareMaster>> getShareMasters() {
        List<ShareMaster> shareMasters = memberService.getShareMasters();
        if (CollectionUtils.isEmpty(shareMasters)) {
            log.warn("No active share masters found");
            throw new EntityNotFoundException("shareMasters");
        }
        return new ResponseEntity<>(shareMasters, HttpStatus.OK);
    }

    @GetMapping("/member/share-master/{id}")
    public ResponseEntity<ShareMaster> getShareMaster(@PathVariable long id) {
        ShareMaster shareMaster = memberService.getShareMaster(id);
        if (shareMaster == null) {
            log.warn("Unable to find share master with ID : {} not found", id);
            throw new EntityNotFoundException(ShareMaster.class.getName());
        }
        return new ResponseEntity<>(shareMaster, HttpStatus.OK);
    }

    @PostMapping("/member/share-master")
    public ResponseEntity<ShareMaster> createShareMaster(@Valid @RequestBody ShareMaster shareMaster) {
        ShareMaster persistedObject = memberService.saveShareMaster(shareMaster);
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @PostMapping("/member/close-member-ship")
    public ResponseEntity<Member> memberShipWithdraw(@RequestBody MemberDto memberDto, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);
        Member persistedObject = null;
        Optional<Member> memberObj = memberService.getMember(memberDto.getMemberId());
        if (!memberObj.isPresent()) {
            throw new EntityNotFoundException("Member Not Found");
        }
        Member member = memberObj.get();
        checkLoans(member);
        member.setMembershipWithdrawStatus(true);
        member.setStatus(false);
        persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getId() + " submitted",
                    "MEMBERSHIP WITHDRAW", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.OK);
    }

    // MEMBER FEE TRANSACTION
    private void memberFeeTransactionEntry(MemberFee memberFee, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("Share Capital");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        Transaction latestTransaction = transactionService.latestTransaction();

        BigDecimal balance;
        if (latestTransaction == null) {
            balance = new BigDecimal(0);
            balance = balance.add(memberFee.getAmount());

        } else {
            balance = latestTransaction.getBalance();
            balance = balance.add(memberFee.getAmount());
        }

        // For credit transaction against the bank
        Transaction creditTransaction = new Transaction();

        creditTransaction.setBalance(balance);
        creditTransaction.setCreditAmount(memberFee.getAmount());
        creditTransaction.setRemark("Amount credited to society main Acc No. : " + memberFee.getAccountNumber());
        creditTransaction.setTransactionBy(user);
        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditTransaction.setTransactionType("CREDIT");
        creditTransaction.setDebitAmount(new BigDecimal(0));
        creditTransaction.setAccountNumber(memberFee.getAccountNumber());
        creditTransaction.setTransferType(memberFee.getModeOfPayment());
        creditTransaction.setAccountHead(ledger.getAccountHead());
        creditTransaction.setLedger(ledger);

        Transaction transaction = transactionService.transactionEntry(creditTransaction);

        if (transaction != null) {
            String message = "" + transaction.getCreditAmount() + " Amount credited to society main Acc No. : " + memberFee.getAccountNumber() + "From " + memberFee.getMemberName();
            applicationLogService.recordApplicationLog(user.getFirstName(), message,
                    "Member Fee Module", user.getId());
        }
    }

    @PostMapping("/member/add-shares/{memberId}")
    public ResponseEntity<Member> addSharesToMember(@RequestBody AdditionalShare additionalShare, @PathVariable long memberId, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);
        SavingsBankDeposit savingsBankDeposit = null;

        if (additionalShare.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(additionalShare.getMemberNumber());

            if (savingsBankDeposit == null) {
                throw new EntityNotFoundException("Bank Account Not Found ");
            }
        }

        NumberFormat numberFormat = memberService.getNumberFormatByType("Share_Certificate");
        if (numberFormat == null) {
            throw new EntityNotFoundException("Need master data for generate member number and application number");
        }
        String shareCertificateNumber = ("00") + (numberFormat.getShareCertificateNumber() + 1);
        additionalShare.setShareCertificateNumber(shareCertificateNumber);
        additionalShare.setTotalAmount(String.valueOf(additionalShare.getShareValue()));

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            log.warn("No active bank found");
            throw new EntityNotFoundException(BankMaster.class.getName());
        }
        BankMaster bankMaster = bankMasterObj.get();

        if (additionalShare.getModeOfPayment().equalsIgnoreCase("Cash")) {
            additionalShareCreditTransactionEntry(additionalShare, bankMaster, loggedUser);
        } else if (additionalShare.getModeOfPayment().equalsIgnoreCase("Bank")) {
            additionalShareCreditTransactionEntry(additionalShare, bankMaster, loggedUser);
        } else if (additionalShare.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            addShareDebitTransactionEntry(additionalShare, bankMaster, savingsBankDeposit, loggedUser);
            additionalShareCreditTransactionEntry(additionalShare, bankMaster, loggedUser);
        }

        Optional<Member> memberObj = memberService.getMemberByApprovedStatus(memberId);
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", memberId);
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();
        Optional<Customer> customerObj = customerService.getCustomer(member.getCustomer().getId());
        if (!customerObj.isPresent()) {
            log.warn("Unable to find customer with ID : {} not found", customerObj);
            throw new EntityNotFoundException(Customer.class.getName());
        }
        Customer customer = customerObj.get();
        additionalShare.setCustomer(customer);

        List<AdditionalShare> additionalShares = member.getAdditionalShares();
        if (additionalShares != null) {
            additionalShares.add(additionalShare);
        } else {
            additionalShares = new ArrayList<>();
            additionalShares.add(additionalShare);
        }

        BigDecimal shareApplied = member.getSharesApplied();
        shareApplied = shareApplied.add(additionalShare.getSharesApplied());
        member.setSharesApplied(shareApplied);

        BigDecimal shareValue = member.getSharesValue();
        shareValue = shareValue.add(additionalShare.getShareValue());
        member.setSharesValue(shareValue);

        member.setAdditionalShares(additionalShares);
        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {

            numberFormat.setShareCertificateNumber(numberFormat.getShareCertificateNumber() + 1);

            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getId() + " submitted",
                    "ADDITIONAL SHARE SUBMIT", loggedUser.getId());
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    // ADDITIONAL SHARE DEBIT TRANSACTION
    private void addShareDebitTransactionEntry(AdditionalShare additionalShare, BankMaster bankMaster, SavingsBankDeposit savingsBankDeposit, User user) {

        Ledger ledger = null;

        if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
            ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");

        } else if (savingsBankDeposit.getAccountType().equals(AccountType.CURRENT)) {
            ledger = accountHeadService.getLedgerByName("Current Account");

        }
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        //Saving bank transaction
        SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

        BigDecimal shareFee = additionalShare.getShareFee();
        BigDecimal entranceFee = additionalShare.getEntranceFee();
        BigDecimal otherFee = additionalShare.getOtherFee();
        BigDecimal amount = additionalShare.getAmount();

        BigDecimal additionalShareAmount = ((shareFee.add(amount)).add(entranceFee)).add(otherFee);

        if (savingBankTransaction != null) {

            //Check society balance
            TransactionValidation.checkSocietyBalance(additionalShareAmount, bankMaster.getBalance());

            BigDecimal depositBalance = savingBankTransaction.getBalance();

            depositBalance = depositBalance.subtract(additionalShareAmount);

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("DEBIT");
            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(additionalShareAmount);
            sbTransaction.setCreditAmount(new BigDecimal(0));
            sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);
            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            BigDecimal balanceAmount = bankMaster.getBalance();
            balanceAmount = balanceAmount.subtract(additionalShareAmount);
            bankMaster.setBalance(balanceAmount);

            bankService.saveBankMaster(bankMaster);

            if (savingBankTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from saving bank Acc No." + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(additionalShareAmount);
            debitTransaction.setRemark("Amount debited from member SB Acc No. : " + savingsBankDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(savingsBankDeposit.getAccountNumber());
            debitTransaction.setTransferType(additionalShare.getModeOfPayment());
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(additionalShareAmount);
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount debited from saving bank Acc No. : " + savingsBankDeposit.getAccountNumber();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Additional Share Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from society bank Acc No. " + debitTransaction.getId() + " submitted",
                        "REFUNDED SHARE", user.getId());
            }
        }
    }

    // ADDITIONAL SHARE CREDIT TRANSACTION
    private void additionalShareCreditTransactionEntry(AdditionalShare additionalShare, BankMaster bankMaster, User user) {

        Ledger ledger = accountHeadService.getLedgerByName("Share Capital");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        BigDecimal shareFee = additionalShare.getShareFee();
        BigDecimal entranceFee = additionalShare.getEntranceFee();
        BigDecimal otherFee = additionalShare.getOtherFee();
        BigDecimal amount = additionalShare.getAmount();

//        BigDecimal additionalShareAmount = ((shareFee.add(amount)).add(entranceFee)).add(otherFee);
        BigDecimal additionalShareAmount = additionalShare.getAmount();
        BigDecimal totalOtherFees = (shareFee.add(entranceFee)).add(otherFee);

        BigDecimal balanceAmount = bankMaster.getBalance();
        balanceAmount = balanceAmount.add(additionalShareAmount);
        bankMaster.setBalance(balanceAmount);

        BigDecimal shareBalanceAmount = bankMaster.getShareBalance();
        shareBalanceAmount = shareBalanceAmount.add(additionalShareAmount);
        bankMaster.setShareBalance(shareBalanceAmount);

        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        BigDecimal balance;
        if (latestTransaction == null) {
            balance = new BigDecimal(0);
            balance = balance.add(additionalShareAmount);

        } else {
            balance = latestTransaction.getBalance();
            balance = balance.add(additionalShareAmount);
        }

        // For credit transaction against the bank
        Transaction creditTransaction = new Transaction();

        creditTransaction.setBalance(balance);
        creditTransaction.setCreditAmount(additionalShareAmount);
        creditTransaction.setRemark("Amount credited to society share  Acc No. : " + additionalShare.getAccountNumber());
        creditTransaction.setTransactionBy(user);
        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditTransaction.setTransactionType("CREDIT");
        creditTransaction.setDebitAmount(new BigDecimal(0));
        creditTransaction.setAccountNumber(additionalShare.getAccountNumber());
        creditTransaction.setTransferType(additionalShare.getModeOfPayment());
        creditTransaction.setAccountHead(ledger.getAccountHead());
        creditTransaction.setLedger(ledger);
        creditTransaction.setOtherFees(totalOtherFees);

        if (additionalShare.getMemberName() != null && additionalShare.getMemberNumber() != null) {
            creditTransaction.setAccountName(additionalShare.getMemberName() + " (" + additionalShare.getMemberNumber() + ")");
        }

        Transaction transaction = transactionService.transactionEntry(creditTransaction);

        if (transaction != null) {
            String message = "" + transaction.getCreditAmount() + " Amount credited to society share Acc No. : " + additionalShare.getAccountNumber();
            applicationLogService.recordApplicationLog(user.getFirstName(), message,
                    "Additional Share Module", user.getId());
        }

        // For credit share fee
        Ledger shareFeeLedger = accountHeadService.getLedgerByName("Share Fees");
        Transaction shareFeeTransaction = new Transaction();
        BigDecimal balanceFee = transaction.getBalance();
        balanceFee = balanceFee.add(shareFee);
        shareFeeTransaction.setBalance(transaction.getBalance());
        shareFeeTransaction.setCreditAmount(shareFee);
        shareFeeTransaction.setRemark("Share fee. : " + additionalShare.getAccountNumber());
        shareFeeTransaction.setTransactionBy(user);
        shareFeeTransaction.setTransactionOn(DateUtil.getTodayDate());
        shareFeeTransaction.setTransactionType("CREDIT");
        shareFeeTransaction.setDebitAmount(new BigDecimal(0));
        shareFeeTransaction.setAccountNumber(bankMaster.getShareAccountNumber());
        shareFeeTransaction.setTransferType("PROFIT");
        shareFeeTransaction.setAccountHead(shareFeeLedger.getAccountHead());
        shareFeeTransaction.setLedger(shareFeeLedger);
        shareFeeTransaction.setOtherFees(new BigDecimal(BigInteger.ZERO));


        if (additionalShare.getMemberName() != null && additionalShare.getMemberNumber() != null) {
            creditTransaction.setAccountName(additionalShare.getMemberName() + " (" + additionalShare.getMemberNumber() + ")");
        }

        Transaction shareFeePersistedTransaction = transactionService.transactionEntry(shareFeeTransaction);

        // For credit entrance Fee
        Ledger entranceFeeLedger = accountHeadService.getLedgerByName("Entry Fees");
        Transaction entranceFeeTransaction = new Transaction();
        BigDecimal balanceEnterenceFee = shareFeePersistedTransaction.getBalance();
        balanceEnterenceFee = balanceEnterenceFee.add(entranceFee);
        entranceFeeTransaction.setBalance(transaction.getBalance());
        entranceFeeTransaction.setCreditAmount(entranceFee);
        entranceFeeTransaction.setRemark("Entrance fee. : " + additionalShare.getAccountNumber());
        entranceFeeTransaction.setTransactionBy(user);
        entranceFeeTransaction.setTransactionOn(DateUtil.getTodayDate());
        entranceFeeTransaction.setTransactionType("CREDIT");
        entranceFeeTransaction.setDebitAmount(new BigDecimal(0));
        entranceFeeTransaction.setAccountNumber(bankMaster.getShareAccountNumber());
        entranceFeeTransaction.setTransferType("PROFIT");
        entranceFeeTransaction.setAccountHead(entranceFeeLedger.getAccountHead());
        entranceFeeTransaction.setLedger(entranceFeeLedger);
        entranceFeeTransaction.setOtherFees(new BigDecimal(BigInteger.ZERO));


        if (additionalShare.getMemberName() != null && additionalShare.getMemberNumber() != null) {
            creditTransaction.setAccountName(additionalShare.getMemberName() + " (" + additionalShare.getMemberNumber() + ")");
        }

        Transaction entranceFeePersistedTransaction = transactionService.transactionEntry(entranceFeeTransaction);

// For credit Other Fee
        Ledger shareOtherFeeLedger = accountHeadService.getLedgerByName("Other Fees");
        Transaction shareOtherFeeTransaction = new Transaction();
        BigDecimal balanceShareOtherFee = entranceFeePersistedTransaction.getBalance();
        balanceShareOtherFee = balanceShareOtherFee.add(otherFee);
        shareOtherFeeTransaction.setBalance(transaction.getBalance());
        shareOtherFeeTransaction.setCreditAmount(otherFee);
        shareOtherFeeTransaction.setRemark("Other fee. : " + additionalShare.getAccountNumber());
        shareOtherFeeTransaction.setTransactionBy(user);
        shareOtherFeeTransaction.setTransactionOn(DateUtil.getTodayDate());
        shareOtherFeeTransaction.setTransactionType("CREDIT");
        shareOtherFeeTransaction.setDebitAmount(new BigDecimal(0));
        shareOtherFeeTransaction.setAccountNumber(bankMaster.getShareAccountNumber());
        shareOtherFeeTransaction.setTransferType("PROFIT");
        shareOtherFeeTransaction.setAccountHead(shareOtherFeeLedger.getAccountHead());
        shareOtherFeeTransaction.setLedger(shareOtherFeeLedger);
        shareOtherFeeTransaction.setOtherFees(new BigDecimal(BigInteger.ZERO));


        if (additionalShare.getMemberName() != null && additionalShare.getMemberNumber() != null) {
            creditTransaction.setAccountName(additionalShare.getMemberName() + " (" + additionalShare.getMemberNumber() + ")");
        }

        Transaction otherFeePersistedTransaction = transactionService.transactionEntry(shareOtherFeeTransaction);


    }

    @PostMapping("/member/refund-share/{memberId}")
    public ResponseEntity<Member> refundShare(@RequestBody RefundShare refundShare, @PathVariable long memberId, Principal principal) {

        bodDateService.checkBOD();
        User loggedUser = getLoggedUser(principal);
        SavingsBankDeposit savingsBankDeposit = null;

        if (refundShare.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            savingsBankDeposit = savingsBankDepositService.getSavingsBankAccountByMemberNumber(refundShare.getMemberNumber());

            if (savingsBankDeposit == null) {
                throw new EntityNotFoundException("Saving Bank Account Not Found ");
            }
        }

        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if (!bankMasterObj.isPresent()) {
            throw new EntityNotFoundException("No active bank found");
        }
        BankMaster bankMaster = bankMasterObj.get();

        if (refundShare.getModeOfPayment().equalsIgnoreCase("Cash")) {
            refundShareDebitTransactionEntry(refundShare, bankMaster, loggedUser);
        } else if (refundShare.getModeOfPayment().equalsIgnoreCase("Bank")) {
            refundShareDebitTransactionEntry(refundShare, bankMaster, loggedUser);
        } else if (refundShare.getModeOfPayment().equalsIgnoreCase("Transfer")) {
            refundShareDebitTransactionEntry(refundShare, bankMaster, loggedUser);
            refundShareCreditTransactionEntry(refundShare, bankMaster, savingsBankDeposit, loggedUser);
        }

        Optional<Member> memberObj = memberService.getMemberByApprovedStatus(memberId);
        if (!memberObj.isPresent()) {
            log.warn("Unable to find member with ID : {} not found", memberId);
            throw new EntityNotFoundException(Member.class.getName());
        }
        Member member = memberObj.get();

        Optional<Customer> customerObj = customerService.getCustomer(member.getCustomer().getId());
        if (!customerObj.isPresent()) {
            log.warn("Unable to find customer with ID : {} not found", customerObj);
            throw new EntityNotFoundException(Customer.class.getName());
        }
        Customer customer = customerObj.get();
        refundShare.setCustomer(customer);

        List<RefundShare> refundShares = member.getRefundShares();

        if (refundShares != null) {
            refundShares.add(refundShare);
        } else {
            refundShares = new ArrayList<>();
            refundShares.add(refundShare);
        }

        BigDecimal shareApplied = member.getSharesApplied();
        shareApplied = shareApplied.subtract(refundShare.getAppliedNumberOfShares());
        member.setSharesApplied(shareApplied);

        BigDecimal shareValue = member.getSharesValue();
        shareValue = shareValue.subtract(refundShare.getAppliedRefundSharesAmount());
        member.setSharesValue(shareValue);
        member.setRefundShares(refundShares);

        Member persistedObject = memberService.saveMember(member);

        if (persistedObject != null) {
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Member application no. " + persistedObject.getApplicationNumber() + " refunded share",
                    "REFUNDED SHARE", loggedUser.getId());
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }


    // REFUND SHARE DEBIT TRANSACTION
    private void refundShareDebitTransactionEntry(RefundShare refundShare, BankMaster bankMaster, User user) {

        //Check society balance
        TransactionValidation.checkSocietyBalance(refundShare.getAppliedRefundSharesAmount(), bankMaster.getBalance());

        Ledger ledger = accountHeadService.getLedgerByName("Share Capital");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }
        BigDecimal bankBalance = bankMaster.getBalance();
        bankBalance = bankBalance.subtract(refundShare.getAppliedRefundSharesAmount());
        bankMaster.setBalance(bankBalance);

        BigDecimal shareBankBalance = bankMaster.getShareBalance();
        shareBankBalance = shareBankBalance.subtract(refundShare.getAppliedRefundSharesAmount());
        bankMaster.setShareBalance(shareBankBalance);

        bankService.saveBankMaster(bankMaster);

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            // For debit transaction against the refund share
            Transaction debitTransaction = new Transaction();

            debitTransaction.setDebitAmount(refundShare.getAppliedRefundSharesAmount());
            debitTransaction.setRemark("Amount debited from society share Acc No. : " + bankMaster.getShareAccountNumber());
            debitTransaction.setTransactionBy(user);
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setAccountNumber(bankMaster.getShareAccountNumber());
            debitTransaction.setTransferType(refundShare.getModeOfPayment());
            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);

            if (refundShare.getMemberName() != null && refundShare.getMemberNumber() != null) {
                debitTransaction.setAccountName(refundShare.getMemberName() + " (" + refundShare.getMemberNumber() + ")");
            }

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(refundShare.getAppliedRefundSharesAmount());
            debitTransaction.setBalance(balance);

            transactionService.transactionEntry(debitTransaction);

            if (debitTransaction != null) {
                String message = "" + debitTransaction.getCreditAmount() + " Amount debited from society share  Acc No. : " + bankMaster.getShareAccountNumber() + "From : " + refundShare.getMemberName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Refund Share Module", user.getId());
            }
            if (debitTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount debited from society sahre bank Acc No. " + bankMaster.getShareAccountNumber() + " submitted",
                        "REFUNDED SHARE", user.getId());
            }
        }
    }

    // REFUND SHARE CREDIT TRANSACTION
    private void refundShareCreditTransactionEntry(RefundShare refundShare, BankMaster bankMaster, SavingsBankDeposit savingsBankDeposit, User user) {

        Ledger ledger = null;
        if (savingsBankDeposit.getAccountType().equals(AccountType.SAVING)) {
            ledger = accountHeadService.getLedgerByName("Saving Bank Deposit");
        } else {
            ledger = accountHeadService.getLedgerByName("Current Account");
        }
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        SavingBankTransaction savingBankTransaction = savingBankTransactionService.getLatestTransactionOfSBAccountNumber(savingsBankDeposit.getAccountNumber());

        if (savingBankTransaction != null) {

            BigDecimal depositBalance = savingBankTransaction.getBalance();
            depositBalance = depositBalance.add(refundShare.getAppliedRefundSharesAmount());

            SavingBankTransaction sbTransaction = new SavingBankTransaction();

            sbTransaction.setTransactionType("CREDIT");
            sbTransaction.setCreditAmount(refundShare.getAppliedRefundSharesAmount());
            sbTransaction.setBalance(depositBalance);
            sbTransaction.setDebitAmount(BigDecimal.ZERO);
            sbTransaction.setAccountNumber(savingBankTransaction.getSavingsBankDeposit().getAccountNumber());
            sbTransaction.setTransactionBy(user);
            sbTransaction.setSavingsBankDeposit(savingsBankDeposit);

            savingBankTransactionService.createSavingBankTransaction(sbTransaction);

            savingsBankDeposit.setBalance(depositBalance);

            savingsBankDepositService.saveSavingsBankDeposit(savingsBankDeposit);

            if (savingBankTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credited to saving bank Acc No." + savingBankTransaction.getAccountNumber() + " submitted",
                        "SAVING BANK TRANSACTION", user.getId());
            }
            BigDecimal bankBalance = bankMaster.getBalance();
            bankBalance = bankBalance.add(refundShare.getAppliedRefundSharesAmount());
            bankMaster.setBalance(bankBalance);
            bankService.saveBankMaster(bankMaster);
        }

        Transaction latestTransaction = transactionService.latestTransaction();

        if (latestTransaction != null) {

            Transaction creditTransaction = new Transaction();
            creditTransaction.setCreditAmount(refundShare.getAppliedRefundSharesAmount());
            creditTransaction.setRemark("Amount credited to Saving Bank Acc No. : " + refundShare.getCreditAccountNumber());
            creditTransaction.setTransactionBy(user);
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setTransactionType("CREDIT");
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setAccountNumber(bankMaster.getShareAccountNumber());
            creditTransaction.setTransferType(refundShare.getModeOfPayment());
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);

            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(refundShare.getAppliedRefundSharesAmount());
            creditTransaction.setBalance(balance);

            transactionService.transactionEntry(creditTransaction);

            if (creditTransaction != null) {
                String message = "" + creditTransaction.getCreditAmount() + " Amount credited to saving bank Acc No. : " + refundShare.getCreditAccountNumber() + "From : " + refundShare.getMemberName();
                applicationLogService.recordApplicationLog(user.getFirstName(), message,
                        "Refund Share Module", user.getId());
            }
            if (creditTransaction != null) {
                applicationLogService.recordApplicationLog(user.getFirstName(), "Amount credit from society share bank Acc No. " + creditTransaction.getId() + " submitted",
                        "REFUNDED SHARE", user.getId());
            }
        }
    }

    private void checkLoans(Member member) {

        List<Loan> memberLoans = new ArrayList<>();
        List<Loan> loans = loanService.findLoanByCustomer(member.getCustomer());
        List<Loan> fixedLoans = loanService.getFixedDepositLoans(member.getCustomer());
        List<Loan> termLoans = loanService.getMemberTermLoans(member.getCustomer());

        if (loans != null) {
            memberLoans.addAll(loans);
        } else if (fixedLoans != null) {
            memberLoans.addAll(fixedLoans);
        } else if (termLoans != null) {
            memberLoans.addAll(termLoans);
        }

        if (memberLoans != null && memberLoans.size() > 0) {
            for (Loan loan : memberLoans) {
                if (!loan.getLoanStatus().equals(LoanStatus.LOAN_CLOSED)) {
                    throw new EntityNotFoundException("Membership can not be closed !!Active loan found " + loan.getLoanAccountNumber());
                }
            }
        }
    }

    @PostMapping("/edit-member-family-detail/{memberId}")
    public ResponseEntity<Member> editMemberFamilyDetail(@PathVariable long memberId, @Valid @RequestBody MemberFamilyDetails memberFamilyDetails) {

        Optional<Member> memberObj = memberService.getMember(memberId);

        Member member = memberObj.get();
        if (member != null) {
            List<MemberFamilyDetails> memberFamilyDetailsList = member.getMemberFamilyDetails();
            if (memberFamilyDetailsList != null) {
                for (int i = 0; i < memberFamilyDetailsList.size(); i++) {

                    MemberFamilyDetails familyDetails = memberFamilyDetailsList.get(i);
                    if (familyDetails.getId() == memberFamilyDetails.getId()) {
                        familyDetails.setTitle(memberFamilyDetails.getTitle());
                        familyDetails.setName(memberFamilyDetails.getName());
                        familyDetails.setAge(memberFamilyDetails.getAge());
                        familyDetails.setOccupationCode(memberFamilyDetails.getOccupationCode());
                        familyDetails.setRelation(memberFamilyDetails.getRelation());
                    }
                }
                member.setMemberFamilyDetails(memberFamilyDetailsList);
            }
        }
        memberService.saveMember(member);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping("/add-member-family-detail/{memberId}")
    public ResponseEntity<Member> addMemberFamilyDetail(@RequestBody MemberFamilyDetails memberFamilyDetails, @PathVariable Long memberId) {

        Optional<Member> memberObj = memberService.getMember(memberId);
        Member member = memberObj.get();
        memberFamilyDetails.setMember(member);
        member.getMemberFamilyDetails().add(memberFamilyDetails);
        Member persisted = memberService.saveMember(member);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    @DeleteMapping("/member-family-detail/{memberId}/{memberFamilyDetailId}")
    public void deleteMemberFamilyDetail(@PathVariable Long memberId, @PathVariable Long memberFamilyDetailId) {

        Optional<Member> memberObj = memberService.getMember(memberId);
        Member member = memberObj.get();
        List<MemberFamilyDetails> memberFamilyDetails = member.getMemberFamilyDetails();
        if (memberFamilyDetails != null) {
            for (int i = 0; i <= memberFamilyDetails.size(); i++) {
                if (memberFamilyDetails.get(i).getId() == memberFamilyDetailId) {
                    memberFamilyDetails.remove(i);
                }
            }
            member.setMemberFamilyDetails(memberFamilyDetails);
            memberService.saveMember(member);
        }
    }

    @PostMapping("/edit-member-nominee-detail/{memberId}")
    public ResponseEntity<Member> editMemberNomineeDetail(@PathVariable long memberId, @Valid @RequestBody MemberNomineeDetails memberNomineeDetails) {

        Optional<Member> memberObj = memberService.getMember(memberId);
        Member member = memberObj.get();
        if (member != null) {
            List<MemberNomineeDetails> memberNomineeDetailsList = member.getMemberNomineeDetails();
            if (memberNomineeDetailsList != null) {
                for (int i = 0; i < memberNomineeDetailsList.size(); i++) {

                    MemberNomineeDetails nomineeDetails = memberNomineeDetailsList.get(i);
                    if (nomineeDetails.getId() == memberNomineeDetails.getId()) {
                        nomineeDetails.setRelationWithMember(memberNomineeDetails.getRelationWithMember());
                        nomineeDetails.setName(memberNomineeDetails.getName());
                        nomineeDetails.setAge(memberNomineeDetails.getAge());
                        nomineeDetails.setPhoneNumber(memberNomineeDetails.getPhoneNumber());
                        nomineeDetails.setVillage(memberNomineeDetails.getVillage());
                        nomineeDetails.setDistrict(memberNomineeDetails.getDistrict());
                        nomineeDetails.setTaluka(memberNomineeDetails.getTaluka());
                        nomineeDetails.setPinCode(memberNomineeDetails.getPinCode());
                        nomineeDetails.setResidentiaAddress(memberNomineeDetails.getResidentiaAddress());
                        nomineeDetails.setMinor(memberNomineeDetails.getMinor());
                    }
                }
                member.setMemberNomineeDetails(memberNomineeDetailsList);
            }
        }
        memberService.saveMember(member);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping("/add-member-nominee-detail/{memberId}")
    public ResponseEntity<Member> addMemberNomineeDetail(@RequestBody MemberNomineeDetails memberNomineeDetails, @PathVariable Long memberId) {
        Optional<Member> memberObj = memberService.getMember(memberId);
        Member member = memberObj.get();
        memberNomineeDetails.setMember(member);
        member.getMemberNomineeDetails().add(memberNomineeDetails);
        Member persisted = memberService.saveMember(member);
        return new ResponseEntity<>(persisted, HttpStatus.OK);
    }

    @GetMapping("/member/member-by-customer/{customerId}")
    public ResponseEntity<Member> getMemberByCustomer(@PathVariable Long customerId) {
        Optional<Customer> customerObj = customerService.getCustomer(customerId);
        Customer customer = customerObj.get();
        Member member = memberService.getMemberByCustomer(customer);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }
}