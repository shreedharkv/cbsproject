package com.dfq.coeffi.cbs.loan.entity.loan;

import com.dfq.coeffi.cbs.loan.entity.Guarantor;
import com.dfq.coeffi.cbs.loan.entity.LoanStatus;
import com.dfq.coeffi.cbs.loan.entity.LoanType;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Model class which contains the loan related information
 * like Account No, Balance, Loan Interest etc
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.Loan
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Setter
@Getter
@Entity
public class Loan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "application_no")
    private long applicationNumber;

//    @Column(name = "application_no")
//    private String applicationNumber;

    private String loanAccountNumber;

    private String exgLoanAccountNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_on")
    private Date createdOn;

    @Enumerated(EnumType.STRING)
    private LoanType loanType;

    @Embedded
    private LoanDetail loanDetail;

    @ManyToOne(cascade = CascadeType.ALL)
    private Guarantor guarantor;

    @Temporal(TemporalType.DATE)
    private Date submittedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    private User submittedBy;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    private LoanSchedule loanSchedule;

    @ManyToOne(cascade = CascadeType.ALL)
    private LoanDisbursement loanDisbursement;

    private boolean active;

    @Temporal(TemporalType.DATE)
    private Date rejectedOnDate;

    @ManyToOne(fetch=FetchType.LAZY)
    private User rejectedBy;

    @OneToOne(cascade = CascadeType.ALL)
    private Member member;

    private long serialNo;

    private long bdrNo;

    private String processRemark;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Repayment> repayments;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<LoanOtherCharges> loanOtherCharges;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(long applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(String loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}

	public String getExgLoanAccountNumber() {
		return exgLoanAccountNumber;
	}

	public void setExgLoanAccountNumber(String exgLoanAccountNumber) {
		this.exgLoanAccountNumber = exgLoanAccountNumber;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public LoanType getLoanType() {
		return loanType;
	}

	public void setLoanType(LoanType loanType) {
		this.loanType = loanType;
	}

	public LoanDetail getLoanDetail() {
		return loanDetail;
	}

	public void setLoanDetail(LoanDetail loanDetail) {
		this.loanDetail = loanDetail;
	}

	public Guarantor getGuarantor() {
		return guarantor;
	}

	public void setGuarantor(Guarantor guarantor) {
		this.guarantor = guarantor;
	}

	public Date getSubmittedOn() {
		return submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	public User getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	public LoanStatus getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

	public LoanSchedule getLoanSchedule() {
		return loanSchedule;
	}

	public void setLoanSchedule(LoanSchedule loanSchedule) {
		this.loanSchedule = loanSchedule;
	}

	public LoanDisbursement getLoanDisbursement() {
		return loanDisbursement;
	}

	public void setLoanDisbursement(LoanDisbursement loanDisbursement) {
		this.loanDisbursement = loanDisbursement;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getRejectedOnDate() {
		return rejectedOnDate;
	}

	public void setRejectedOnDate(Date rejectedOnDate) {
		this.rejectedOnDate = rejectedOnDate;
	}

	public User getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(User rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public long getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(long serialNo) {
		this.serialNo = serialNo;
	}

	public long getBdrNo() {
		return bdrNo;
	}

	public void setBdrNo(long bdrNo) {
		this.bdrNo = bdrNo;
	}

	public String getProcessRemark() {
		return processRemark;
	}

	public void setProcessRemark(String processRemark) {
		this.processRemark = processRemark;
	}

	public List<Repayment> getRepayments() {
		return repayments;
	}

	public void setRepayments(List<Repayment> repayments) {
		this.repayments = repayments;
	}

	public List<LoanOtherCharges> getLoanOtherCharges() {
		return loanOtherCharges;
	}

	public void setLoanOtherCharges(List<LoanOtherCharges> loanOtherCharges) {
		this.loanOtherCharges = loanOtherCharges;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}