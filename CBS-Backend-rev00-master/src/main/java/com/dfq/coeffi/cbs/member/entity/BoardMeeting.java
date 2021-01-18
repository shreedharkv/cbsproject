package com.dfq.coeffi.cbs.member.entity;

import com.dfq.coeffi.cbs.loan.entity.loan.Loan;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
public class BoardMeeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long meetingNumber;

    private String meetingType;

    private String title;

    @Column(length = 1500)
    private String agenda;

    @Temporal(TemporalType.DATE)
    private Date meetingDate;

    @Column(length = 1500)
    private String applicationNumbers;

    @Column(length = 1500)
    private String minutesOfMeeting;

    private String boardMeetingNumber;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Member> boardAttendedMembers;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Member> memberApproval;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Loan> loanApplications;

    @OneToOne(cascade = CascadeType.ALL)
    private BoardOfDirectors boardOfDirectors;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date credtedOn;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMeetingNumber() {
		return meetingNumber;
	}

	public void setMeetingNumber(long meetingNumber) {
		this.meetingNumber = meetingNumber;
	}

	public String getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(String meetingType) {
		this.meetingType = meetingType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(Date meetingDate) {
		this.meetingDate = meetingDate;
	}

	public String getApplicationNumbers() {
		return applicationNumbers;
	}

	public void setApplicationNumbers(String applicationNumbers) {
		this.applicationNumbers = applicationNumbers;
	}

	public String getMinutesOfMeeting() {
		return minutesOfMeeting;
	}

	public void setMinutesOfMeeting(String minutesOfMeeting) {
		this.minutesOfMeeting = minutesOfMeeting;
	}

	public String getBoardMeetingNumber() {
		return boardMeetingNumber;
	}

	public void setBoardMeetingNumber(String boardMeetingNumber) {
		this.boardMeetingNumber = boardMeetingNumber;
	}

	public List<Member> getBoardAttendedMembers() {
		return boardAttendedMembers;
	}

	public void setBoardAttendedMembers(List<Member> boardAttendedMembers) {
		this.boardAttendedMembers = boardAttendedMembers;
	}

	public List<Member> getMemberApproval() {
		return memberApproval;
	}

	public void setMemberApproval(List<Member> memberApproval) {
		this.memberApproval = memberApproval;
	}

	public List<Loan> getLoanApplications() {
		return loanApplications;
	}

	public void setLoanApplications(List<Loan> loanApplications) {
		this.loanApplications = loanApplications;
	}

	public BoardOfDirectors getBoardOfDirectors() {
		return boardOfDirectors;
	}

	public void setBoardOfDirectors(BoardOfDirectors boardOfDirectors) {
		this.boardOfDirectors = boardOfDirectors;
	}

	public Date getCredtedOn() {
		return credtedOn;
	}

	public void setCredtedOn(Date credtedOn) {
		this.credtedOn = credtedOn;
	}
    
    
    
    
    
    
    
    
    
    
}