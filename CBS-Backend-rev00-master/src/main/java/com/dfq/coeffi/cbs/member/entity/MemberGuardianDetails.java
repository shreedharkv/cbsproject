package com.dfq.coeffi.cbs.member.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class MemberGuardianDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String residentiaAddress;

    private String village;

    private String taluka;

    private String district;

    private String phoneNumber;

    private String pinCode;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResidentiaAddress() {
		return residentiaAddress;
	}

	public void setResidentiaAddress(String residentiaAddress) {
		this.residentiaAddress = residentiaAddress;
	}

	public String getVillage() {
		return village;
	}

	public void setVillage(String village) {
		this.village = village;
	}

	public String getTaluka() {
		return taluka;
	}

	public void setTaluka(String taluka) {
		this.taluka = taluka;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
    
    
    
    
    
    
    
    
    
    
}