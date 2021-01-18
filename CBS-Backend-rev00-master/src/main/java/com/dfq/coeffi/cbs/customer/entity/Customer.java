package com.dfq.coeffi.cbs.customer.entity;

import com.dfq.coeffi.cbs.document.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String applicationNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date applicationDate;

    private Date dateOfBirth;

    private String organisationCode;

    private String casteCode;

    private String title;

    private String name;

    private String occupationCode;

    private String gender;

    private String age;

    private Boolean status;

    @OneToOne(cascade = CascadeType.ALL)
    private CustomerPersonalDetails customerPersonalDetails;

    @OneToOne(cascade = CascadeType.ALL)
    private CustomerIntroducersDetails introducersDetails;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "customer")
    private List<CustomerFamilyDetails> familyMemberDetails;

    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    private List<CustomerNomineeDatails> nomineeDatails;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Document> documents;

    @Transient
    private List<Long> documentIds;

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

	public String getCasteCode() {
		return casteCode;
	}

	public void setCasteCode(String casteCode) {
		this.casteCode = casteCode;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public CustomerPersonalDetails getCustomerPersonalDetails() {
		return customerPersonalDetails;
	}

	public void setCustomerPersonalDetails(CustomerPersonalDetails customerPersonalDetails) {
		this.customerPersonalDetails = customerPersonalDetails;
	}

	public CustomerIntroducersDetails getIntroducersDetails() {
		return introducersDetails;
	}

	public void setIntroducersDetails(CustomerIntroducersDetails introducersDetails) {
		this.introducersDetails = introducersDetails;
	}

	public List<CustomerFamilyDetails> getFamilyMemberDetails() {
		return familyMemberDetails;
	}

	public void setFamilyMemberDetails(List<CustomerFamilyDetails> familyMemberDetails) {
		this.familyMemberDetails = familyMemberDetails;
	}

	public List<CustomerNomineeDatails> getNomineeDatails() {
		return nomineeDatails;
	}

	public void setNomineeDatails(List<CustomerNomineeDatails> nomineeDatails) {
		this.nomineeDatails = nomineeDatails;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<Long> getDocumentIds() {
		return documentIds;
	}

	public void setDocumentIds(List<Long> documentIds) {
		this.documentIds = documentIds;
	}
    
    
    
    
    
    
}