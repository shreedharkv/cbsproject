package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
public class RefundShare {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String memberNumber;

    private String title;

    private String memberName;

    private String exgMemberNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date transactionDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ShareRefundReason reasonForShareRefund;

    private BigDecimal availableNumberOfShares;

    private BigDecimal appliedNumberOfShares;

    private BigDecimal appliedRefundSharesAmount;

    private String debitAccountNumber;

    private String accountName;

    private String modeOfPayment;

    private String creditAccountNumber;

    private String receivedCash;

    private String chequeNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date chequeDate;

    private String remarks;

    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @Column(name = "customer_ref_id")
    private long customerId;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

    private String description;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public ShareRefundReason getReasonForShareRefund() {
		return reasonForShareRefund;
	}

	public void setReasonForShareRefund(ShareRefundReason reasonForShareRefund) {
		this.reasonForShareRefund = reasonForShareRefund;
	}

	public BigDecimal getAvailableNumberOfShares() {
		return availableNumberOfShares;
	}

	public void setAvailableNumberOfShares(BigDecimal availableNumberOfShares) {
		this.availableNumberOfShares = availableNumberOfShares;
	}

	public BigDecimal getAppliedNumberOfShares() {
		return appliedNumberOfShares;
	}

	public void setAppliedNumberOfShares(BigDecimal appliedNumberOfShares) {
		this.appliedNumberOfShares = appliedNumberOfShares;
	}

	public BigDecimal getAppliedRefundSharesAmount() {
		return appliedRefundSharesAmount;
	}

	public void setAppliedRefundSharesAmount(BigDecimal appliedRefundSharesAmount) {
		this.appliedRefundSharesAmount = appliedRefundSharesAmount;
	}

	public String getDebitAccountNumber() {
		return debitAccountNumber;
	}

	public void setDebitAccountNumber(String debitAccountNumber) {
		this.debitAccountNumber = debitAccountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getCreditAccountNumber() {
		return creditAccountNumber;
	}

	public void setCreditAccountNumber(String creditAccountNumber) {
		this.creditAccountNumber = creditAccountNumber;
	}

	public String getReceivedCash() {
		return receivedCash;
	}

	public void setReceivedCash(String receivedCash) {
		this.receivedCash = receivedCash;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    
    
    
    
    
    
    
    
    
    
}