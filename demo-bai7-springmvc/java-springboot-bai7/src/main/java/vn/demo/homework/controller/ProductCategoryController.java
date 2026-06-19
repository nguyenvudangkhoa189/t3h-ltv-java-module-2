package vn.demo.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import vn.demo.homework.service.HomeworkProductService;

/**
 * Bài tập 2 — GET /api/external/products/category/{name}.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ProductCategoryController {

	private final HomeworkProductService homeworkProductService;

	@GetMapping("/products/category/{name}")
	public ResponseEntity<JsonNode> getProductsByCategory(@PathVariable String name) {
		return ResponseEntity.ok(homeworkProductService.fetchProductsByCategory(name));
	}

}
