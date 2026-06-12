package vn.demo.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.dto.MeResponse;

@RestController
@RequestMapping("/api/v1")
public class MeApiController {

	@GetMapping("/me")
	public ResponseEntity<MeResponse> getMe(@RequestHeader("X-User-Id") String userId) {
		return ResponseEntity.ok(new MeResponse(userId, "Hello"));
	}

}
