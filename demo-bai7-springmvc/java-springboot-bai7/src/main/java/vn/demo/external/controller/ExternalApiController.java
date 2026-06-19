package vn.demo.external.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import vn.demo.external.service.ExternalApiService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalApiController {

	private final ExternalApiService externalApiService;

	@GetMapping("/products")
	public ResponseEntity<JsonNode> getProducts(
			@RequestParam(defaultValue = "10") int limit
	) {
		return ResponseEntity.ok(externalApiService.fetchProducts(limit));
	}

	@GetMapping("/categories")
	public ResponseEntity<JsonNode> getCategories() {
		return ResponseEntity.ok(externalApiService.fetchCategories());
	}

	@GetMapping("/users")
	public ResponseEntity<JsonNode> getUsers(
			@RequestParam(defaultValue = "10") int limit
	) {
		return ResponseEntity.ok(externalApiService.fetchUsers(limit));
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<JsonNode> getUserById(@PathVariable long id) {
		JsonNode user = externalApiService.fetchUserById(id);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}

}
