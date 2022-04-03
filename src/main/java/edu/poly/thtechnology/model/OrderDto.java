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
public class OrderDto{
	private Long orderID;
	private Date orderDate;
	private double amount;
	@NotEmpty
	@Length(min = 3)
	private String status;
}
