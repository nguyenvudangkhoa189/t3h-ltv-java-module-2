package vn.demo.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.demo.homework.service.ProductService;

@RestController("homeworkProductController")
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/validate-price")
	public ResponseEntity<Boolean> validatePrice(@RequestParam double price) {
		return ResponseEntity.ok(productService.isValidPrice(price));
	}

}
