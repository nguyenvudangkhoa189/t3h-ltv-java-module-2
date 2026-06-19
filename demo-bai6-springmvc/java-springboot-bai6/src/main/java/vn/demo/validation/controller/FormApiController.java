package vn.demo.validation.controller;

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
import vn.demo.validation.model.Account;

@RestController
@RequestMapping("/api/form")
public class FormApiController {

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
		// TODO: gọi AccountService xử lý đăng ký
		return ResponseEntity.ok().build();
	}

}
