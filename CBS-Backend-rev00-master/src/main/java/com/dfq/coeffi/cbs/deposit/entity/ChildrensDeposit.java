package com.dfq.coeffi.cbs.deposit.entity;

import com.dfq.coeffi.cbs.deposit.api.DepositNomineeDetails;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.member.entity.MemberIntroducersDetails;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@ToString
public class ChildrensDeposit implements Serializable {

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

    private String transactionType;

    private String voucherType;

    @OneToOne
    private User transactionBy;

    @Column(length = 20)
    private BigDecimal depositAmount;

    @Column(length = 20)
    private BigDecimal periodOfDeposit;

    @Column(length = 20)
    private double rateOfInterest;

    @Column(length = 45)
    private BigDecimal maturityAmount;

    @Column(length = 45)
    private BigDecimal interestAmount;

    @Column(length = 45)
    private BigDecimal preMatureFine;

    private BigDecimal preMatureAmount;

    private BigDecimal preMatureInterestAmount;

    private BigDecimal preMaturePeriodOfDeposit;

    private Date preMatureDate;

    private double preMatureRateOfInterest;

    private String duration;

    @Column(length = 15)
    private Date maturityDate;

    @Column(length = 10)
    private boolean status;

    @Column(length = 10)
    private boolean isWithDrawn;

    private Date accountClosedOn;

    private String modeOfPayment;

    private String periodOfName;

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

	public BigDecimal getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(BigDecimal depositAmount) {
		this.depositAmount = depositAmount;
	}

	public BigDecimal getPeriodOfDeposit() {
		return periodOfDeposit;
	}

	public void setPeriodOfDeposit(BigDecimal periodOfDeposit) {
		this.periodOfDeposit = periodOfDeposit;
	}

	public double getRateOfInterest() {
		return rateOfInterest;
	}

	public void setRateOfInterest(double rateOfInterest) {
		this.rateOfInterest = rateOfInterest;
	}

	public BigDecimal getMaturityAmount() {
		return maturityAmount;
	}

	public void setMaturityAmount(BigDecimal maturityAmount) {
		this.maturityAmount = maturityAmount;
	}

	public BigDecimal getInterestAmount() {
		return interestAmount;
	}

	public void setInterestAmount(BigDecimal interestAmount) {
		this.interestAmount = interestAmount;
	}

	public BigDecimal getPreMatureFine() {
		return preMatureFine;
	}

	public void setPreMatureFine(BigDecimal preMatureFine) {
		this.preMatureFine = preMatureFine;
	}

	public BigDecimal getPreMatureAmount() {
		return preMatureAmount;
	}

	public void setPreMatureAmount(BigDecimal preMatureAmount) {
		this.preMatureAmount = preMatureAmount;
	}

	public BigDecimal getPreMatureInterestAmount() {
		return preMatureInterestAmount;
	}

	public void setPreMatureInterestAmount(BigDecimal preMatureInterestAmount) {
		this.preMatureInterestAmount = preMatureInterestAmount;
	}

	public BigDecimal getPreMaturePeriodOfDeposit() {
		return preMaturePeriodOfDeposit;
	}

	public void setPreMaturePeriodOfDeposit(BigDecimal preMaturePeriodOfDeposit) {
		this.preMaturePeriodOfDeposit = preMaturePeriodOfDeposit;
	}

	public Date getPreMatureDate() {
		return preMatureDate;
	}

	public void setPreMatureDate(Date preMatureDate) {
		this.preMatureDate = preMatureDate;
	}

	public double getPreMatureRateOfInterest() {
		return preMatureRateOfInterest;
	}

	public void setPreMatureRateOfInterest(double preMatureRateOfInterest) {
		this.preMatureRateOfInterest = preMatureRateOfInterest;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
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

	public Date getAccountClosedOn() {
		return accountClosedOn;
	}

	public void setAccountClosedOn(Date accountClosedOn) {
		this.accountClosedOn = accountClosedOn;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getPeriodOfName() {
		return periodOfName;
	}

	public void setPeriodOfName(String periodOfName) {
		this.periodOfName = periodOfName;
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