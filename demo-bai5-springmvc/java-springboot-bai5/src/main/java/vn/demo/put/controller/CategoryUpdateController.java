package vn.demo.put.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.put.dto.CategoryRequest;

@RestController
public class CategoryUpdateController {

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
	public ResponseEntity<CategoryRequest> updateCategoryFromBody(
			@PathVariable String id,
			@RequestBody CategoryRequest request) {
		System.out.println("Update category " + id + ": " + request.getName());
		return ResponseEntity.ok(request);
	}

}
