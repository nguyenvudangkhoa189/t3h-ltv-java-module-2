package vn.demo.patch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.patch.dto.UserPatchRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserPatchController {

	@PatchMapping("/{id}")
	public ResponseEntity<UserPatchRequest> patchUser(
			@PathVariable String id,
			@RequestBody UserPatchRequest request) {
		System.out.println("Patch user " + id);
		if (request.getAddress() != null) {
			System.out.println("  address: " + request.getAddress());
		}
		if (request.getPhone() != null) {
			System.out.println("  phone: " + request.getPhone());
		}
		return ResponseEntity.ok(request);
	}

}
