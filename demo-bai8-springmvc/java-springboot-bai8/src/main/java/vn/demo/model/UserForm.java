package vn.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Đối tượng vừa dùng làm dữ liệu hiển thị, vừa là form nhập liệu cho User.
 *
 * <p>Các annotation validation ({@code @NotBlank}, {@code @Email}...) sẽ được kiểm tra
 * khi controller nhận form qua {@code @Valid @ModelAttribute}.</p>
 */
@Data
@NoArgsConstructor
public class UserForm {

	private Long id;

	@NotBlank(message = "Họ không được để trống")
	@Size(max = 50, message = "Họ tối đa 50 ký tự")
	private String firstName;

	@NotBlank(message = "Tên không được để trống")
	@Size(max = 50, message = "Tên tối đa 50 ký tự")
	private String lastName;

	@NotBlank(message = "Email không được để trống")
	@Email(message = "Email không đúng định dạng")
	private String email;

	@Size(max = 20, message = "SĐT tối đa 20 ký tự")
	private String phone;

	private String avatarUrl;

	/** @return họ và tên ghép lại (an toàn khi một trong hai bị null). */
	public String getFullName() {
		return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
	}

}
