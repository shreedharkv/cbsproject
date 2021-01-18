package com.dfq.coeffi.cbs.loan.entity;


import com.dfq.coeffi.cbs.common.address.Address;
import com.dfq.coeffi.cbs.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Model class which contains guarantor details
 * and his relationship with the member.
 *
 * @see com.dfq.coeffi.cbs.loan.entity.Guarantor
 * @author Kapil Kumar
 * @since Feb-2019
 * @version 1.0
 */

@Setter
@Getter
@Entity
public class Guarantor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    private String title;

    private String relationship;

    private int age;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address residenceAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    private Address officeAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private Member member;

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Address getResidenceAddress() {
		return residenceAddress;
	}

	public void setResidenceAddress(Address residenceAddress) {
		this.residenceAddress = residenceAddress;
	}

	public Address getOfficeAddress() {
		return officeAddress;
	}

	public void setOfficeAddress(Address officeAddress) {
		this.officeAddress = officeAddress;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}