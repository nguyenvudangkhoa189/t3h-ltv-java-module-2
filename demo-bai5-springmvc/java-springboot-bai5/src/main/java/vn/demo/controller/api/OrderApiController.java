package vn.demo.controller.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderApiController {

	@DeleteMapping("/api/v1/orders")
	public ResponseEntity<Void> deleteOrderByQuery(@RequestParam String id) {
		System.out.println("Delete order id: " + id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/v1/orders/{id}")
	public ResponseEntity<Void> deleteOrderById(@PathVariable String id) {
		System.out.println("Delete order id: " + id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/v1/orders/batch")
	public ResponseEntity<Void> deleteOrdersFromBody(@RequestBody Map<String, Object> body) {
		System.out.println("Body data: " + body);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/v1/songs")
	public ResponseEntity<Void> deleteSongByQuery(
			@RequestParam String title,
			@RequestParam(required = false) String theme) {
		System.out.println("Delete song title: " + title);
		System.out.println("Theme: " + theme);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/api/v1/songs/{id}")
	public ResponseEntity<Void> deleteSongById(@PathVariable String id) {
		System.out.println("Delete song id: " + id);
		return ResponseEntity.noContent().build();
	}

}
