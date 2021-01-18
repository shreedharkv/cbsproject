package com.dfq.coeffi.cbs.customer.entity;

import com.dfq.coeffi.cbs.member.entity.Member;
import com.dfq.coeffi.cbs.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class CustomerIntroducersDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String address;

    private String village;

    private String taluka;

    private String district;

    private String phoneNumber;

    private String age;

    private String occupationCode;

    @OneToOne(cascade = CascadeType.ALL)
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    private String introducerType;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIntroducerType() {
		return introducerType;
	}

	public void setIntroducerType(String introducerType) {
		this.introducerType = introducerType;
	}
    
    
    
    
    
    
}