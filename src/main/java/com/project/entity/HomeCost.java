package com.project.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "homeCost")
public class HomeCost {

	@Id
	@Column(name = "sno")
	private Long sno;

	@Column(name = "item")
	@NotBlank(message = "Item is required")
	private String item;

	@Column(name = "cost", precision = 10, scale = 2)
	@NotNull(message = "Cost is required")
	@DecimalMin(value = "0.01", inclusive = true, message = "Cost must be greater than 0")
	private BigDecimal cost;

	@Column(name = "date")
	@NotNull(message = "Date is required")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	@Column(name = "username") // 👈 this links HomeCost to a user
	private String username;

	// Define Constructor
	public HomeCost() {

	}

	public HomeCost(String item, BigDecimal cost, Date date, String username) {
		this.item = item;
		this.cost = cost;
		this.date = date;
		this.username = username;
	}

	public Long getSno() {
		return sno;
	}

	public void setSno(Long sno) {
		this.sno = sno;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
