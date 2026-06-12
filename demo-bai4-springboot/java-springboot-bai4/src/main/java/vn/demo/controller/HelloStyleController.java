package vn.demo.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloStyleController {

	@GetMapping("/")
	public String home() {
		return "redirect:/hello-style";
	}

	@GetMapping("/hello-style")
	public String helloStyle(Model model) {
		model.addAttribute("title", "Hello World có CSS & thời gian");
		model.addAttribute("studentName", "Nguyễn Văn A");
		model.addAttribute("message", "Xin chào từ Spring Boot!");
		model.addAttribute("now", LocalDateTime.now());
		return "hello-style";
	}

}
