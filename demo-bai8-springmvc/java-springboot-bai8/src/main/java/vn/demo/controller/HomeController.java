package vn.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller cho trang chủ — chỉ làm nhiệm vụ điều hướng.
 */
@Controller
public class HomeController {

	/**
	 * Khi truy cập {@code /}, tự chuyển hướng sang danh sách user.
	 *
	 * @return chỉ thị redirect tới {@code /users}
	 */
	@GetMapping("/")
	public String home() {
		return "redirect:/users";
	}

}
