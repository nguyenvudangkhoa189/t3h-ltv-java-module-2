package vn.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import vn.demo.controller.ProductWebController;

/**
 * Bộ xử lý lỗi tập trung cho các trang web (Thymeleaf).
 *
 * <p>Chỉ áp dụng cho {@link ProductWebController} ({@code assignableTypes}); mọi ngoại lệ
 * phát sinh khi render trang web sẽ được chuyển thành trang lỗi thân thiện thay vì
 * trang lỗi mặc định của Spring Boot.</p>
 */
@Slf4j
@ControllerAdvice(assignableTypes = ProductWebController.class)
public class WebExceptionHandler {

	/**
	 * Xử lý trường hợp không tìm thấy sản phẩm → hiển thị trang 404 riêng.
	 *
	 * @return template {@code error/404}
	 */
	@ExceptionHandler(ProductNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(ProductNotFoundException ex, Model model) {
		log.warn("404 - {}", ex.getMessage());
		model.addAttribute("message", ex.getMessage());
		return "error/404";
	}

	/**
	 * Xử lý mọi lỗi không lường trước (vd. lỗi gọi API) → hiển thị trang 500 riêng.
	 *
	 * @return template {@code error/500}
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleUnexpected(Exception ex, Model model) {
		log.error("500 - lỗi không xác định", ex);
		model.addAttribute("message", ex.getMessage());
		return "error/500";
	}

}
