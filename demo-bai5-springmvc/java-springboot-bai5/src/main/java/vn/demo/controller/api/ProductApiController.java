package vn.demo.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.dto.ProductPatchRequest;
import vn.demo.dto.ProductRequest;

@RestController
@RequestMapping("/api/v1/products")
public class ProductApiController {

	@GetMapping
	public ResponseEntity<List<String>> getAllProducts() {
		List<String> productNames = new ArrayList<>();
		productNames.add("Samsung");
		productNames.add("iPhone");
		return new ResponseEntity<>(productNames, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<String> searchProducts(
			@RequestParam String category,
			@RequestParam(required = false) String brand,
			@RequestParam(defaultValue = "name") String sortBy) {
		System.out.println("Category: " + category);
		System.out.println("Brand: " + brand);
		System.out.println("Sort by: " + sortBy);

		String message = "Category: " + category + ", sortBy: " + sortBy;
		if (brand != null && !brand.isBlank()) {
			message += ", brand: " + brand;
		}
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getProductById(@PathVariable String id) {
		System.out.println("Id value: " + id);
		return new ResponseEntity<>("Product id: " + id, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Void> createProductFromForm(
			@RequestParam String name,
			@RequestParam(required = false) String price,
			@RequestParam(defaultValue = "yellow") String color) {
		System.out.println("Name: " + name);
		System.out.println("Price: " + price);
		System.out.println("Color: " + color);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/json")
	public ResponseEntity<ProductRequest> createProductFromBody(@RequestBody ProductRequest request) {
		System.out.println("Body data: " + request.getName() + ", " + request.getPrice());
		return ResponseEntity.status(HttpStatus.CREATED).body(request);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ProductPatchRequest> patchProduct(
			@PathVariable Long id,
			@RequestBody ProductPatchRequest request) {
		System.out.println("Patch product " + id);
		if (request.getName() != null) {
			System.out.println("  name: " + request.getName());
		}
		if (request.getPrice() != null) {
			System.out.println("  price: " + request.getPrice());
		}
		if (request.getColor() != null) {
			System.out.println("  color: " + request.getColor());
		}
		return ResponseEntity.ok(request);
	}

}
