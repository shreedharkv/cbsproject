package com.dfq.coeffi.cbs.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
public class PersonnelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

    @Column(length=4)
    private String age;

    @Column(length = 15)
    private String bloodGroup;

    @Column(length = 20)
    private String religion;

    @Column(length = 20)
    private String caste;

    @Column(length = 20)
    private String fatherName;

    @Column(length = 20)
    private String motherName;

    @Column(length = 20)
    private String motherTongue;

    @Column(length=10)
    private String maritalStatus;

    @Column(length=25)
    private String wifeName;

    @Column(length=25)
    private String adharNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(String caste) {
		this.caste = caste;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getMotherTongue() {
		return motherTongue;
	}

	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getWifeName() {
		return wifeName;
	}

	public void setWifeName(String wifeName) {
		this.wifeName = wifeName;
	}

	public String getAdharNumber() {
		return adharNumber;
	}

	public void setAdharNumber(String adharNumber) {
		this.adharNumber = adharNumber;
	}
    
    
    
    
    
    
    
    
    
    
    
}