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
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
public class SavingsBankDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 20)
    private String applicationNumber;

    @Column(length = 20)
    private String transactionNumber;

    private String accountNumber;

    private Date createdOn;

    @Column(name = "MODIFIED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private DepositType depositType;

    @Column(length = 20)
    private BigDecimal balance;

    @Column(length = 10)
    private boolean isApproved;

    @Column(length = 10)
    private boolean status;

    private String accountStatus;

    private Date accountClosedOn;

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

    @OneToOne
    private User transactionBy;

    private BigDecimal depositAmount;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    private String receiptNumber;

    private BigDecimal sbInterestAmount;

    private BigDecimal fixedDepositInterestAmount;

    private Date lastUpdatedDate;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Date getAccountClosedOn() {
		return accountClosedOn;
	}

	public void setAccountClosedOn(Date accountClosedOn) {
		this.accountClosedOn = accountClosedOn;
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

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
	}

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
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

	public BigDecimal getSbInterestAmount() {
		return sbInterestAmount;
	}

	public void setSbInterestAmount(BigDecimal sbInterestAmount) {
		this.sbInterestAmount = sbInterestAmount;
	}

	public BigDecimal getFixedDepositInterestAmount() {
		return fixedDepositInterestAmount;
	}

	public void setFixedDepositInterestAmount(BigDecimal fixedDepositInterestAmount) {
		this.fixedDepositInterestAmount = fixedDepositInterestAmount;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
}