package com.dfq.coeffi.cbs.external.pigmy;

import com.dfq.coeffi.cbs.admin.service.BODDateService;
import com.dfq.coeffi.cbs.applicationlogs.log.ApplicationLogService;
import com.dfq.coeffi.cbs.bank.service.BankService;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransaction;
import com.dfq.coeffi.cbs.deposit.depositTransaction.pigmyDepositTransaction.PigmyDepositTransactionService;
import com.dfq.coeffi.cbs.deposit.entity.FixedDeposit;
import com.dfq.coeffi.cbs.deposit.entity.PigmyAgent;
import com.dfq.coeffi.cbs.deposit.entity.PigmyDeposit;
import com.dfq.coeffi.cbs.deposit.service.FixedDepositService;
import com.dfq.coeffi.cbs.deposit.service.PigmyAgentService;
import com.dfq.coeffi.cbs.deposit.service.PigmyDepositService;
import com.dfq.coeffi.cbs.init.BaseController;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.master.service.AccountHeadService;
import com.dfq.coeffi.cbs.transaction.entity.Transaction;
import com.dfq.coeffi.cbs.transaction.service.TransactionService;
import com.dfq.coeffi.cbs.user.entity.User;
import com.dfq.coeffi.cbs.utils.DateUtil;
import com.dfq.coeffi.cbs.utils.TransactionUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.CREDIT;
import static com.dfq.coeffi.cbs.master.entity.accounthead.AccountHeadType.DEBIT;

@RestController
public class PigmyCollectionResource extends BaseController {

    @Autowired
    private PigmyCollectionRepository pigmyCollectionRepository;

    @Autowired
    private PigmyDepositService pigmyDepositService;

    @Autowired
    private ApplicationLogService applicationLogService;

    @Autowired
    private PigmyDepositTransactionService pigmyDepositTransactionService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountHeadService accountHeadService;

    @Autowired
    private PigmyAgentService pigmyAgentService;

    @Autowired
    private BODDateService bodDateService;

    @Autowired
    private BankService bankService;

    @Autowired
    private FixedDepositService fixedDepositService;

    @Value("${pigmyFilePath}")
    private String FILE_PATH;

    @Value("${pigmyReadFilePath}")
    private String PIGMY_READ_FILE_PATH;

    @GetMapping("/pigmy-collected-data")
    public ResponseEntity<List<PigmyCollection>> getPigmyColletedDeviceData() {
        List<PigmyCollection> objects = pigmyCollectionRepository.findByUploadedOn(DateUtil.getTodayDate());
        if (CollectionUtils.isEmpty(objects)) {
            throw new EntityNotFoundException("No active pigmy collection data found");
        }
        return new ResponseEntity<>(objects, HttpStatus.OK);
    }

    @PostMapping("/pigmy-device-read")
    public ResponseEntity<List<PigmyDepositCollectionDto>> readDataFromDevice(@RequestParam("file") MultipartFile file) {

        BufferedReader bufferedReader;
        List<PigmyDepositCollectionDto> pigmyCollections = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String dataString;
            while ((dataString = bufferedReader.readLine()) != null) {
                String[] lineStringArray = dataString.split(",");

                PigmyDepositCollectionDto pigmyCollection = new PigmyDepositCollectionDto();
                if (lineStringArray != null && lineStringArray.length > 0) {

                    if (!lineStringArray[0].isEmpty() && lineStringArray[0].length() > 1) {
                        String accountNumber = lineStringArray[2].trim().replace("\u0000", "");
                        Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumber(accountNumber);
                        if (pigmyDepositOptional.isPresent()) {
                            PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();
                            pigmyCollection.setPigmyDeposit(pigmyDeposit);
                        }

                        pigmyCollection.setAccountNumber(accountNumber);
                        Optional<PigmyAgent> pigmyAgentOptional = pigmyAgentService.getPigmyAgentById(Long.parseLong(lineStringArray[16].trim()));
                        if (pigmyAgentOptional.isPresent()) {
                            PigmyAgent pigmyAgent = pigmyAgentOptional.get();
                            pigmyCollection.setPigmyAgent(pigmyAgent);
                        }

                        if (lineStringArray[3].length() > 1) {
                            System.out.println("AMT : " + lineStringArray[3]);
                            System.out.println("AMT : " + lineStringArray[3].length());

                        }
                        String collectedAmount = lineStringArray[3].trim();
                        pigmyCollection.setCollectedAmount(collectedAmount.replace("\u0000", ""));
                        pigmyCollection.setCollectedOn(DateUtil.convertToDate(lineStringArray[8].replace("\u0000", "")));
                        pigmyCollections.add(pigmyCollection);
                    }
                }
            }
            inputStream.close();
        } catch (IOException e) {
            System.out.println("File Read Error");
        }


        return new ResponseEntity<>(pigmyCollections, HttpStatus.OK);
    }

    @GetMapping("/pigmy-device-upload-text")
    public ResponseEntity<PigmyFileDto> downloadPigmyDeviceUploadTextFile() {

        PigmyFileDto fileDto = new PigmyFileDto();
        fileDto.setFilePath(PIGMY_READ_FILE_PATH + "/download");
        File file = null;
        try {
            List<PigmyDeposit> pigmyDeposits = pigmyDepositService.getPigmyDepositActiveAccounts();
            if (CollectionUtils.isEmpty(pigmyDeposits)) {
                throw new EntityNotFoundException("No PigmyDeposits accounts for the provided member");
            }

            FileUtils.cleanDirectory(new File(PIGMY_READ_FILE_PATH + "/download"));
            file = new File(PIGMY_READ_FILE_PATH + "/download/master.MST");

            boolean fileCreated = file.createNewFile();
            if (fileCreated) {
                FileWriter entityWriter = new FileWriter(file);
                for (PigmyDeposit pigmyDeposit : pigmyDeposits) {
                    if (pigmyDeposit != null) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String accountOpeningDate = simpleDateFormat.format(pigmyDeposit.getModifiedDate());
                        entityWriter.write("" + pigmyDeposit.getAccountType() +
                                "," + pigmyDeposit.getAccountNumber() +
                                "," + pigmyDeposit.getMember().getName() +
                                "," +
                                ",," +
                                "," + pigmyDeposit.getAgreedAmount().intValue() +
                                ",0" +
                                "," + pigmyDeposit.getBalance().intValue() +
                                "," + accountOpeningDate +
                                ",0" + "\n");
                    }
                }
                entityWriter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(fileDto, HttpStatus.OK);
    }

    @PostMapping("/upload-pigmy-device-data")
    public ResponseEntity<List<PigmyDeposit>> uploadPigmyDeviceDatatoCbs(@RequestBody List<PigmyDepositCollectionDto> pigmyCollectedData, Principal principal) {

        bodDateService.checkBOD();
        List<PigmyCollection> pigmyCollections = pigmyCollectionRepository.findByUploadedOn(DateUtil.getTodayDate());
        if (pigmyCollections != null && pigmyCollections.size() > 0) {
            throw new EntityNotFoundException("Pigmy device data already uploaded for the day");
        }
        List<PigmyDeposit> pigmyDeposits = new ArrayList<>();
        if (CollectionUtils.isEmpty(pigmyCollectedData)) {
            throw new EntityNotFoundException("No active pigmy collection data found");
        }

        if (pigmyCollectedData != null && pigmyCollectedData.size() > 0) {
            User loggedUser = getLoggedUser(principal);
            for (PigmyDepositCollectionDto pigmyCollection : pigmyCollectedData) {
                Optional<PigmyDeposit> pigmyDepositOptional = pigmyDepositService.getPigmyDepositByAccountNumber(pigmyCollection.getAccountNumber());
                if (pigmyDepositOptional.isPresent()) {

                    PigmyDeposit pigmyDeposit = pigmyDepositOptional.get();
                    BigDecimal pigmyDepositBalance = pigmyDeposit.getBalance();
                    BigDecimal pigmyDepositAmount = pigmyDeposit.getDepositAmount();
                    pigmyDepositBalance = pigmyDepositBalance.add(new BigDecimal(Double.parseDouble(pigmyCollection.getCollectedAmount())));
                    pigmyDepositAmount = pigmyDepositAmount.add(new BigDecimal(Double.parseDouble(pigmyCollection.getCollectedAmount())));
                    pigmyDeposit.setBalance(pigmyDepositBalance);
                    pigmyDeposit.setDepositAmount(pigmyDepositAmount);

                    PigmyDeposit updatePigmyDeposit = pigmyDepositService.createPigmyDeposit(pigmyDeposit);
                    if (updatePigmyDeposit != null) {

                        PigmyCollection pigmyCollectionTrack = new PigmyCollection();
                        pigmyCollectionTrack.setPigmyDeposit(updatePigmyDeposit);
                        pigmyCollectionTrack.setPigmyAgent(updatePigmyDeposit.getPigmyAgent());
                        pigmyCollectionTrack.setCollectedAmount(new BigDecimal(Double.parseDouble(pigmyCollection.getCollectedAmount())));
                        pigmyCollectionTrack.setUploadedOn(DateUtil.getTodayDate());
                        pigmyCollectionTrack.setRefId(updatePigmyDeposit.getAccountNumber());
                        pigmyCollectionRepository.save(pigmyCollectionTrack);

                        pigmyDeposits.add(updatePigmyDeposit);
                        PigmyDepositTransaction pigmyDepositTransaction = new PigmyDepositTransaction();
                        pigmyDepositTransaction.setCreditAmount(new BigDecimal(Double.parseDouble(pigmyCollection.getCollectedAmount())));
                        pigmyDepositTransaction.setTransactionType("CREDIT");
                        pigmyDepositTransaction.setBalance(updatePigmyDeposit.getDepositAmount());
                        pigmyDepositTransaction.setPigmyDeposit(updatePigmyDeposit);
                        pigmyDepositTransaction.setAccountNumber(updatePigmyDeposit.getAccountNumber());
                        User user = getLoggedUser(principal);
                        pigmyDepositTransaction.setTransactionBy(user);

                        pigmyDepositTransactionService.createPigmyDepositTransaction(pigmyDepositTransaction);
                        pigmyTransactionCreditEntry(updatePigmyDeposit, new BigDecimal(Double.parseDouble(pigmyCollection.getCollectedAmount())));

                        applicationLogService.recordApplicationLog(loggedUser.getFirstName(), "Pigmy deposit amount " + pigmyCollection.getCollectedAmount()
                                        + " for Acc No. : " + updatePigmyDeposit.getAccountNumber() + " updated.",
                                "Pigmy Deposit", loggedUser.getId());

                        // Update society bank balance
                        TransactionUtil transactionUtil = new TransactionUtil(bankService);
                        transactionUtil.getUpdateSocietyBalance(pigmyCollectionTrack.getCollectedAmount(), "CREDIT");

                    }
                }
            }
        }

        return new ResponseEntity<>(pigmyDeposits, HttpStatus.OK);
    }

    private void pigmyTransactionCreditEntry(PigmyDeposit pigmyDeposit, BigDecimal amount) {
        Ledger ledger = accountHeadService.getLedgerByName("Pigmy Deposit");
        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction creditTransaction = new Transaction();
            creditTransaction.setDebitAmount(new BigDecimal(0));
            creditTransaction.setRemark("Initial Amount Credited for currentAccount Account " + pigmyDeposit.getId());
            creditTransaction.setCreditAmount(amount);
            creditTransaction.setTransactionBy(pigmyDeposit.getTransactionBy());
            creditTransaction.setTransactionOn(DateUtil.getTodayDate());
            creditTransaction.setParticulars("PIGMY DEPOSIT");
            creditTransaction.setTransferType("CASH");
            creditTransaction.setTransactionType(pigmyDeposit.getTransactionType());
            creditTransaction.setVoucherType(pigmyDeposit.getVoucherType());
            creditTransaction.setAccountNumber(pigmyDeposit.getAccountNumber());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.add(creditTransaction.getCreditAmount());
            creditTransaction.setBalance(balance);
            creditTransaction.setAccountHead(ledger.getAccountHead());
            creditTransaction.setLedger(ledger);
            transactionService.transactionEntry(creditTransaction);
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(amount, "CREDIT");
        }
    }

    @GetMapping("/pigmy-agent-collection/{agentId}")
    public ResponseEntity<AgentCollectionDto> getPigmyAgentCollection(@PathVariable("agentId") long agentId) {

        AgentCollectionDto dto = null;
        Optional<PigmyAgent> pigmyAgentOptions = pigmyAgentService.getPigmyAgentById(agentId);
        PigmyAgent pigmyAgent = pigmyAgentOptions.get();

        System.out.println("FIRST DATE " + DateUtil.getFirstDateOfCurrentMonth(new Date()));
        System.out.println("LAST DATE " + DateUtil.getLastDateOfCurrentMonth(new Date()));
        List<PigmyCollection> pigmyCollections = pigmyCollectionRepository.getCurrentAgentCollection(pigmyAgent.getId(), DateUtil.getFirstDateOfCurrentMonth(new Date()), DateUtil.getLastDateOfCurrentMonth(new Date()));
        if (pigmyCollections != null && pigmyCollections.size() > 0) {
            BigDecimal totalCollection = new BigDecimal(BigInteger.ZERO);
            for (PigmyCollection collection : pigmyCollections) {
                totalCollection = totalCollection.add(collection.getCollectedAmount());
            }

            dto = new AgentCollectionDto();
            dto.setCollectedAmount(totalCollection);
            dto.setPigmyAgent(pigmyAgent);
        } else {
            throw new EntityNotFoundException("No collection found for the selected agent");
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/fixed-deposit-account-number/{accountNumber}")
    public ResponseEntity<FixedDeposit> getFixedDepositByDepositAccountNumber(@PathVariable("accountNumber") String accountNumber) {

        Optional<FixedDeposit> fixedDepositOptional = fixedDepositService.getFixedDepositByAccountNumber(accountNumber);
        if (!fixedDepositOptional.isPresent()) {
            throw new EntityNotFoundException("No fixed deposit found for the Acc : " + accountNumber);
        }
        FixedDeposit fixedDeposit = fixedDepositOptional.get();
        return new ResponseEntity<>(fixedDeposit, HttpStatus.OK);
    }


    @PostMapping("/fixed-deposit-partial-withdraw")
    public ResponseEntity<FixedDeposit> fixedDepositPartialWithdraw(@RequestBody PigmyDepositCollectionDto pigmyDepositCollectionDto) {

        bodDateService.checkBOD();

        Optional<FixedDeposit> fixedDepositOptional = fixedDepositService.getFixedDepositByAccountNumber(pigmyDepositCollectionDto.getAccountNumber());
        if (!fixedDepositOptional.isPresent()) {
            throw new EntityNotFoundException("No fixed deposit found for the Acc : " + pigmyDepositCollectionDto.getAccountNumber());
        }
        FixedDeposit fixedDeposit = fixedDepositOptional.get();

        if (pigmyDepositCollectionDto.getAmount() > fixedDeposit.getDepositAmount().intValue()) {
            throw new EntityNotFoundException("Partial withdraw amount should not be great than fixed deposit amount");
        }

        BigDecimal depositAmount = fixedDeposit.getDepositAmount().subtract(new BigDecimal(pigmyDepositCollectionDto.getAmount()));
        fixedDeposit.setDepositAmount(depositAmount);

        BigDecimal withdrawAmount = fixedDeposit.getWithdrawAmount();
        if(withdrawAmount == null){
            withdrawAmount = new BigDecimal(0);
        }
        withdrawAmount = withdrawAmount.add(new BigDecimal(pigmyDepositCollectionDto.getAmount()));
        fixedDeposit.setWithdrawAmount(withdrawAmount);

        BigDecimal maturityAmount = fixedDeposit.getMaturityAmount();
        maturityAmount = maturityAmount.subtract(new BigDecimal(pigmyDepositCollectionDto.getAmount()));

        fixedDeposit.setMaturityAmount(maturityAmount);

        FixedDeposit persistedDeposit = fixedDepositService.saveFixedDeposit(fixedDeposit);

        if (persistedDeposit != null) {
            transactionDebitEntry(persistedDeposit, new BigDecimal(pigmyDepositCollectionDto.getAmount()),pigmyDepositCollectionDto.getModeOfPayment(),pigmyDepositCollectionDto.getChequeNumber());
            TransactionUtil transactionUtil = new TransactionUtil(bankService);
            transactionUtil.getUpdateSocietyBalance(new BigDecimal(pigmyDepositCollectionDto.getAmount()), "DEBIT");
        }

        return new ResponseEntity<>(fixedDeposit, HttpStatus.OK);
    }

    private void transactionDebitEntry(FixedDeposit fixedDeposit, BigDecimal amount,String modeOfPayment,String chequeNumber) {

        Ledger ledger = accountHeadService.getLedgerByName("Fixed Deposit");
        if (ledger == null) {
            throw new EntityNotFoundException("Account head or ledger not found");
        }

        Transaction latestTransaction = transactionService.latestTransaction();
        if (latestTransaction != null) {
            Transaction debitTransaction = new Transaction();
            debitTransaction.setDebitAmount(amount);
            debitTransaction.setRemark("Amount debited for partial fixed deposit withdrawn thru "+modeOfPayment+" : "+chequeNumber);
            debitTransaction.setCreditAmount(new BigDecimal(0));
            debitTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            debitTransaction.setTransactionOn(DateUtil.getTodayDate());
            debitTransaction.setTransactionType("DEBIT");
            debitTransaction.setParticulars("FIXED DEPOSIT");
            debitTransaction.setVoucherType(fixedDeposit.getVoucherType());
            BigDecimal balance = latestTransaction.getBalance();
            balance = balance.subtract(debitTransaction.getDebitAmount());
            debitTransaction.setBalance(balance);

            debitTransaction.setAccountHead(ledger.getAccountHead());
            debitTransaction.setLedger(ledger);
            debitTransaction.setAccountNumber(fixedDeposit.getAccountNumber());
            debitTransaction.setTransactionBy(fixedDeposit.getTransactionBy());
            debitTransaction.setTransferType(modeOfPayment);
            debitTransaction.setAccountName(fixedDeposit.getMember().getName() + " (" + fixedDeposit.getMember().getMemberNumber() + ")");
            transactionService.transactionEntry(debitTransaction);
        }
    }
}