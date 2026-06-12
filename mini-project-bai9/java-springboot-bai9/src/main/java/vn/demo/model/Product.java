package vn.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

	private Long id;
	private String title;
	private String description;
	private String category;
	private Double price;
	private Double discountPercentage;
	private Double rating;
	private Integer stock;
	private String brand;
	private String sku;
	private String thumbnail;
	private List<String> images;
	private String warrantyInformation;
	private String shippingInformation;
	private String availabilityStatus;
	private String returnPolicy;

}
