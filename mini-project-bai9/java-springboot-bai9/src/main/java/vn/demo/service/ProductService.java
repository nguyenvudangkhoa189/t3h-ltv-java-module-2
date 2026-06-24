package vn.demo.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import vn.demo.dto.ProductListResponse;
import vn.demo.dto.ProductRequest;
import vn.demo.model.Category;
import vn.demo.model.Product;

/**
 * Service chứa toàn bộ nghiệp vụ sản phẩm — là cầu nối giữa controller và API ngoài DummyJSON.
 *
 * <p>Lớp này dùng {@link RestTemplate} để gọi REST API tại {@code https://dummyjson.com/products}
 * cho các thao tác: lấy danh sách, xem chi tiết, tìm kiếm, lấy nhóm sản phẩm, thêm và sửa.
 * Mọi lời gọi đều được bọc try/catch để khi API lỗi/timeout thì trả về dữ liệu rỗng hoặc
 * {@code null} thay vì làm sập trang web.</p>
 */
@Service
@RequiredArgsConstructor
public class ProductService {

	/** Địa chỉ gốc của API sản phẩm DummyJSON, mọi endpoint đều nối thêm vào URL này. */
	private static final String BASE_URL = "https://dummyjson.com/products";

	/** HTTP client (cấu hình sẵn timeout) dùng để gọi API ngoài. */
	private final RestTemplate restTemplate;

	/**
	 * Lấy danh sách sản phẩm cho trang chủ — hỗ trợ lọc theo nhóm, phân trang và sắp xếp.
	 *
	 * <p>Hàm tự dựng URL gọi DummyJSON dựa trên tham số:
	 * dùng endpoint {@code /products/category/{slug}} khi có lọc nhóm, ngược lại dùng
	 * {@code /products}; thêm {@code limit}/{@code skip} để phân trang và {@code sortBy}/{@code order}
	 * để sắp xếp.</p>
	 *
	 * @return danh sách sản phẩm của trang hiện tại kèm tổng số; rỗng nếu API lỗi
	 */
	public ProductListResponse getProducts(String category, int page, int size, String sortBy, String order) {
		String baseUrl = (category == null || category.isBlank())
				? BASE_URL
				: BASE_URL + "/category/" + category;

		int skip = Math.max(page - 1, 0) * size;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl)
				.queryParam("limit", size)
				.queryParam("skip", skip);

		if (sortBy != null && !sortBy.isBlank()) {
			builder.queryParam("sortBy", sortBy)
					.queryParam("order", (order == null || order.isBlank()) ? "asc" : order);
		}

		return fetchProductList(builder.toUriString());
	}

	/**
	 * Lấy thông tin chi tiết của một sản phẩm theo id.
	 *
	 * @return sản phẩm tìm được, hoặc {@code null} nếu không có / API lỗi
	 */
	public Product getProductById(long id) {
		try {
			return restTemplate.getForObject(BASE_URL + "/" + id, Product.class);
		} catch (RestClientException ex) {
			return null;
		}
	}

	/**
	 * Tìm kiếm sản phẩm theo từ khóa (gọi endpoint {@code /products/search?q=...}),
	 * có hỗ trợ phân trang và sắp xếp giống trang chủ.
	 *
	 * @return danh sách sản phẩm khớp từ khóa; rỗng nếu không có kết quả / API lỗi
	 */
	public ProductListResponse searchProducts(String keyword, int page, int size, String sortBy, String order) {
		int skip = Math.max(page - 1, 0) * size;
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL + "/search")
				.queryParam("q", keyword)
				.queryParam("limit", size)
				.queryParam("skip", skip);

		if (sortBy != null && !sortBy.isBlank()) {
			builder.queryParam("sortBy", sortBy)
					.queryParam("order", (order == null || order.isBlank()) ? "asc" : order);
		}

		return fetchProductList(builder.toUriString());
	}

	/**
	 * Lấy danh sách nhóm (category) sản phẩm để hiển thị trang phân loại.
	 *
	 * @return danh sách category; rỗng nếu API lỗi
	 */
	public List<Category> getCategories() {
		try {
			Category[] categories = restTemplate.getForObject(BASE_URL + "/categories", Category[].class);
			if (categories == null) {
				return Collections.emptyList();
			}
			return Arrays.asList(categories);
		} catch (RestClientException ex) {
			return Collections.emptyList();
		}
	}

	/**
	 * Thêm mới một sản phẩm bằng cách gửi {@code POST /products/add} tới DummyJSON.
	 *
	 * <p>Lưu ý: DummyJSON chỉ giả lập việc thêm, dữ liệu không được lưu thật trên server.</p>
	 *
	 * @return sản phẩm vừa tạo (kèm id mới do API trả về)
	 */
	public Product addProduct(ProductRequest request) {
		HttpEntity<ProductRequest> entity = jsonEntity(request);
		return restTemplate.postForObject(BASE_URL + "/add", entity, Product.class);
	}

	/**
	 * Cập nhật một sản phẩm theo id bằng cách gửi {@code PUT /products/{id}} tới DummyJSON.
	 *
	 * <p>Lưu ý: DummyJSON chỉ giả lập việc cập nhật, dữ liệu không được lưu thật trên server.</p>
	 *
	 * @return sản phẩm sau khi cập nhật do API trả về
	 */
	public Product updateProduct(long id, ProductRequest request) {
		HttpEntity<ProductRequest> entity = jsonEntity(request);
		return restTemplate
				.exchange(BASE_URL + "/" + id, HttpMethod.PUT, entity, Product.class)
				.getBody();
	}

	/**
	 * Xóa một sản phẩm theo id bằng cách gửi {@code DELETE /products/{id}} tới DummyJSON.
	 *
	 * <p>Lưu ý: DummyJSON chỉ giả lập việc xóa, dữ liệu không bị xóa thật trên server.</p>
	 */
	public void deleteProduct(long id) {
		restTemplate.delete(BASE_URL + "/" + id);
	}

	/**
	 * Hàm dùng chung để gọi một URL trả về danh sách sản phẩm và chuẩn hóa kết quả.
	 *
	 * <p>Nếu API lỗi hoặc trả về thiếu trường, hàm tự bù {@code products} rỗng và
	 * {@code total} hợp lệ để view luôn render được mà không cần kiểm tra null.</p>
	 *
	 * @return danh sách sản phẩm đã được làm sạch; rỗng nếu API lỗi
	 */
	private ProductListResponse fetchProductList(String url) {
		try {
			ProductListResponse response = restTemplate.getForObject(url, ProductListResponse.class);
			if (response == null) {
				return emptyProductList();
			}
			if (response.getProducts() == null) {
				response.setProducts(Collections.emptyList());
			}
			if (response.getTotal() == null) {
				response.setTotal(response.getProducts().size());
			}
			return response;
		} catch (RestClientException ex) {
			return emptyProductList();
		}
	}

	/** Tạo một kết quả danh sách rỗng (0 sản phẩm) dùng khi API lỗi. */
	private ProductListResponse emptyProductList() {
		ProductListResponse response = new ProductListResponse();
		response.setProducts(Collections.emptyList());
		response.setTotal(0);
		return response;
	}

	/** Đóng gói request thành {@link HttpEntity} có header {@code Content-Type: application/json}. */
	private HttpEntity<ProductRequest> jsonEntity(ProductRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<>(request, headers);
	}

}
