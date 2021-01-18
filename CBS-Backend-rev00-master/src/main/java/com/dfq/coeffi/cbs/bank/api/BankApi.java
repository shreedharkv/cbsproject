package com.dfq.coeffi.cbs.bank.api;

import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.entity.BankMaster;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.document.FileStorageService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.transaction.entity.BankTransaction;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.BankTransactionService;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class BankApi extends BaseController {

    private final BankService bankService;
    private final ApplicationLogService applicationLogService;
    private final FileStorageService fileStorageService;
    private final TransactionService transactionService;
    private final BankTransactionService bankTransactionService;

    @Autowired
    private BankApi(final BankService bankService,final ApplicationLogService applicationLogService,
                    final FileStorageService fileStorageService,final TransactionService transactionService,
                    final BankTransactionService bankTransactionService){
        this.bankService = bankService;
        this.applicationLogService = applicationLogService;
        this.fileStorageService = fileStorageService;
        this.transactionService = transactionService;
        this.bankTransactionService = bankTransactionService;
    }

    @GetMapping("/bank")
    public ResponseEntity<List<BankMaster>> getBankMasters() {
        List<BankMaster> bankMasters = bankService.getBankMasters();
        if (CollectionUtils.isEmpty(bankMasters)) {
            log.warn("Bank master not found");
            throw new EntityNotFoundException("bankMasters");
        }
        return new ResponseEntity<>(bankMasters, HttpStatus.OK);
    }

    @GetMapping("/bank/{id}")
    public ResponseEntity<BankMaster> getBankMaster(@PathVariable Long id) {
        Optional<BankMaster> bankMasterObj = bankService.getBankMaster(id);

        if (!bankMasterObj.isPresent()) {
            log.warn("bank not found : id");
            throw new EntityNotFoundException("bank not found for id " + id);
        }
        BankMaster bankMaster = bankMasterObj.get();
        return new ResponseEntity<>(bankMaster, HttpStatus.OK);
    }

    @PostMapping("/bank")
    public ResponseEntity<BankMaster> createBankMaster(@Valid @RequestBody BankMaster bankMaster,Principal principal) {
        User loggedUser = getLoggedUser(principal);

        BankMaster persistedObject = bankService.saveBankMaster(bankMaster);
        if(persistedObject != null){
            bankTransactionEntry(persistedObject,loggedUser);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Bank no. " + persistedObject.getId() + " submitted",
                    "BANK CREATED", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @PostMapping("/bank/upload-logo-image/{bankId}/{documentCategoryId}/{imageType}")
    public ResponseEntity<BankMaster> uploadLogoAndImage(@PathVariable long bankId,@RequestParam("file") MultipartFile file,
                                                         @PathVariable("documentCategoryId") long documentCategoryId,
                                                         @PathVariable("imageType") long imageType,Principal principal) {

        Document document = fileStorageService.storeFile(file, documentCategoryId);
        Optional<BankMaster> bankMasterObj = bankService.getBankMaster(bankId);
        if(!bankMasterObj.isPresent()){
            throw new EntityNotFoundException("Bank not found");
        }
        BankMaster bankMaster = bankMasterObj.get();

        if(imageType == 1){
            bankMaster.setDocumentLogo(document);
        }
        else if(imageType == 2){
            bankMaster.setDocumentImage(document);
        }

        BankMaster persistedObject = bankService.saveBankMaster(bankMaster);
        if(persistedObject != null){
            User loggedUser = getLoggedUser(principal);
            applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Bank no. " + persistedObject.getId() + " submitted",
                    "LOGO or IMAGE UPLOADED", loggedUser.getId());
        }
        return new ResponseEntity<>(persistedObject, HttpStatus.CREATED);
    }

    @DeleteMapping("/bank")
    public ResponseEntity<BankMaster> delete(@PathVariable long id) {
        Optional<BankMaster> bankMasterObj = bankService.getBankMaster(id);
        if (!bankMasterObj.isPresent()) {
            log.warn("bank not found : id");
            throw new EntityNotFoundException("bank not found for id " + id);
        }
        bankService.deleteBankMaster(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/bank/active-bank")
    public ResponseEntity<BankMaster> getActiveBank(){
        Optional<BankMaster> bankMasterObj = bankService.getActiveBank();
        if(!bankMasterObj.isPresent()){
            throw new EntityNotFoundException("No active bank found");
        }
        BankMaster bankMaster = bankMasterObj.get();
        return new ResponseEntity<>(bankMaster, HttpStatus.OK);
    }

    private void bankTransactionEntry(BankMaster bankMaster,User user){

//        Transaction latestTransaction = transactionService.latestTransaction();
//
//        BigDecimal balance;
//        if(latestTransaction == null){
//            balance = new BigDecimal(0);
//            balance = balance.add(bankMaster.getBalance());
//
//        }else{
//            balance = latestTransaction.getBalance();
//            balance = balance.add(bankMaster.getBalance());
//        }
//
//        // For credit transaction against the bank
//        Transaction creditTransaction = new Transaction();
//
//        creditTransaction.setBalance(balance);
//
////        creditTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getCreditAccount());
//        creditTransaction.setCreditAmount(bankMaster.getBalance());
//        creditTransaction.setRemark("Amount credited to society main Acc No. : " + bankMaster.getAccountNumber());
//        creditTransaction.setTransactionBy(user);
//        creditTransaction.setTransactionOn(DateUtil.getTodayDate());
//        creditTransaction.setTransactionType("CREDIT");
//        creditTransaction.setDebitAmount(new BigDecimal(0));
//        creditTransaction.setAccountNumber(bankMaster.getAccountNumber());
//
//        Transaction transaction = transactionService.transactionEntry(creditTransaction);


        // BANK TRANSACTION ENTRY
        BankTransaction latestBankTransaction = bankTransactionService.latestBankTransaction();

        // For credit bank transaction against the bank
        BankTransaction creditBankTransaction = new BankTransaction();

//       creditTransaction.setAccountHead(scheduledLoan.getLoanDisbursement().getCreditAccount());
        creditBankTransaction.setCreditAmount(bankMaster.getBalance());
        creditBankTransaction.setRemark("Amount credited to Bank Transaction from" + bankMaster.getBankName() + " : " + bankMaster.getAccountNumber());
        creditBankTransaction.setTransactionBy(user);
        creditBankTransaction.setTransactionOn(DateUtil.getTodayDate());
        creditBankTransaction.setTransactionType("CREDIT");
        creditBankTransaction.setDebitAmount(new BigDecimal(0));
        creditBankTransaction.setAccountNumber(bankMaster.getAccountNumber());
        creditBankTransaction.setBankMaster(bankMaster);
        creditBankTransaction.setAccountHead(latestBankTransaction.getAccountHead());
        creditBankTransaction.setTransferType("Cash");

        BigDecimal bankTransactionBalance = latestBankTransaction.getBalance();
        bankTransactionBalance = bankTransactionBalance.add(bankMaster.getBalance());
        creditBankTransaction.setBalance(bankTransactionBalance);

        BankTransaction bankTransaction = bankTransactionService.bankTransactionEntry(creditBankTransaction);

        if(bankTransaction != null){
            String message = "" + bankTransaction.getCreditAmount() + " Amount credited to society main Acc No. : " + bankMaster.getAccountNumber();
            applicationLogService.recordApplicationLog(user.getFirstName(), message,
                    "Bank Module", user.getId());
        }
    }

    @GetMapping("/bank/all-bank")
    public ResponseEntity<List<BankMaster>> getAllBankMasters(){
        List<BankMaster> bankMasters = bankService.getAllBankMasters();
        return new ResponseEntity<>(bankMasters, HttpStatus.OK);
    }
}