package vn.demo.homework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import vn.demo.homework.dto.BookRequest;
import vn.demo.homework.service.BookService;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookWebController {

	private final BookService bookService;

	@GetMapping("/new")
	public String showForm(Model model) {
		model.addAttribute("bookRequest", new BookRequest());
		return "homework/form";
	}

	@PostMapping
	public String create(
			@Valid @ModelAttribute("bookRequest") BookRequest bookRequest,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "homework/form";
		}
		bookService.create(bookRequest);
		return "redirect:/books/success";
	}

	@GetMapping("/success")
	public String success() {
		return "homework/success";
	}

}
