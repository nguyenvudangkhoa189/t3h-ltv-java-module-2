package vn.demo.basic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

	@GetMapping("/hello")
	public String hello(Model model) {
		model.addAttribute("title", "Thymeleaf Hello World");
		model.addAttribute("studentName", "Nguyễn Văn A");
		model.addAttribute("message", "Xin chào từ Spring Boot!");
		return "basic/hello";
	}

}
