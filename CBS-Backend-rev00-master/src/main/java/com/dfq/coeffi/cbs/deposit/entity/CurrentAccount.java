package com.dfq.coeffi.cbs.deposit.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
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
public class CurrentAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 20)
    private String applicationNumber;

    @Column(length = 20)
    private String accountNumber;

    @Column(length = 20)
    private String transactionNumber;

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

    @Column(length = 20)
    private BigDecimal depositAmount;

    @Column(length = 10)
    private boolean approved;

    @Column(length = 10)
    private boolean status;

    private String accountStatus;

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

    private String transactionType;

    private String voucherType;

    private Date accountClosedOn;

    @OneToOne
    private User transactionBy;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    private String receiptNumber;

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

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
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

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
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

	public Date getAccountClosedOn() {
		return accountClosedOn;
	}

	public void setAccountClosedOn(Date accountClosedOn) {
		this.accountClosedOn = accountClosedOn;
	}

	public User getTransactionBy() {
		return transactionBy;
	}

	public void setTransactionBy(User transactionBy) {
		this.transactionBy = transactionBy;
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
