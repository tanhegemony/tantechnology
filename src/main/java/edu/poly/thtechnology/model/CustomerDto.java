package edu.poly.thtechnology.model;

import java.util.Date;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto{
	private Long customerID;
	@NotEmpty
	@Length(min = 6)
	private String customerName;
	private String email;
	@NotEmpty
	@Length(min = 6)
	private String password;
	@NotEmpty
	@Length(min = 11, max = 11)
	private String phone;
	private Date registeredDate;
	@NotEmpty
	@Length(min = 3)
	private String status;
}
