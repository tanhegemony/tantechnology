package edu.poly.thtechnology.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto{
	private Long orderDetailID;
	private int quantity;
	private double unitPrice;
	private double invoiceTotal;
}
