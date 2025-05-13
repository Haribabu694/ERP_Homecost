package com.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="adustOrder")
public class AdjustOrder {
	
	@Id
	@Column(name = "item")
	@Size(max = 25, message="Item must be less than 25 chars")
	private String item;
	
	@Column(name = "warehouse")
	@NotBlank(message="Warehouse is required")
	private String warehouse;
	
	@Column(name = "bin")
	@NotEmpty(message="Bin is required")
	private String bin;
	
	@Column(name = "adjustquantity")
	@NotNull(message="AdjustQuantity is required")
	private int adjustQuantity;
	
	@Column(name="reason")
	private String Reason;
	
	@Column(name="units", length=9)
	@NotEmpty(message="Units is required")
	private String units;
	
	// Define Constructor
	public AdjustOrder() {
		
	}

	public AdjustOrder(String warehouse, String bin, int adjustQuantity, String reason, String units) {
		warehouse = warehouse;
		bin = bin;
		adjustQuantity = adjustQuantity;
		Reason = reason;
		units = units;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getBin() {
		return bin;
	}

	public void setBin(String bin) {
		this.bin = bin;
	}

	public int getAdjustQuantity() {
	    return adjustQuantity;
	}

	public void setAdjustQuantity(int adjustQuantity) {
	    this.adjustQuantity = adjustQuantity;
	}

	public String getReason() {
		return Reason;
	}

	public void setReason(String reason) {
		Reason = reason;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	

}
