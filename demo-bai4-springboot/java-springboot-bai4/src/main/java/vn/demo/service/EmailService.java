package vn.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final SpringTemplateEngine templateEngine;

	public EmailService(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public String renderWelcomeEmail(String userName, String email) {
		Context context = new Context();
		context.setVariable("userName", userName);
		context.setVariable("email", email);
		String html = templateEngine.process("emails/welcome", context);
		log.info("Rendered welcome email for {} <{}>", userName, email);
		return html;
	}

	public void sendWelcomeEmail(String userName, String email) {
		String html = renderWelcomeEmail(userName, email);
		log.info("Demo: would send email to {} with {} bytes HTML", email, html.length());
	}

}
