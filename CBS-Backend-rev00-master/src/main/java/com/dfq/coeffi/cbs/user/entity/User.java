package com.dfq.coeffi.cbs.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 45)
    private String firstName;

    @Column(length = 45)
    private String lastName;

    @Column(length = 45)
    private String middleName;

    @Column(length = 10)
    private String userCode;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date dateOfJoining;

    @Column(length = 10)
    private BigDecimal offeredSalary;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String password;

    @Column(length = 10)
    private String gender;

    @Column(length = 30)
    private String jobTitle;

    @Column
    private Boolean active;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Role> roles;

    @OneToOne(cascade = CascadeType.ALL)
    private UserAddress userAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private ContactInformation contactInformation;

    @OneToOne(cascade = CascadeType.ALL)
    private PersonnelDetails personnelDetails;

    private long roleId;

    public User() {
    }

    public User(User user) {
        this.active = user.active;
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.roles = user.roles;
        this.email = user.email;
        this.password = user.password;
        this.id = user.id;
        this.userCode = user.userCode;
        this.gender = user.gender;
        this.dateOfJoining = user.dateOfJoining;
        this.offeredSalary = user.offeredSalary;
        this.userAddress = user.userAddress;
        this.contactInformation = user.contactInformation;
        this.personnelDetails = user.personnelDetails;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public BigDecimal getOfferedSalary() {
		return offeredSalary;
	}

	public void setOfferedSalary(BigDecimal offeredSalary) {
		this.offeredSalary = offeredSalary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public UserAddress getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(UserAddress userAddress) {
		this.userAddress = userAddress;
	}

	public ContactInformation getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(ContactInformation contactInformation) {
		this.contactInformation = contactInformation;
	}

	public PersonnelDetails getPersonnelDetails() {
		return personnelDetails;
	}

	public void setPersonnelDetails(PersonnelDetails personnelDetails) {
		this.personnelDetails = personnelDetails;
	}

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	
}