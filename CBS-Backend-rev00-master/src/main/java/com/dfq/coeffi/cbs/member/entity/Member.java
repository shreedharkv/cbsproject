package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.master.entity.accounthead.AccountHead;
import com.dfq.coeffi.cbs.master.entity.accounthead.Ledger;
import com.dfq.coeffi.cbs.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String applicationNumber;

    private String memberNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date applicationDate;

    private Date dateOfBirth;

    private String organisationCode;

    private String organisationName;

    private String title;

    private String name;

    private String occupationCode;

    private String gender;

    private String age;

    private BigDecimal sharesApplied;

    private BigDecimal sharesValue;

    private Boolean status;

    private Date approvedOn;

    private Boolean approvedStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_type", nullable = false)
    @NotNull
    private MemberType memberType;

    @OneToOne(cascade = CascadeType.ALL)
    private MemberPersonalDetail memberPersonalDetail;

    @OneToOne(cascade = CascadeType.ALL)
    private MemberIntroducersDetails memberIntroducersDetail;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "member")
    private List<MemberFamilyDetails> memberFamilyDetails;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    private List<MemberNomineeDetails> memberNomineeDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL)
    private User approvedBy;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<MemberFee> memberFees;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<AdditionalShare> additionalShares;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<RefundShare> refundShares;

    private String existingMemberNumber;

    private Boolean membershipWithdrawStatus;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Document> documents;

    private String personOneFullName;
    private String personTwoFullName;

    private String personOneTitle;
    private String personOneName;
    private String personOneGender;
    private String personOneAge;
    private String personOneOccupationCode;

    private String personTwoTitle;
    private String personTwoName;
    private String personTwoGender;
    private String personTwoAge;
    private String personTwoOccupationCode;

    @OneToOne(cascade = CascadeType.ALL)
    private BoardMeeting boardMeeting;

    @OneToOne(cascade = CascadeType.ALL)
    private AccountHead accountHead;

    @OneToOne(cascade = CascadeType.ALL)
    private Ledger ledger;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(String memberNumber) {
		this.memberNumber = memberNumber;
	}

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getOrganisationCode() {
		return organisationCode;
	}

	public void setOrganisationCode(String organisationCode) {
		this.organisationCode = organisationCode;
	}

	public String getOrganisationName() {
		return organisationName;
	}

	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public BigDecimal getSharesApplied() {
		return sharesApplied;
	}

	public void setSharesApplied(BigDecimal sharesApplied) {
		this.sharesApplied = sharesApplied;
	}

	public BigDecimal getSharesValue() {
		return sharesValue;
	}

	public void setSharesValue(BigDecimal sharesValue) {
		this.sharesValue = sharesValue;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Date getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(Date approvedOn) {
		this.approvedOn = approvedOn;
	}

	public Boolean getApprovedStatus() {
		return approvedStatus;
	}

	public void setApprovedStatus(Boolean approvedStatus) {
		this.approvedStatus = approvedStatus;
	}

	public MemberType getMemberType() {
		return memberType;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}

	public MemberPersonalDetail getMemberPersonalDetail() {
		return memberPersonalDetail;
	}

	public void setMemberPersonalDetail(MemberPersonalDetail memberPersonalDetail) {
		this.memberPersonalDetail = memberPersonalDetail;
	}

	public MemberIntroducersDetails getMemberIntroducersDetail() {
		return memberIntroducersDetail;
	}

	public void setMemberIntroducersDetail(MemberIntroducersDetails memberIntroducersDetail) {
		this.memberIntroducersDetail = memberIntroducersDetail;
	}

	public List<MemberFamilyDetails> getMemberFamilyDetails() {
		return memberFamilyDetails;
	}

	public void setMemberFamilyDetails(List<MemberFamilyDetails> memberFamilyDetails) {
		this.memberFamilyDetails = memberFamilyDetails;
	}

	public List<MemberNomineeDetails> getMemberNomineeDetails() {
		return memberNomineeDetails;
	}

	public void setMemberNomineeDetails(List<MemberNomineeDetails> memberNomineeDetails) {
		this.memberNomineeDetails = memberNomineeDetails;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public User getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	public List<MemberFee> getMemberFees() {
		return memberFees;
	}

	public void setMemberFees(List<MemberFee> memberFees) {
		this.memberFees = memberFees;
	}

	public List<AdditionalShare> getAdditionalShares() {
		return additionalShares;
	}

	public void setAdditionalShares(List<AdditionalShare> additionalShares) {
		this.additionalShares = additionalShares;
	}

	public List<RefundShare> getRefundShares() {
		return refundShares;
	}

	public void setRefundShares(List<RefundShare> refundShares) {
		this.refundShares = refundShares;
	}

	public String getExistingMemberNumber() {
		return existingMemberNumber;
	}

	public void setExistingMemberNumber(String existingMemberNumber) {
		this.existingMemberNumber = existingMemberNumber;
	}

	public Boolean getMembershipWithdrawStatus() {
		return membershipWithdrawStatus;
	}

	public void setMembershipWithdrawStatus(Boolean membershipWithdrawStatus) {
		this.membershipWithdrawStatus = membershipWithdrawStatus;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public String getPersonOneFullName() {
		return personOneFullName;
	}

	public void setPersonOneFullName(String personOneFullName) {
		this.personOneFullName = personOneFullName;
	}

	public String getPersonTwoFullName() {
		return personTwoFullName;
	}

	public void setPersonTwoFullName(String personTwoFullName) {
		this.personTwoFullName = personTwoFullName;
	}

	public String getPersonOneTitle() {
		return personOneTitle;
	}

	public void setPersonOneTitle(String personOneTitle) {
		this.personOneTitle = personOneTitle;
	}

	public String getPersonOneName() {
		return personOneName;
	}

	public void setPersonOneName(String personOneName) {
		this.personOneName = personOneName;
	}

	public String getPersonOneGender() {
		return personOneGender;
	}

	public void setPersonOneGender(String personOneGender) {
		this.personOneGender = personOneGender;
	}

	public String getPersonOneAge() {
		return personOneAge;
	}

	public void setPersonOneAge(String personOneAge) {
		this.personOneAge = personOneAge;
	}

	public String getPersonOneOccupationCode() {
		return personOneOccupationCode;
	}

	public void setPersonOneOccupationCode(String personOneOccupationCode) {
		this.personOneOccupationCode = personOneOccupationCode;
	}

	public String getPersonTwoTitle() {
		return personTwoTitle;
	}

	public void setPersonTwoTitle(String personTwoTitle) {
		this.personTwoTitle = personTwoTitle;
	}

	public String getPersonTwoName() {
		return personTwoName;
	}

	public void setPersonTwoName(String personTwoName) {
		this.personTwoName = personTwoName;
	}

	public String getPersonTwoGender() {
		return personTwoGender;
	}

	public void setPersonTwoGender(String personTwoGender) {
		this.personTwoGender = personTwoGender;
	}

	public String getPersonTwoAge() {
		return personTwoAge;
	}

	public void setPersonTwoAge(String personTwoAge) {
		this.personTwoAge = personTwoAge;
	}

	public String getPersonTwoOccupationCode() {
		return personTwoOccupationCode;
	}

	public void setPersonTwoOccupationCode(String personTwoOccupationCode) {
		this.personTwoOccupationCode = personTwoOccupationCode;
	}

	public BoardMeeting getBoardMeeting() {
		return boardMeeting;
	}

	public void setBoardMeeting(BoardMeeting boardMeeting) {
		this.boardMeeting = boardMeeting;
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
    
    
    
    
    
    
    
    
    
}