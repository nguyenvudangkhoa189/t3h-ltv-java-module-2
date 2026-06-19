package vn.demo.delete.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	@DeleteMapping
	public ResponseEntity<Void> deleteOrderByQuery(@RequestParam String id) {
		System.out.println("Delete order id: " + id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrderById(@PathVariable String id) {
		System.out.println("Delete order id: " + id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/batch")
	public ResponseEntity<Void> deleteOrdersFromBody(@RequestBody Map<String, Object> body) {
		System.out.println("Body data: " + body);
		return ResponseEntity.noContent().build();
	}

}
