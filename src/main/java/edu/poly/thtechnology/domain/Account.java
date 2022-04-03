package edu.poly.thtechnology.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account implements Serializable{
	@Id
	@Column(length = 30, unique = true)
	private String username;
	
	@Column(length = 100, nullable = false)
	private String password;
	
	@Column(length = 100, columnDefinition = "nvarchar(150) not null")
	private String fullname;
	
	@Column(length = 100)
	private String email;
	
	@Column(length = 10)
	private String phoneNumber;
	
	@Column()
	private boolean isAdmin = false;
}
