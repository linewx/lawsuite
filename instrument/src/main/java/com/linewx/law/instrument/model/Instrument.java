package com.linewx.law.instrument.model;

import javax.persistence.*;
//import java.time.Instant;

@SuppressWarnings("serial")
@Entity
@Table(name = "Instrument")
public class Instrument implements java.io.Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String email;


    protected Instrument() {

	}
	
	public Instrument(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
