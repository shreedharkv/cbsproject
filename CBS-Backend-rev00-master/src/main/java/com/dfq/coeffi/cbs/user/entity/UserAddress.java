package com.dfq.coeffi.cbs.user.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 25)
    private String areaName;

    @Column(length = 10)
    private String areaPinCode;

    @Column(length = 25)
    private String houseNumber;

    @Column(length = 60)
    private String addressLine;

    @Column(length = 25)
    private String landmark;

    @Column(length = 45)
    private String city;

    @Column(length = 45)
    private String state;

    @Column(length = 35)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    @NotNull
    private AddressType addressType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaPinCode() {
		return areaPinCode;
	}

	public void setAreaPinCode(String areaPinCode) {
		this.areaPinCode = areaPinCode;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}
    
    
    
    
    
    
    
    
    
    
    
    
}