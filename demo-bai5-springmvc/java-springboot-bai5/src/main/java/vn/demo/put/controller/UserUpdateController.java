package vn.demo.put.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.put.dto.UserProfileRequest;

@RestController
@RequestMapping("/api/v1/users")
public class UserUpdateController {

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateUserFromForm(
			@PathVariable String id,
			@RequestParam String name,
			@RequestParam(required = false) String address) {
		System.out.println("Update user " + id);
		System.out.println("Name: " + name);
		System.out.println("Address: " + address);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/{id}/profile")
	public ResponseEntity<UserProfileRequest> updateUserProfile(
			@PathVariable String id,
			@RequestBody UserProfileRequest request) {
		System.out.println("Update profile user " + id);
		System.out.println("Gender: " + request.getGender());
		System.out.println("Age: " + request.getAge());
		System.out.println("Education: " + request.getEducation());
		return ResponseEntity.ok(request);
	}

}
