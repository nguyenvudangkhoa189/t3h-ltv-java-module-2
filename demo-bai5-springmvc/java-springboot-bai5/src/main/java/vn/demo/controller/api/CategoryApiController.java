package vn.demo.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.dto.GameCreateRequest;
import vn.demo.dto.ProductRequest;

@RestController
public class CategoryApiController {

	@PostMapping("/api/v1/categories")
	public ResponseEntity<Void> createCategoryFromForm(
			@RequestParam String name,
			@RequestParam(required = false) String location) {
		System.out.println("Category name: " + name);
		System.out.println("Location: " + location);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/api/v1/categories/{id}")
	public ResponseEntity<Void> updateCategoryFromForm(
			@PathVariable String id,
			@RequestParam String name,
			@RequestParam(required = false) String description,
			@RequestParam(defaultValue = "active") String status) {
		System.out.println("Update category " + id);
		System.out.println("Name: " + name);
		System.out.println("Description: " + description);
		System.out.println("Status: " + status);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/api/v1/categories/{id}/json")
	public ResponseEntity<ProductRequest> updateCategoryFromBody(
			@PathVariable String id,
			@RequestBody ProductRequest request) {
		System.out.println("Update category " + id + ": " + request.getName());
		return ResponseEntity.ok(request);
	}

	@PostMapping("/api/v1/games")
	public ResponseEntity<GameCreateRequest> createGame(@RequestBody GameCreateRequest request) {
		System.out.println("Game: " + request.getName() + ", " + request.getPrice() + ", " + request.getPlatform());
		return ResponseEntity.status(HttpStatus.CREATED).body(request);
	}

}
