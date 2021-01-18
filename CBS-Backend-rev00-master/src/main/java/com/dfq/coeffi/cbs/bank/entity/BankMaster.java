package com.dfq.coeffi.cbs.bank.entity;

import com.dfq.coeffi.cbs.document.Document;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BankMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String bankCode;
    private String bankName;
    private String sortNumber;
    private String address;
    private String village;
    private String taluk;
    private String district;
    private String pinCode;
    private String phoneNumber;
    private String ifscCode;
    private String branchName;

    private boolean active;

    @OneToOne(cascade = CascadeType.ALL)
    private Document documentLogo;

    @OneToOne(cascade = CascadeType.ALL)
    private Document documentImage;

    private String accountNumber;

    private BigDecimal balance;

    private String shareAccountNumber;

    private BigDecimal shareBalance;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(String sortNumber) {
		this.sortNumber = sortNumber;
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

	public String getTaluk() {
		return taluk;
	}

	public void setTaluk(String taluk) {
		this.taluk = taluk;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Document getDocumentLogo() {
		return documentLogo;
	}

	public void setDocumentLogo(Document documentLogo) {
		this.documentLogo = documentLogo;
	}

	public Document getDocumentImage() {
		return documentImage;
	}

	public void setDocumentImage(Document documentImage) {
		this.documentImage = documentImage;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getShareAccountNumber() {
		return shareAccountNumber;
	}

	public void setShareAccountNumber(String shareAccountNumber) {
		this.shareAccountNumber = shareAccountNumber;
	}

	public BigDecimal getShareBalance() {
		return shareBalance;
	}

	public void setShareBalance(BigDecimal shareBalance) {
		this.shareBalance = shareBalance;
	}
    
    
    
    
    
}