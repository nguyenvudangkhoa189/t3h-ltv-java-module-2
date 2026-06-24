package vn.demo.exception;

/**
 * Ngoại lệ báo hiệu không tìm thấy sản phẩm theo id.
 *
 * <p>Controller ném ngoại lệ này khi service trả về {@code null}; bộ xử lý lỗi
 * {@code WebExceptionHandler} sẽ bắt và hiển thị trang 404 thân thiện cho người dùng.</p>
 */
public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(long id) {
		super("Không tìm thấy sản phẩm có id = " + id);
	}

}
