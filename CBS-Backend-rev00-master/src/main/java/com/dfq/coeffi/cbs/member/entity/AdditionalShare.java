package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class AdditionalShare {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String memberNumber;

    private String memberName;

    private String exgMemberNumber;

    private String currentSharesNumber;

    private BigDecimal currentSharesValue;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date appliedDate;

    private BigDecimal sharesApplied;

    private BigDecimal shareValue;

    private String modeOfPayment;

    private String accountNumber;

    private String accountName;

    private String chequeNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date chequeDate;

    private BigDecimal amount;

    private String totalAmount;

    private String totalNumberOfShares;

    private String remarks;

    private String receivedCash;

    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @Column(name = "customer_ref_id")
    private long customerId;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    private BigDecimal shareFee;

    private BigDecimal entranceFee;

    private BigDecimal otherFee;

    private String shareCertificateNumber;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getExgMemberNumber() {
		return exgMemberNumber;
	}

	public void setExgMemberNumber(String exgMemberNumber) {
		this.exgMemberNumber = exgMemberNumber;
	}

	public String getCurrentSharesNumber() {
		return currentSharesNumber;
	}

	public void setCurrentSharesNumber(String currentSharesNumber) {
		this.currentSharesNumber = currentSharesNumber;
	}

	public BigDecimal getCurrentSharesValue() {
		return currentSharesValue;
	}

	public void setCurrentSharesValue(BigDecimal currentSharesValue) {
		this.currentSharesValue = currentSharesValue;
	}

	public Date getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(Date appliedDate) {
		this.appliedDate = appliedDate;
	}

	public BigDecimal getSharesApplied() {
		return sharesApplied;
	}

	public void setSharesApplied(BigDecimal sharesApplied) {
		this.sharesApplied = sharesApplied;
	}

	public BigDecimal getShareValue() {
		return shareValue;
	}

	public void setShareValue(BigDecimal shareValue) {
		this.shareValue = shareValue;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTotalNumberOfShares() {
		return totalNumberOfShares;
	}

	public void setTotalNumberOfShares(String totalNumberOfShares) {
		this.totalNumberOfShares = totalNumberOfShares;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getReceivedCash() {
		return receivedCash;
	}

	public void setReceivedCash(String receivedCash) {
		this.receivedCash = receivedCash;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
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

	public BigDecimal getShareFee() {
		return shareFee;
	}

	public void setShareFee(BigDecimal shareFee) {
		this.shareFee = shareFee;
	}

	public BigDecimal getEntranceFee() {
		return entranceFee;
	}

	public void setEntranceFee(BigDecimal entranceFee) {
		this.entranceFee = entranceFee;
	}

	public BigDecimal getOtherFee() {
		return otherFee;
	}

	public void setOtherFee(BigDecimal otherFee) {
		this.otherFee = otherFee;
	}

	public String getShareCertificateNumber() {
		return shareCertificateNumber;
	}

	public void setShareCertificateNumber(String shareCertificateNumber) {
		this.shareCertificateNumber = shareCertificateNumber;
	}
    
    
    
    
    
}