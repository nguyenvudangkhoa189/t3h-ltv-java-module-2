package vn.demo.controller;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.demo.dto.UserPage;
import vn.demo.model.UserForm;
import vn.demo.service.FileStorageService;
import vn.demo.service.UserService;

/**
 * Controller cho các màn hình HTML quản lý User (render bằng Thymeleaf).
 *
 * <p>Mỗi phương thức xử lý một request, đẩy dữ liệu vào {@link Model} rồi trả về
 * tên template (vd. {@code "users/list"} → {@code templates/users/list.html}).
 * Sau khi thêm/sửa/xóa thành công sẽ {@code redirect} theo mẫu Post-Redirect-Get.</p>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserViewController {

	private final UserService userService;
	private final FileStorageService fileStorageService;

	/**
	 * Hiển thị danh sách user có tìm kiếm và phân trang.
	 *
	 * @param query từ khóa tìm kiếm ({@code ?q=}), tùy chọn
	 * @param page  số trang ({@code ?page=}), mặc định 1
	 * @param model nơi gửi dữ liệu sang view
	 * @return template {@code users/list}
	 */
	@GetMapping
	public String list(
			@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "page", defaultValue = "1") int page,
			Model model
	) {
		UserPage userPage = userService.findPage(query, page);
		model.addAttribute("userPage", userPage);
		model.addAttribute("users", userPage.getUsers());
		model.addAttribute("q", userPage.getQuery());
		return "users/list";
	}

	/**
	 * Mở form tạo user mới (form trống).
	 *
	 * @param model nơi đặt đối tượng {@code user} rỗng cho form binding
	 * @return template {@code users/form}
	 */
	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("user", new UserForm());
		model.addAttribute("isEdit", false);
		return "users/form";
	}

	/**
	 * Xử lý submit tạo user mới kèm upload avatar.
	 *
	 * <p>Validate dữ liệu form; nếu có lỗi thì quay lại form. Nếu hợp lệ thì
	 * lưu avatar (nếu có), tạo user và redirect về danh sách (PRG).</p>
	 *
	 * @param user               dữ liệu form đã được validate
	 * @param bindingResult      kết quả validate
	 * @param avatar             file ảnh upload (tùy chọn)
	 * @param model              nơi gửi dữ liệu sang view khi cần render lại form
	 * @param redirectAttributes nơi đặt flash message hiển thị 1 lần sau redirect
	 * @return redirect {@code /users} khi thành công, hoặc template {@code users/form} khi lỗi
	 */
	@PostMapping
	public String create(
			@Valid @ModelAttribute("user") UserForm user,
			BindingResult bindingResult,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("isEdit", false);
			return "users/form";
		}
		try {
			if (avatar != null && !avatar.isEmpty()) {
				user.setAvatarUrl(fileStorageService.store(avatar, "avatars"));
			}
			userService.create(user);
			redirectAttributes.addFlashAttribute("message", "Tạo user thành công!");
			log.info("Created user id={}", user.getId());
			return "redirect:/users";
		} catch (IllegalArgumentException e) {
			log.warn("Create user failed - upload: {}", e.getMessage());
			model.addAttribute("isEdit", false);
			model.addAttribute("uploadError", e.getMessage());
			return "users/form";
		} catch (Exception e) {
			log.warn("Create user failed - upload IO: {}", e.getMessage());
			model.addAttribute("isEdit", false);
			model.addAttribute("uploadError", "Không thể lưu file ảnh.");
			return "users/form";
		}
	}

	/**
	 * Hiển thị chi tiết một user.
	 *
	 * @param id    id user cần xem
	 * @param model nơi đặt dữ liệu user cho view
	 * @return template {@code users/detail}, hoặc {@code users/not-found} nếu không thấy
	 */
	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		return userService.findById(id)
				.map(user -> {
					model.addAttribute("user", user);
					return "users/detail";
				})
				.orElse("users/not-found");
	}

	/**
	 * Mở form sửa user (đổ sẵn dữ liệu hiện tại vào form).
	 *
	 * @param id    id user cần sửa
	 * @param model nơi đặt dữ liệu user cho form binding
	 * @return template {@code users/form}, hoặc {@code users/not-found} nếu không thấy
	 */
	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		return userService.findById(id)
				.map(user -> {
					model.addAttribute("user", user);
					model.addAttribute("isEdit", true);
					return "users/form";
				})
				.orElse("users/not-found");
	}

	/**
	 * Xử lý submit cập nhật user kèm upload avatar (tùy chọn).
	 *
	 * @param id                 id user cần sửa
	 * @param user               dữ liệu form đã được validate
	 * @param bindingResult      kết quả validate
	 * @param avatar             file ảnh mới (nếu để trống sẽ giữ avatar cũ)
	 * @param model              nơi gửi dữ liệu sang view khi cần render lại form
	 * @param redirectAttributes nơi đặt flash message sau redirect
	 * @return redirect {@code /users} khi thành công, hoặc template form/not-found khi lỗi
	 */
	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("user") UserForm user,
			BindingResult bindingResult,
			@RequestParam(value = "avatar", required = false) MultipartFile avatar,
			Model model,
			RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("isEdit", true);
			return "users/form";
		}
		try {
			if (avatar != null && !avatar.isEmpty()) {
				user.setAvatarUrl(fileStorageService.store(avatar, "avatars"));
			}
			userService.update(id, user);
			redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
			return "redirect:/users";
		} catch (NoSuchElementException e) {
			return "users/not-found";
		} catch (IllegalArgumentException e) {
			model.addAttribute("isEdit", true);
			model.addAttribute("uploadError", e.getMessage());
			return "users/form";
		} catch (Exception e) {
			model.addAttribute("isEdit", true);
			model.addAttribute("uploadError", "Không thể lưu file ảnh.");
			return "users/form";
		}
	}

	/**
	 * Xóa user theo id rồi quay lại danh sách (PRG).
	 *
	 * @param id                 id user cần xóa
	 * @param redirectAttributes nơi đặt flash message thông báo kết quả
	 * @return redirect {@code /users}
	 */
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			userService.delete(id);
			redirectAttributes.addFlashAttribute("message", "Đã xóa user.");
		} catch (NoSuchElementException e) {
			redirectAttributes.addFlashAttribute("error", "Không tìm thấy user.");
		}
		return "redirect:/users";
	}

}
