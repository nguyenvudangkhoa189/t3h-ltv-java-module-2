package vn.demo.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import vn.demo.dto.UserPage;
import vn.demo.model.UserForm;

/**
 * Service quản lý User bằng bộ nhớ tạm (in-memory) — chưa dùng database.
 *
 * <p>Toàn bộ user được lưu trong một {@link Map} (mất khi restart app). Lớp này
 * cung cấp các thao tác CRUD cơ bản cùng tìm kiếm và phân trang cho màn hình danh sách.</p>
 */
@Slf4j
@Service
public class UserService {

	/** Kho lưu user trong RAM, giữ thứ tự thêm vào nhờ {@link LinkedHashMap}. */
	private final Map<Long, UserForm> store = new LinkedHashMap<>();

	/** Bộ sinh id tự tăng cho user mới (user mẫu chiếm id 1..12). */
	private final AtomicLong idSequence = new AtomicLong(13);

	/** Số user hiển thị trên mỗi trang, đọc từ application.properties. */
	private final int pageSize;

	/**
	 * Khởi tạo service và đọc kích thước trang từ cấu hình.
	 *
	 * @param pageSize số user mỗi trang (key {@code app.users.page-size}, mặc định 5)
	 */
	public UserService(@Value("${app.users.page-size:5}") int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Nạp 12 user mẫu vào bộ nhớ ngay khi app khởi động.
	 *
	 * <p>Nhờ {@link PostConstruct}, học viên mở {@code /users} là thấy dữ liệu ngay,
	 * không cần database hay External API.</p>
	 */
	@PostConstruct
	public void initSampleData() {
		addSample(1L, "Nguyễn", "Văn An", "an.nguyen@example.com", "0901111111");
		addSample(2L, "Trần", "Thị Bình", "binh.tran@example.com", "0902222222");
		addSample(3L, "Lê", "Văn Cường", "cuong.le@example.com", "0903333333");
		addSample(4L, "Phạm", "Thị Dung", "dung.pham@example.com", "0904444444");
		addSample(5L, "Hoàng", "Văn Em", "em.hoang@example.com", "0905555555");
		addSample(6L, "Vũ", "Thị Phương", "phuong.vu@example.com", "0906666666");
		addSample(7L, "Đặng", "Văn Giang", "giang.dang@example.com", "0907777777");
		addSample(8L, "Bùi", "Thị Hà", "ha.bui@example.com", "0908888888");
		addSample(9L, "Ngô", "Văn Ích", "ich.ngo@example.com", "0909999999");
		addSample(10L, "Dương", "Thị Kim", "kim.duong@example.com", "0910000000");
		addSample(11L, "Lý", "Văn Long", "long.ly@example.com", "0911111111");
		addSample(12L, "Mai", "Thị Lan", "lan.mai@example.com", "0912222222");
		log.info("Initialized {} sample users for demo (page size={})", store.size(), pageSize);
	}

	/** Tạo một user mẫu (avatar để trống) rồi bỏ vào kho. */
	private void addSample(Long id, String firstName, String lastName, String email, String phone) {
		UserForm user = new UserForm();
		user.setId(id);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPhone(phone);
		store.put(id, user);
	}

	/**
	 * Lấy toàn bộ user hiện có.
	 *
	 * @return danh sách tất cả user (bản sao, an toàn khi sửa bên ngoài)
	 */
	public List<UserForm> findAll() {
		return new ArrayList<>(store.values());
	}

	/**
	 * Tìm kiếm theo từ khóa rồi cắt ra đúng một trang dữ liệu.
	 *
	 * @param query từ khóa tìm kiếm (có thể null/rỗng = lấy tất cả)
	 * @param page  số trang muốn xem (tự đưa về khoảng hợp lệ)
	 * @return {@link UserPage} chứa user của trang + thông tin phân trang
	 */
	public UserPage findPage(String query, int page) {
		String normalizedQuery = query == null ? "" : query.trim();
		List<UserForm> filtered = findAll().stream()
				.filter(user -> matchesQuery(user, normalizedQuery))
				.toList();

		int safePage = Math.max(page, 1);
		int totalItems = filtered.size();
		int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / pageSize);
		if (safePage > totalPages) {
			safePage = totalPages;
		}

		int fromIndex = (safePage - 1) * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, totalItems);
		List<UserForm> pageItems = fromIndex >= totalItems
				? List.of()
				: filtered.subList(fromIndex, toIndex);

		return new UserPage(pageItems, normalizedQuery, safePage, pageSize, totalItems, totalPages);
	}

	/** Kiểm tra user có khớp từ khóa không (so trên họ, tên, full name, email, SĐT). */
	private boolean matchesQuery(UserForm user, String query) {
		if (query.isEmpty()) {
			return true;
		}
		String lower = query.toLowerCase(Locale.ROOT);
		return containsIgnoreCase(user.getFirstName(), lower)
				|| containsIgnoreCase(user.getLastName(), lower)
				|| containsIgnoreCase(user.getFullName(), lower)
				|| containsIgnoreCase(user.getEmail(), lower)
				|| containsIgnoreCase(user.getPhone(), lower);
	}

	/** So sánh chứa chuỗi, không phân biệt hoa thường và an toàn với null. */
	private boolean containsIgnoreCase(String value, String lowerQuery) {
		return value != null && value.toLowerCase(Locale.ROOT).contains(lowerQuery);
	}

	/**
	 * Tìm một user theo id.
	 *
	 * @param id id cần tìm
	 * @return {@link Optional} chứa user nếu tồn tại, ngược lại rỗng
	 */
	public Optional<UserForm> findById(Long id) {
		return Optional.ofNullable(store.get(id));
	}

	/**
	 * Tạo mới một user và cấp id tự tăng.
	 *
	 * @param form dữ liệu user gửi từ form
	 * @return user sau khi đã được gán id
	 */
	public UserForm create(UserForm form) {
		long newId = idSequence.getAndIncrement();
		form.setId(newId);
		store.put(newId, form);
		return form;
	}

	/**
	 * Cập nhật user theo id; giữ lại avatar cũ nếu lần này không upload ảnh mới.
	 *
	 * @param id   id user cần sửa
	 * @param form dữ liệu mới từ form
	 * @return user sau khi cập nhật
	 * @throws NoSuchElementException nếu id không tồn tại
	 */
	public UserForm update(Long id, UserForm form) {
		if (!store.containsKey(id)) {
			throw new NoSuchElementException("User not found: " + id);
		}
		UserForm existing = store.get(id);
		form.setId(id);
		if (form.getAvatarUrl() == null || form.getAvatarUrl().isBlank()) {
			form.setAvatarUrl(existing.getAvatarUrl());
		}
		store.put(id, form);
		return form;
	}

	/**
	 * Xóa user theo id.
	 *
	 * @param id id user cần xóa
	 * @throws NoSuchElementException nếu id không tồn tại
	 */
	public void delete(Long id) {
		if (store.remove(id) == null) {
			throw new NoSuchElementException("User not found: " + id);
		}
	}

}
