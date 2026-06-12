package vn.demo.controller.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.demo.service.ProductService;

@RestController
@RequiredArgsConstructor
public class ProductApiController {

	private final ProductService productService;

	@GetMapping("/products")
	public ResponseEntity<Map<String, String>> getAllProducts(
			@RequestHeader Map<String, String> headers) {
		System.out.println(headers);
		return ResponseEntity.ok(headers);
	}

	@GetMapping("/profile")
	public ResponseEntity<String> getProfile(
			@RequestHeader("Authorization") String authorization,
			@RequestHeader(value = "X-Request-Id", required = false) String requestId) {
		return ResponseEntity.ok("Profile data (Authorization present, requestId=" + requestId + ")");
	}

	@GetMapping("/api/v1/products/validate-price")
	public ResponseEntity<Boolean> validatePrice(@RequestParam double price) {
		return ResponseEntity.ok(productService.isValidPrice(price));
	}

}
