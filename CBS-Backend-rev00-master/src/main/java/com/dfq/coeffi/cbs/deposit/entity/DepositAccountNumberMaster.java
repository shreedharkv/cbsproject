package com.dfq.coeffi.cbs.deposit.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class DepositAccountNumberMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String accountNumberCD;

    private String accountNumberFD;

    private String accountNumberRD;

    private String accountNumberADS;

    private String accountNumberSB;

    private String accountNumberTDR;

    private String accountNumberPG;

    private String accountNumberCA;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNumberCD() {
		return accountNumberCD;
	}

	public void setAccountNumberCD(String accountNumberCD) {
		this.accountNumberCD = accountNumberCD;
	}

	public String getAccountNumberFD() {
		return accountNumberFD;
	}

	public void setAccountNumberFD(String accountNumberFD) {
		this.accountNumberFD = accountNumberFD;
	}

	public String getAccountNumberRD() {
		return accountNumberRD;
	}

	public void setAccountNumberRD(String accountNumberRD) {
		this.accountNumberRD = accountNumberRD;
	}

	public String getAccountNumberADS() {
		return accountNumberADS;
	}

	public void setAccountNumberADS(String accountNumberADS) {
		this.accountNumberADS = accountNumberADS;
	}

	public String getAccountNumberSB() {
		return accountNumberSB;
	}

	public void setAccountNumberSB(String accountNumberSB) {
		this.accountNumberSB = accountNumberSB;
	}

	public String getAccountNumberTDR() {
		return accountNumberTDR;
	}

	public void setAccountNumberTDR(String accountNumberTDR) {
		this.accountNumberTDR = accountNumberTDR;
	}

	public String getAccountNumberPG() {
		return accountNumberPG;
	}

	public void setAccountNumberPG(String accountNumberPG) {
		this.accountNumberPG = accountNumberPG;
	}

	public String getAccountNumberCA() {
		return accountNumberCA;
	}

	public void setAccountNumberCA(String accountNumberCA) {
		this.accountNumberCA = accountNumberCA;
	}
    
    
    
    
    
    
    
    
    
    


}
