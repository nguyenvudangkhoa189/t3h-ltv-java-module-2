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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserViewController {

	private final UserService userService;
	private final FileStorageService fileStorageService;

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

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("user", new UserForm());
		model.addAttribute("isEdit", false);
		return "users/form";
	}

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

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		return userService.findById(id)
				.map(user -> {
					model.addAttribute("user", user);
					return "users/detail";
				})
				.orElse("users/not-found");
	}

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
