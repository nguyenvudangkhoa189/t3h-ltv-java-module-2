package vn.demo.controller.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.demo.model.Account;
import vn.demo.service.AccountService;

@RestController
@RequestMapping("/api/form")
@RequiredArgsConstructor
public class FormApiController {

	private final AccountService accountService;

	@PostMapping("/fill")
	public ResponseEntity<?> fillTheForm(
			@Valid @RequestBody Account account,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			List<String> errors = new ArrayList<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errors.add(error.getDefaultMessage());
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		accountService.save(account);
		return ResponseEntity.ok().build();
	}

}
