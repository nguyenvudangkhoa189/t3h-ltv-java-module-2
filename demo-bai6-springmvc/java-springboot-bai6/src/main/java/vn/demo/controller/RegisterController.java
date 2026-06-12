package vn.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.demo.model.Account;
import vn.demo.service.AccountService;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

	private final AccountService accountService;

	@GetMapping
	public String showForm(Model model) {
		model.addAttribute("account", new Account());
		return "register/form";
	}

	@PostMapping
	public String submitForm(
			@Valid @ModelAttribute("account") Account account,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "register/form";
		}
		accountService.save(account);
		return "redirect:/register/success";
	}

	@GetMapping("/success")
	public String success() {
		return "register/success";
	}

}
