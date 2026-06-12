package vn.demo.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.demo.dto.ProductRequest;
import vn.demo.model.Product;
import vn.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductApiController {

	private final ProductService productService;

	@PostMapping
	public ResponseEntity<Product> addProduct(@RequestBody ProductRequest request) {
		Product product = productService.addProduct(request);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(
			@PathVariable long id,
			@RequestBody ProductRequest request) {
		Product product = productService.updateProduct(id, request);
		return ResponseEntity.ok(product);
	}

}
