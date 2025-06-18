package com.sinosoft.testapp.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductModel {

	private String productNo;

	private String productName;

	private BigDecimal price;

	private String status;

}
