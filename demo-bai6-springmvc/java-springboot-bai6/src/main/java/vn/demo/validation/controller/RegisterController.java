package vn.demo.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import vn.demo.validation.model.Account;

@Controller
@RequestMapping("/register")
public class RegisterController {

	@GetMapping
	public String showForm(Model model) {
		model.addAttribute("account", new Account());
		return "validation/form";
	}

	@PostMapping
	public String submitForm(
			@Valid @ModelAttribute("account") Account account,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "validation/form";
		}
		// TODO: gọi AccountService lưu tài khoản
		return "redirect:/register/success";
	}

	@GetMapping("/success")
	public String success() {
		return "validation/success";
	}

}
