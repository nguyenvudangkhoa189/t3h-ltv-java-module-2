package vn.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import vn.demo.controller.api.ProductApiController;

/**
 * Bộ xử lý lỗi tập trung cho REST API ({@link ProductApiController}).
 *
 * <p>Trả về lỗi dạng JSON để JavaScript trên form đọc được và hiển thị ngay tại từng ô nhập,
 * thay vì trả về trang HTML như phía web.</p>
 */
@Slf4j
@RestControllerAdvice(assignableTypes = ProductApiController.class)
public class ApiExceptionHandler {

	/**
	 * Gom các lỗi validation {@code @Valid} thành map {@code tên trường → thông báo}.
	 *
	 * @return HTTP 400 kèm danh sách lỗi theo từng trường
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
		}
		log.warn("400 - validation thất bại: {}", errors);
		return ResponseEntity.badRequest().body(errors);
	}

	/**
	 * Xử lý mọi lỗi khác khi gọi API (vd. lỗi gọi DummyJSON) → trả về HTTP 502.
	 *
	 * @return HTTP 502 kèm thông báo lỗi
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
		log.error("502 - lỗi khi gọi API", ex);
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(Map.of("error", "Không thể xử lý yêu cầu, vui lòng thử lại."));
	}

}
