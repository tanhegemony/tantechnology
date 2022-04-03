package edu.poly.thtechnology.model;

import java.util.Date;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto{
	private Long productID;
	
	@NotEmpty(message = "Name không được để trống")
	private String name;
	
	@Min(value = 1, message = "Quantity phải lớn hơn 0")
	private int quantity;
	
	@DecimalMin(message = "Discount phải là số lớn hơn 0", value = "0.01")
	private double unitPrice;
	
	private String image;
	private MultipartFile imageFile;
	
	@NotEmpty(message = "Description1 không được để trống")
	private String description1;
	@NotEmpty(message = "Description2 không được để trống")
	private String description2;
	
	@DecimalMin(message = "Discount phải nằm từ 0.00-100.00", value = "0.00")
	@DecimalMax(message = "Discount phải nằm từ 0.00-100.00", value = "100.00")
	private double discount;
	
	@NotEmpty
	@Length(min = 3)
	private String status;
	
	private Date enteredDate;
	
	@DecimalMin(message = "Price phải là số lớn hơn hoặc bằng 0", value = "0.00")
	private double price;
	
	@NotEmpty(message = "DeviceName không được để trống")
	private String deviceName;
	
	private Long categoryID;
	
	private Boolean isEdit = false;
}
