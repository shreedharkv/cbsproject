package com.dfq.coeffi.cbs.loan.entity.loan;


import com.dfq.coeffi.cbs.customer.entity.Customer;
import com.dfq.coeffi.cbs.document.Document;
import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Model class which contains term loan information like loan security
 * property details, vehicl details etc.
 *
 * @see com.dfq.coeffi.cbs.loan.entity.loan.TermDetails
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Entity
@Setter
@Getter
public class TermDetails {

    @Id
    @GeneratedValue
    private long id;

    private String tdrType;

    private String suretyType;

    private String propertyType;

    @OneToOne
    private Customer staff;

    @OneToOne
    private Member member;

    private long userId;

    @Column(name = "member_ref_id")
    private long memberId;

    private String measurement;

    private String address;

    private String surveyNumber;

    private String siteRegistrationDate;

    private String propertyHolderName;

    private BigDecimal estimationAmount;

    private String vehicleName;

    private String vehicleModel;

    private String vehicleDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private Document termLoanDocument;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTdrType() {
		return tdrType;
	}

	public void setTdrType(String tdrType) {
		this.tdrType = tdrType;
	}

	public String getSuretyType() {
		return suretyType;
	}

	public void setSuretyType(String suretyType) {
		this.suretyType = suretyType;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public Customer getStaff() {
		return staff;
	}

	public void setStaff(Customer staff) {
		this.staff = staff;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSurveyNumber() {
		return surveyNumber;
	}

	public void setSurveyNumber(String surveyNumber) {
		this.surveyNumber = surveyNumber;
	}

	public String getSiteRegistrationDate() {
		return siteRegistrationDate;
	}

	public void setSiteRegistrationDate(String siteRegistrationDate) {
		this.siteRegistrationDate = siteRegistrationDate;
	}

	public String getPropertyHolderName() {
		return propertyHolderName;
	}

	public void setPropertyHolderName(String propertyHolderName) {
		this.propertyHolderName = propertyHolderName;
	}

	public BigDecimal getEstimationAmount() {
		return estimationAmount;
	}

	public void setEstimationAmount(BigDecimal estimationAmount) {
		this.estimationAmount = estimationAmount;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getVehicleDetails() {
		return vehicleDetails;
	}

	public void setVehicleDetails(String vehicleDetails) {
		this.vehicleDetails = vehicleDetails;
	}

	public Document getTermLoanDocument() {
		return termLoanDocument;
	}

	public void setTermLoanDocument(Document termLoanDocument) {
		this.termLoanDocument = termLoanDocument;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
