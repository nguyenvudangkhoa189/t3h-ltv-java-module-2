package vn.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import vn.demo.service.EmailService;

@RestController
@RequestMapping("/demo")
public class EngineDemoController {

	private final SpringTemplateEngine templateEngine;
	private final EmailService emailService;

	public EngineDemoController(SpringTemplateEngine templateEngine, EmailService emailService) {
		this.templateEngine = templateEngine;
		this.emailService = emailService;
	}

	@GetMapping(value = "/engine/hello", produces = MediaType.TEXT_HTML_VALUE)
	public String engineHello() {
		Context context = new Context();
		context.setVariable("title", "Render bằng SpringTemplateEngine");
		context.setVariable("studentName", "Nguyễn Văn A");
		context.setVariable("message", "HTML được render thủ công qua templateEngine.process()");
		return templateEngine.process("hello", context);
	}

	@GetMapping(value = "/email/preview", produces = MediaType.TEXT_HTML_VALUE)
	public String emailPreview() {
		return emailService.renderWelcomeEmail("Nguyễn Văn A", "nguyenvana@example.com");
	}

}
