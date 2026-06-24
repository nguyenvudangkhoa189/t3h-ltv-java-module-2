package vn.demo.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Dữ liệu gửi lên khi thêm/sửa sản phẩm (body của request từ form).
 *
 * <p>Các annotation Bean Validation bên dưới là lớp kiểm tra <b>phía server</b> — chạy khi
 * controller nhận request với {@code @Valid}. Đây là lớp bảo vệ thứ hai sau validation
 * JavaScript ở trình duyệt: dù người dùng tắt JS hay gọi thẳng API thì dữ liệu sai vẫn bị chặn.</p>
 */
@Data
public class ProductRequest {

	@NotBlank(message = "Tên sản phẩm không được rỗng.")
	private String title;

	@NotBlank(message = "Mô tả không được rỗng.")
	private String description;

	@NotNull(message = "Giá không được rỗng.")
	@DecimalMin(value = "1", message = "Giá phải từ 1 đến 200 USD.")
	@DecimalMax(value = "200", message = "Giá phải từ 1 đến 200 USD.")
	private Double price;

	@NotBlank(message = "Nhóm sản phẩm không được rỗng.")
	private String category;

	@NotBlank(message = "URL hình ảnh không được rỗng.")
	private String thumbnail;

	@NotBlank(message = "Thương hiệu không được rỗng.")
	private String brand;

}
