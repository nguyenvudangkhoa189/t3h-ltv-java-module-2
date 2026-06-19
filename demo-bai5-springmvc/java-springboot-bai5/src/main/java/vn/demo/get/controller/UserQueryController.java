package vn.demo.get.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserQueryController {

	@GetMapping
	public ResponseEntity<List<String>> getAllUsers() {
		return ResponseEntity.ok(List.of("Sarah", "Mike", "Kim Jong"));
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getUserById(@PathVariable String id) {
		System.out.println("User id: " + id);
		return ResponseEntity.ok("User id: " + id);
	}

}
