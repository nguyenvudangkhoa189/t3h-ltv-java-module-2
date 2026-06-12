package vn.demo.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import vn.demo.model.Product;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListResponse {

	private List<Product> products = Collections.emptyList();
	private Integer total = 0;
	private Integer skip = 0;
	private Integer limit = 0;

}
