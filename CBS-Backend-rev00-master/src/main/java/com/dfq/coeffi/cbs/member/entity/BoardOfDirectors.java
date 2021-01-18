package com.dfq.coeffi.cbs.member.entity;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Setter
@Getter
public class BoardOfDirectors implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String president;

    private String vicePresident;

    private String director1;

    private String director2;

    private String director3;

    private String director4;

    private String director5;

    private String director6;

    private String director7;

    private String director8;

    private String director9;

    private String director10;

    private String director11;

    private String director12;

    private String director13;

    private String ceo;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPresident() {
		return president;
	}

	public void setPresident(String president) {
		this.president = president;
	}

	public String getVicePresident() {
		return vicePresident;
	}

	public void setVicePresident(String vicePresident) {
		this.vicePresident = vicePresident;
	}

	public String getDirector1() {
		return director1;
	}

	public void setDirector1(String director1) {
		this.director1 = director1;
	}

	public String getDirector2() {
		return director2;
	}

	public void setDirector2(String director2) {
		this.director2 = director2;
	}

	public String getDirector3() {
		return director3;
	}

	public void setDirector3(String director3) {
		this.director3 = director3;
	}

	public String getDirector4() {
		return director4;
	}

	public void setDirector4(String director4) {
		this.director4 = director4;
	}

	public String getDirector5() {
		return director5;
	}

	public void setDirector5(String director5) {
		this.director5 = director5;
	}

	public String getDirector6() {
		return director6;
	}

	public void setDirector6(String director6) {
		this.director6 = director6;
	}

	public String getDirector7() {
		return director7;
	}

	public void setDirector7(String director7) {
		this.director7 = director7;
	}

	public String getDirector8() {
		return director8;
	}

	public void setDirector8(String director8) {
		this.director8 = director8;
	}

	public String getDirector9() {
		return director9;
	}

	public void setDirector9(String director9) {
		this.director9 = director9;
	}

	public String getDirector10() {
		return director10;
	}

	public void setDirector10(String director10) {
		this.director10 = director10;
	}

	public String getDirector11() {
		return director11;
	}

	public void setDirector11(String director11) {
		this.director11 = director11;
	}

	public String getDirector12() {
		return director12;
	}

	public void setDirector12(String director12) {
		this.director12 = director12;
	}

	public String getDirector13() {
		return director13;
	}

	public void setDirector13(String director13) {
		this.director13 = director13;
	}

	public String getCeo() {
		return ceo;
	}

	public void setCeo(String ceo) {
		this.ceo = ceo;
	}
    
    
    
    
    
    
    
    
    
    
    
}