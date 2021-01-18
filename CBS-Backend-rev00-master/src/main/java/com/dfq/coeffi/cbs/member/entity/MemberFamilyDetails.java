package com.dfq.coeffi.cbs.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Setter
@Getter
@Entity
public class MemberFamilyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    private String name;

    private String occupationCode;

    private String age;

    private String relation;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
    
    
    
    
    
    
    
    
    
    
}
