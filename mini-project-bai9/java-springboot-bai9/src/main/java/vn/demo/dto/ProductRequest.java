package vn.demo.dto;

import lombok.Data;

@Data
public class ProductRequest {

	private String title;
	private String description;
	private Double price;
	private String category;
	private String thumbnail;
	private String brand;

}
