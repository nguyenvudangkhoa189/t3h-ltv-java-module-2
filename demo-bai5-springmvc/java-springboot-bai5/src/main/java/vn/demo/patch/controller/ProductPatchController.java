package vn.demo.patch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.patch.dto.ProductPatchRequest;

@RestController
@RequestMapping("/api/v1/products")
public class ProductPatchController {

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
