package vn.demo.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.post.dto.GameCreateRequest;

@RestController
public class CategoryCreateController {

	@PostMapping("/api/v1/categories")
	public ResponseEntity<Void> createCategoryFromForm(
			@RequestParam String name,
			@RequestParam(required = false) String location) {
		System.out.println("Category name: " + name);
		System.out.println("Location: " + location);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/api/v1/games")
	public ResponseEntity<GameCreateRequest> createGame(@RequestBody GameCreateRequest request) {
		System.out.println("Game: " + request.getName() + ", " + request.getPrice() + ", " + request.getPlatform());
		return ResponseEntity.status(HttpStatus.CREATED).body(request);
	}

}
