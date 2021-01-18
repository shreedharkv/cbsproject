package com.dfq.coeffi.cbs.deposit.entity;

import com.dfq.coeffi.cbs.deposit.api.DepositNomineeDetails;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class PigmyDeposit implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(length = 20)
    private String applicationNumber;

    @Column(length = 20)
    private String transactionNumber;

    private String accountNumber;

    private String exgAccountNumber;

    private Date createdOn;

    @Column(name="MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private DepositType depositType;

    @Column(length = 20)
    private BigDecimal depositAmount;

    @Column(length = 20)
    private BigDecimal balance;

    @Column(length = 10)
    private boolean status;

    @Column(length = 10)
    private boolean isWithDrawn;

    private String modeOfPayment;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositsApproval depositsApproval;

    @OneToOne(cascade = CascadeType.ALL)
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositNomineeDetails depositNomineeDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositNomineeDetails depositNomineeDetailsTwo;

    @OneToOne(cascade = CascadeType.ALL)
    private DepositNomineeDetails depositNomineeDetailsThree;

    @OneToOne(cascade = CascadeType.ALL)
    private PigmyAgent pigmyAgent;

    private String transactionType;

    private String voucherType;

    @OneToOne
    private User transactionBy;

    private BigDecimal agreedAmount;

    @OneToOne
    private PigmyBankData pigmyBankData;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    private String receiptNumber;

    private BigDecimal maturityAmount;
    private BigDecimal calculatedInterest;

    private Date maturityDate;

    private BigDecimal periodOfDeposit;

    private Date withdrawnOn;

    private Date accountCloserDate;

    private BigDecimal withdrawAmount;

    private String accountType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getExgAccountNumber() {
		return exgAccountNumber;
	}

	public void setExgAccountNumber(String exgAccountNumber) {
		this.exgAccountNumber = exgAccountNumber;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public DepositType getDepositType() {
		return depositType;
	}

	public void setDepositType(DepositType depositType) {
		this.depositType = depositType;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isWithDrawn() {
		return isWithDrawn;
	}

	public void setWithDrawn(boolean isWithDrawn) {
		this.isWithDrawn = isWithDrawn;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public DepositsApproval getDepositsApproval() {
		return depositsApproval;
	}

	public void setDepositsApproval(DepositsApproval depositsApproval) {
		this.depositsApproval = depositsApproval;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public DepositNomineeDetails getDepositNomineeDetails() {
		return depositNomineeDetails;
	}

	public void setDepositNomineeDetails(DepositNomineeDetails depositNomineeDetails) {
		this.depositNomineeDetails = depositNomineeDetails;
	}

	public DepositNomineeDetails getDepositNomineeDetailsTwo() {
		return depositNomineeDetailsTwo;
	}

	public void setDepositNomineeDetailsTwo(DepositNomineeDetails depositNomineeDetailsTwo) {
		this.depositNomineeDetailsTwo = depositNomineeDetailsTwo;
	}

	public DepositNomineeDetails getDepositNomineeDetailsThree() {
		return depositNomineeDetailsThree;
	}

	public void setDepositNomineeDetailsThree(DepositNomineeDetails depositNomineeDetailsThree) {
		this.depositNomineeDetailsThree = depositNomineeDetailsThree;
	}

	public PigmyAgent getPigmyAgent() {
		return pigmyAgent;
	}

	public void setPigmyAgent(PigmyAgent pigmyAgent) {
		this.pigmyAgent = pigmyAgent;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getVoucherType() {
		return voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
	}

	public BigDecimal getAgreedAmount() {
		return agreedAmount;
	}

	public void setAgreedAmount(BigDecimal agreedAmount) {
		this.agreedAmount = agreedAmount;
	}

	public PigmyBankData getPigmyBankData() {
		return pigmyBankData;
	}

	public void setPigmyBankData(PigmyBankData pigmyBankData) {
		this.pigmyBankData = pigmyBankData;
	}

	public AccountHead getAccountHead() {
		return accountHead;
	}

	public void setAccountHead(AccountHead accountHead) {
		this.accountHead = accountHead;
	}

	public Ledger getLedger() {
		return ledger;
	}

	public void setLedger(Ledger ledger) {
		this.ledger = ledger;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public BigDecimal getMaturityAmount() {
		return maturityAmount;
	}

	public void setMaturityAmount(BigDecimal maturityAmount) {
		this.maturityAmount = maturityAmount;
	}

	public BigDecimal getCalculatedInterest() {
		return calculatedInterest;
	}

	public void setCalculatedInterest(BigDecimal calculatedInterest) {
		this.calculatedInterest = calculatedInterest;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public BigDecimal getPeriodOfDeposit() {
		return periodOfDeposit;
	}

	public void setPeriodOfDeposit(BigDecimal periodOfDeposit) {
		this.periodOfDeposit = periodOfDeposit;
	}

	public Date getWithdrawnOn() {
		return withdrawnOn;
	}

	public void setWithdrawnOn(Date withdrawnOn) {
		this.withdrawnOn = withdrawnOn;
	}

	public Date getAccountCloserDate() {
		return accountCloserDate;
	}

	public void setAccountCloserDate(Date accountCloserDate) {
		this.accountCloserDate = accountCloserDate;
	}

	public BigDecimal getWithdrawAmount() {
		return withdrawAmount;
	}

	public void setWithdrawAmount(BigDecimal withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
