package com.dfq.coeffi.cbs.member.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
public class NumberFormat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long applicationNumber;

    private long memberNumber;

    private String type;

    private String prefix;

    private long paymentNumber;

    private long receiptNumber;

    private long boardMeetingNumber;

    private long shareCertificateNumber;

    private boolean active;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(long applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public long getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(long memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public long getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(long receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public long getBoardMeetingNumber() {
		return boardMeetingNumber;
	}

	public void setBoardMeetingNumber(long boardMeetingNumber) {
		this.boardMeetingNumber = boardMeetingNumber;
	}

	public long getShareCertificateNumber() {
		return shareCertificateNumber;
	}

	public void setShareCertificateNumber(long shareCertificateNumber) {
		this.shareCertificateNumber = shareCertificateNumber;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
    
    
    
    
    
    
    
    
    
}