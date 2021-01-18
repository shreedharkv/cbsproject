package com.dfq.coeffi.cbs.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
public class MemberNomineeDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String relationWithMember;

    private String name;

    private String residentiaAddress;

    private String village;

    private String taluka;

    private String district;

    private String phoneNumber;

    private String age;

    private String pinCode;

    private Boolean minor;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(cascade = CascadeType.ALL)
    private MemberGuardianDetails memberGuardianDetails;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRelationWithMember() {
		return relationWithMember;
	}

	public void setRelationWithMember(String relationWithMember) {
		this.relationWithMember = relationWithMember;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public Boolean getMinor() {
		return minor;
	}

	public void setMinor(Boolean minor) {
		this.minor = minor;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public MemberGuardianDetails getMemberGuardianDetails() {
		return memberGuardianDetails;
	}

	public void setMemberGuardianDetails(MemberGuardianDetails memberGuardianDetails) {
		this.memberGuardianDetails = memberGuardianDetails;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
}