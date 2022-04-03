package edu.poly.thtechnology.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productID;
	
	@Column(columnDefinition = "nvarchar(100) not null")
	private String name;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column(nullable = false)
	private double unitPrice;
	
	@Column(length = 200)
	private String image;
	
	@Column(columnDefinition = "nvarchar(500) not null")
	private String description1;
	@Column(columnDefinition = "nvarchar(500) not null")
	private String description2;
	
	@Column(nullable = false)
	private double discount;
	
	@Column(columnDefinition = "nvarchar(20) not null")
	private String status;
	
	@Temporal(TemporalType.DATE)
	private Date enteredDate;
	
	@Column(nullable = false)
	private double price;
	
	@Column(nullable = false)
	private String deviceName;
	
//	mối quan hệ với bảng category 
	@ManyToOne
	@JoinColumn(name = "categoryID")
	private Category category;
	
	// khi xóa product thì các thông tin liên quan product sẽ bị xóa  nên dùng cascade
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private Set<OrderDetail> orderDetails;
	
//	@ManyToOne
//	@JoinColumn(name = "cartItemID")
//	private CartItem cartItem;
}
