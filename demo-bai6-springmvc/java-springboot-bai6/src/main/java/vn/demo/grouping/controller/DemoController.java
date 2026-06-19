package vn.demo.grouping.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

	@DeleteMapping("/order/detail")
	public ResponseEntity<String> deleteOrder(@RequestParam String id) {
		System.out.println("Id value: " + id);
		return ResponseEntity.ok(id);
	}

	@GetMapping("/order/list")
	public ResponseEntity<List<String>> listOrders() {
		return ResponseEntity.ok(List.of("order-1", "order-2"));
	}

}
