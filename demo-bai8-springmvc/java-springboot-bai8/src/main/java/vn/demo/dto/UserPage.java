package vn.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.demo.model.UserForm;

/**
 * DTO gói gọn dữ liệu một trang danh sách user để gửi sang view.
 *
 * <p>Chứa danh sách user của trang hiện tại cùng các thông tin phân trang
 * (từ khóa, số trang, tổng số item, tổng số trang) giúp template Thymeleaf
 * dễ dàng vẽ bảng và thanh phân trang.</p>
 */
@Data
@AllArgsConstructor
public class UserPage {

	/** Danh sách user thuộc trang hiện tại. */
	private List<UserForm> users;

	/** Từ khóa tìm kiếm đang áp dụng (rỗng nếu không tìm). */
	private String query;

	/** Số trang hiện tại (bắt đầu từ 1). */
	private int page;

	/** Số user mỗi trang. */
	private int pageSize;

	/** Tổng số user khớp điều kiện tìm kiếm. */
	private int totalItems;

	/** Tổng số trang. */
	private int totalPages;

	/** @return true nếu còn trang trước đó. */
	public boolean hasPrevious() {
		return page > 1;
	}

	/** @return true nếu còn trang kế tiếp. */
	public boolean hasNext() {
		return page < totalPages;
	}

}
