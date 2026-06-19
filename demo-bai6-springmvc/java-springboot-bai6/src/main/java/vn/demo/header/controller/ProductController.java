package vn.demo.header.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController("headerProductController")
public class ProductController {

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

}
