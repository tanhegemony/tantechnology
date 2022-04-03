package edu.poly.thtechnology.model;


import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto{
	@NotEmpty
	@Length(min = 6)
	@NonNull
	private String username;
	
	@NotEmpty
	@Length(min = 6)
	private String password;
	
	@NotEmpty
	private String confirmPassword;
	
	@NotEmpty
	private String fullname;
	
	@NotEmpty
	private String email;
	
	@NotEmpty
	@Length(min = 10, max = 10)
	private String phoneNumber;
}
