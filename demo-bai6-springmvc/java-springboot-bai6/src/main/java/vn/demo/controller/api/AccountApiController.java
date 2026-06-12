package vn.demo.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.demo.service.AccountService;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountApiController {

	private final AccountService accountService;

	@PostMapping("/signUp")
	public ResponseEntity<Boolean> signUpAccount(@RequestParam String emailAddress) {
		return ResponseEntity.ok(accountService.validEmailFormat(emailAddress));
	}

	@GetMapping("/orders")
	public ResponseEntity<java.util.List<String>> getOrders(@RequestParam String userId) {
		return ResponseEntity.ok(accountService.getOrders(userId));
	}

}
