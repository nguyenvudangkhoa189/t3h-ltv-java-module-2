package vn.demo.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import vn.demo.dto.ProductListResponse;
import vn.demo.dto.ProductRequest;
import vn.demo.model.Category;
import vn.demo.model.Product;

@Service
@RequiredArgsConstructor
public class ProductService {

	private static final String BASE_URL = "https://dummyjson.com/products";

	private final RestTemplate restTemplate;

	public ProductListResponse getAllProducts() {
		return fetchProductList(BASE_URL);
	}

	public Product getProductById(long id) {
		try {
			return restTemplate.getForObject(BASE_URL + "/" + id, Product.class);
		} catch (RestClientException ex) {
			return null;
		}
	}

	public ProductListResponse searchProducts(String keyword) {
		String url = UriComponentsBuilder.fromUriString(BASE_URL + "/search")
				.queryParam("q", keyword)
				.toUriString();
		return fetchProductList(url);
	}

	public List<Category> getCategories() {
		try {
			Category[] categories = restTemplate.getForObject(BASE_URL + "/categories", Category[].class);
			if (categories == null) {
				return Collections.emptyList();
			}
			return Arrays.asList(categories);
		} catch (RestClientException ex) {
			return Collections.emptyList();
		}
	}

	public Product addProduct(ProductRequest request) {
		HttpEntity<ProductRequest> entity = jsonEntity(request);
		return restTemplate.postForObject(BASE_URL + "/add", entity, Product.class);
	}

	public Product updateProduct(long id, ProductRequest request) {
		HttpEntity<ProductRequest> entity = jsonEntity(request);
		return restTemplate
				.exchange(BASE_URL + "/" + id, HttpMethod.PUT, entity, Product.class)
				.getBody();
	}

	private ProductListResponse fetchProductList(String url) {
		try {
			ProductListResponse response = restTemplate.getForObject(url, ProductListResponse.class);
			if (response == null) {
				return emptyProductList();
			}
			if (response.getProducts() == null) {
				response.setProducts(Collections.emptyList());
			}
			if (response.getTotal() == null) {
				response.setTotal(response.getProducts().size());
			}
			return response;
		} catch (RestClientException ex) {
			return emptyProductList();
		}
	}

	private ProductListResponse emptyProductList() {
		ProductListResponse response = new ProductListResponse();
		response.setProducts(Collections.emptyList());
		response.setTotal(0);
		return response;
	}

	private HttpEntity<ProductRequest> jsonEntity(ProductRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(request, headers);
	}

}
