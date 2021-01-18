package com.dfq.coeffi.cbs.transaction.entity;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundTransferDto {

    public long loanAccountNumber;

	public long getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(long loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}
    
    
    
    

}
