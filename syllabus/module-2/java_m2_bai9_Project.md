# Mini Project Bài 9 — Website bán hàng trực tuyến

Ứng dụng web bán hàng xây dựng bằng **Spring Boot + Thymeleaf**, lấy dữ liệu sản phẩm từ [DummyJSON API](https://dummyjson.com/docs/products). Các thao tác thêm/sửa sản phẩm đi qua **Spring Proxy** (backend gọi API ngoài, không gọi thẳng từ trình duyệt).

---

## 1. Đề bài — Yêu cầu xây dựng phần mềm

### 1.1. Bối cảnh

Công ty A muốn xây dựng **1 website bán hàng trực tuyến** với các sản phẩm đa dạng về chủng loại và giá cả, phục vụ các đối tượng khách hàng trong nước.

### 1.2. Yêu cầu chức năng

Website có các chức năng sau — **tất cả đều đã được triển khai**:

1. **Hiển thị danh sách sản phẩm** (có hình, tên, giá, mô tả, phân nhóm)
2. **Hiển thị nhóm sản phẩm**
3. **Hiển thị chi tiết** từng sản phẩm
4. **Tìm kiếm sản phẩm** theo từ khóa tên sản phẩm
5. **Thêm sản phẩm mới** thông qua giao diện form
6. **Cập nhật, sửa đổi** thông tin sản phẩm thông qua giao diện form
7. **Xóa sản phẩm** — nút Xóa ở trang chi tiết, gọi `DELETE` qua Spring Proxy
8. **Phân trang danh sách** — chia danh sách theo trang (tham số `limit` & `skip` của DummyJSON), có nút Trước / Sau
9. **Sắp xếp sản phẩm** — theo giá, tên, đánh giá (tăng / giảm dần)
10. **Lọc theo nhóm** — bấm 1 nhóm ở trang Category để xem sản phẩm thuộc nhóm đó
11. **Validation phía server** — Bean Validation (`@NotBlank`, `@NotNull`, `@DecimalMin`, `@DecimalMax`) cho `ProductRequest` bên cạnh validation JavaScript
12. **Trang lỗi thân thiện** — xử lý không tìm thấy sản phẩm / API lỗi bằng trang `404`, `500` riêng (`@ControllerAdvice`)

#### Gợi ý phát triển thêm (chưa triển khai)

Các ý tưởng dưới đây nằm ngoài phạm vi bài, dùng cho học viên rèn luyện thêm (hướng tới Module 3+):

- **Giỏ hàng (Cart)** — thêm sản phẩm vào giỏ, lưu trong `Session`, hiển thị số lượng & tổng tiền.
- **Đặt hàng (Checkout)** — form thông tin khách hàng, tạo đơn hàng từ giỏ hàng.
- **Lưu dữ liệu thật** — thay DummyJSON bằng database (MySQL/PostgreSQL + Spring Data JPA) để dữ liệu thêm/sửa được lưu vĩnh viễn.
- **Đăng nhập / phân quyền** — Spring Security: khách xem sản phẩm, admin mới được thêm/sửa/xóa.
- **Upload ảnh sản phẩm** — cho phép tải ảnh từ máy thay vì nhập URL (tham khảo cách làm ở demo bài 8).
- **Đa ngôn ngữ (i18n)** — hỗ trợ chuyển đổi Tiếng Việt / English bằng `MessageSource`.

### 1.3. Đặc tả chi tiết từng chức năng

#### Chức năng 1 — Danh sách sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/home` |
| Nguồn dữ liệu (API) | [products-all](https://dummyjson.com/docs/products#products-all) |
| Template tham khảo | [Fruitables — shop.html](https://themewagon.github.io/fruitables/shop.html) |
| Hướng dẫn | Hiển thị hình ảnh, tên, giá, miêu tả của mỗi sản phẩm lấy từ nguồn dữ liệu |
| Gợi ý | Dùng Thymeleaf với các tag `th:each`, `th:text`, `th:src` để đổ dữ liệu ra view |

#### Chức năng 2 — Danh sách nhóm sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/category` |
| Nguồn dữ liệu (API) | [products-categories](https://dummyjson.com/docs/products#products-categories) |
| Template tham khảo | [Fruitables — shop.html](https://themewagon.github.io/fruitables/shop.html) |
| Hướng dẫn | Hiển thị danh sách nhóm sản phẩm lấy từ nguồn dữ liệu |
| Gợi ý | Dùng Thymeleaf với tag `th:each` để hiển thị dữ liệu ra view |

#### Chức năng 3 — Chi tiết sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/product_detail?id=xxx` |
| Nguồn dữ liệu (API) | [products-single](https://dummyjson.com/docs/products#products-single) |
| Template tham khảo | [Fruitables — shop-detail.html](https://themewagon.github.io/fruitables/shop-detail.html) |
| Hướng dẫn | Hiển thị chi tiết của 1 sản phẩm lấy từ nguồn dữ liệu (lưu ý mỗi sản phẩm có id khác nhau) |
| Gợi ý | Lấy id từ URL → lấy thông tin sản phẩm từ nguồn dữ liệu → hiển thị nhiều thông tin nhất có thể |

#### Chức năng 4 — Tìm kiếm sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/product_search?keyword=xxx` |
| Nguồn dữ liệu (API) | [products-search](https://dummyjson.com/docs/products#products-search) |
| Template tham khảo | [Fruitables — shop.html](https://themewagon.github.io/fruitables/shop.html) |
| Hướng dẫn | Người dùng nhập 1 từ khóa → tìm tất cả sản phẩm chứa từ khóa và hiển thị danh sách. Nếu không có kết quả → hiển thị trang trống kèm thông báo "không tìm thấy sản phẩm" |
| Gợi ý | Tạo hàm JavaScript nhận từ khóa; nếu từ khóa **> 2 ký tự** → chuyển sang trang `/product_search?keyword=xxx`. Trang search lấy từ khóa từ URL, gửi request đến nguồn dữ liệu, kiểm tra kết quả rồi hiển thị |

#### Chức năng 5 — Thêm sản phẩm mới

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/product_add` |
| Nguồn dữ liệu (API) | [products-add](https://dummyjson.com/docs/products#products-add) |
| Template tham khảo | Khuyến khích học viên tự tìm template cho form này |
| Hướng dẫn | Tạo 1 trang HTML với các textbox để nhập thông tin sản phẩm mới |
| Gợi ý | Tạo hàm JavaScript nhận dữ liệu từ textbox → **xác thực dữ liệu** → nếu hợp lệ gửi request `POST` tới nguồn dữ liệu → thành công thì hiển thị thông báo (có thể dùng `alert`) |

#### Chức năng 6 — Cập nhật sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `http://localhost:8081/product_edit?id=xxx` |
| Nguồn dữ liệu (API) | [products-single](https://dummyjson.com/docs/products#products-single) + [products-update](https://dummyjson.com/docs/products#products-update) |
| Template tham khảo | Khuyến khích học viên tự tìm template cho form này |
| Hướng dẫn | Tạo 1 trang HTML với các textbox để sửa thông tin sản phẩm |
| Gợi ý | Lấy id từ URL → gửi request lấy thông tin sản phẩm → đổ dữ liệu vào textbox → tạo hàm JS nhận dữ liệu → **xác thực dữ liệu** → nếu hợp lệ gửi request `PUT` tới nguồn dữ liệu → thành công thì hiển thị thông báo (có thể dùng `alert`) |

#### Chức năng 7 — Xóa sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `DELETE http://localhost:8081/api/products/{id}` (gọi từ trang chi tiết) |
| Nguồn dữ liệu (API) | [products-delete](https://dummyjson.com/docs/products#products-delete) |
| Hướng dẫn | Thêm nút **Xóa sản phẩm** ở trang chi tiết; hỏi xác nhận trước khi xóa; xóa xong quay về trang chủ |
| Gợi ý | JS gọi `fetch DELETE` qua Spring Proxy → `ProductService.deleteProduct()` → DummyJSON |

#### Chức năng 8 — Phân trang danh sách

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `GET /home?page={p}&size={s}` (áp dụng cho cả `/product_search`) |
| Nguồn dữ liệu (API) | [products-limit-skip](https://dummyjson.com/docs/products#products-limit_skip) |
| Hướng dẫn | Chia danh sách theo trang; hiển thị nút **Trước / Sau** và "Trang X / Y" |
| Gợi ý | `skip = (page - 1) * size`; tổng số trang = `ceil(total / size)` |

#### Chức năng 9 — Sắp xếp sản phẩm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `GET /home?sortBy={title\|price\|rating}&order={asc\|desc}` |
| Nguồn dữ liệu (API) | [products-sort](https://dummyjson.com/docs/products#products-sort) |
| Hướng dẫn | Form chọn tiêu chí (Tên / Giá / Đánh giá) và thứ tự (Tăng / Giảm); áp dụng cho cả trang chủ và tìm kiếm |
| Gợi ý | Truyền `sortBy`/`order` xuống DummyJSON; giữ nguyên nhóm đang lọc khi đổi sắp xếp |

#### Chức năng 10 — Lọc theo nhóm

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `GET /home?category={slug}` |
| Nguồn dữ liệu (API) | [products-by-category](https://dummyjson.com/docs/products#products-category) |
| Hướng dẫn | Bấm 1 nhóm ở trang Category để xem sản phẩm thuộc nhóm đó; có nút **Bỏ lọc nhóm** |
| Gợi ý | Service gọi `/products/category/{slug}`; kết hợp được với sắp xếp & phân trang |

#### Chức năng 11 — Validation phía server

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `POST` / `PUT` `/api/products` |
| Nguồn dữ liệu (API) | (nội bộ — Spring Proxy) |
| Hướng dẫn | Kiểm tra dữ liệu trên server bằng Bean Validation, chặn được cả khi người dùng tắt JavaScript hoặc gọi thẳng API |
| Gợi ý | `@Valid` + `@NotBlank`/`@NotNull`/`@DecimalMin`/`@DecimalMax`; lỗi trả JSON `400` để JS hiển thị tại từng ô |

#### Chức năng 12 — Trang lỗi thân thiện

| Mục | Nội dung |
|-----|----------|
| Đường dẫn | `error/404`, `error/500` |
| Nguồn dữ liệu (API) | — |
| Hướng dẫn | Hiển thị trang 404 khi không tìm thấy sản phẩm; trang 500 khi lỗi hệ thống / gọi API thất bại |
| Gợi ý | `@ControllerAdvice` (web) + `@RestControllerAdvice` (API); ném `ProductNotFoundException` khi id không tồn tại |

### 1.4. Yêu cầu xác thực dữ liệu (form Thêm / Sửa)

- Tất cả textbox **không được rỗng**
- **Giá**: từ **1 – 200 \$**
- **Mô tả**: dài tối đa **255 từ**

> Các quy tắc trên được kiểm tra ở **2 lớp**: JavaScript phía trình duyệt (chức năng 5, 6) và Bean Validation phía server (chức năng 11). Chi tiết kỹ thuật xem mục [2 — Quy tắc validation](#quy-tắc-validation-form-thêm--sửa).

### 1.5. Ràng buộc & nguồn tham khảo

| Mục | Yêu cầu |
|-----|---------|
| Nguồn dữ liệu | [DummyJSON Products API](https://dummyjson.com/docs/products) |
| Cổng (port) chạy ứng dụng | `8081` |
| Giao diện tham khảo | [Theme Fruitables](https://themewagon.com/themes/fruitables-free/) |
| Phạm vi kiến thức | Tổng hợp Module 2: Spring Boot, Spring MVC, Thymeleaf, gọi REST API bên ngoài |

---

## 2. Phân tích & thiết kế giải pháp

### 2.1. Danh sách tính năng

| # | Tính năng | URL / Cách dùng | Triển khai |
|---|-----------|-----------------|------------|
| 1 | Danh sách sản phẩm | `GET /home` | Hiển thị hình, tên, giá, mô tả, nhóm sản phẩm |
| 2 | Nhóm sản phẩm | `GET /category` | Hiển thị danh sách category từ API |
| 3 | Chi tiết sản phẩm | `GET /product_detail?id={id}` | Hiển thị đầy đủ thông tin một sản phẩm |
| 4 | Tìm kiếm | `GET /product_search?keyword={kw}` | Tìm theo tên; không có kết quả → thông báo trống |
| 5 | Thêm sản phẩm | `GET /product_add` → `POST /api/products` | Form nhập liệu + validation client & server |
| 6 | Cập nhật sản phẩm | `GET /product_edit?id={id}` → `PUT /api/products/{id}` | Form pre-fill + validation client & server |
| 7 | Xóa sản phẩm | `DELETE /api/products/{id}` | Nút "Xóa sản phẩm" ở trang chi tiết → `product-detail.js` gọi proxy → `ProductService.deleteProduct()` |
| 8 | Phân trang | `GET /home?page={p}&size={s}` | Map sang `limit`/`skip` của DummyJSON; thanh phân trang Trước/Sau ở `home.html`, `product-search.html` |
| 9 | Sắp xếp | `GET /home?sortBy={title\|price\|rating}&order={asc\|desc}` | Map sang `sortBy`/`order` của DummyJSON; form chọn sắp xếp trên trang danh sách & tìm kiếm |
| 10 | Lọc theo nhóm | `GET /home?category={slug}` | Trang Category → mỗi nhóm dẫn tới `/home?category=...`; service gọi `/products/category/{slug}` |
| 11 | Validation server | `POST/PUT /api/products` | `@Valid` + Bean Validation trên `ProductRequest`; lỗi trả JSON 400, JS hiển thị tại từng ô |
| 12 | Trang lỗi thân thiện | `error/404`, `error/500` | `@ControllerAdvice` (web) + `@RestControllerAdvice` (API); 404 khi không tìm thấy sản phẩm |

> Các tham số `page`, `size`, `sortBy`, `order`, `category` đều **tùy chọn** và có thể kết hợp với nhau (vd. `/home?category=beauty&sortBy=price&order=desc&page=2`).

### Quy tắc validation (form Thêm / Sửa)

Validation được kiểm tra **2 lớp**:

- **Lớp 1 — JavaScript (client):** chặn ngay trên trình duyệt trước khi gửi request.
- **Lớp 2 — Bean Validation (server):** `ProductApiController` dùng `@Valid`, dù tắt JS hay gọi thẳng API vẫn bị chặn.

Quy tắc:

- Tất cả trường nhập **không được rỗng** (`@NotBlank`)
- **Giá**: từ 1 – 200 USD (`@NotNull`, `@DecimalMin(1)`, `@DecimalMax(200)`)
- **Mô tả**: tối đa **255 từ** (kiểm tra phía JavaScript)
- Thành công → hiển thị `alert()` và chuyển về trang chi tiết
- Lỗi server → trả HTTP 400 kèm JSON `{ tên trường: thông báo }`, JS hiển thị ngay tại ô nhập

### Luồng kiến trúc

```
[Trình duyệt]
    │
    ├─ GET /home, /category, /product_detail, /product_search
    │       (kèm tham số page, size, sortBy, order, category)
    │       → ProductWebController → ProductService → DummyJSON → Thymeleaf render
    │
    └─ POST / PUT / DELETE /api/products (Spring Proxy)
            → ProductApiController (@Valid) → ProductService → DummyJSON

[Xử lý lỗi]
    ├─ Web:  WebExceptionHandler (@ControllerAdvice)     → trang error/404, error/500
    └─ API:  ApiExceptionHandler (@RestControllerAdvice) → JSON 400 (validation), 502 (API lỗi)
```

---

## 3. Công nghệ sử dụng

| Công nghệ | Phiên bản / Ghi chú | Vai trò |
|-----------|---------------------|---------|
| Java | 17 | Ngôn ngữ chính |
| Spring Boot | 3.5.15 | Framework, embedded Tomcat |
| Spring MVC | (starter-web) | `@Controller`, routing, request param |
| Thymeleaf | (starter-thymeleaf) | Server-side rendering HTML |
| RestTemplate | Spring Web | HTTP client gọi DummyJSON |
| Lombok | optional | Giảm boilerplate (`@Data`, `@RequiredArgsConstructor`) |
| Bean Validation | starter-validation | Validation server-side cho `ProductRequest` (`@Valid`) |
| Bootstrap | 5.3.3 (CDN) | UI responsive, theme Fruitables |
| JavaScript | ES6+ | Search redirect, form validation, fetch API |
| Maven | mvnw wrapper | Build & dependency management |
| DummyJSON | External REST API | Nguồn dữ liệu sản phẩm (mock POST/PUT/DELETE) |

**Không sử dụng:** Database, JPA, Spring Security, React/Vue (SSR bằng Thymeleaf).

---

## 4. Cấu trúc thư mục

```
mini-project-bai9/
├── CHECKLIST.md                          ← Checklist công việc & kiểm thử
└── java-springboot-bai9/
    ├── pom.xml                           ← Dependencies Maven
    ├── mvnw, mvnw.cmd                    ← Maven Wrapper
    └── src/
        ├── main/
        │   ├── java/vn/demo/
        │   │   ├── MiniProjectBai9Application.java   ← Entry point Spring Boot
        │   │   ├── ServletInitializer.java           ← Deploy WAR trên Tomcat (tùy chọn)
        │   │   │
        │   │   ├── config/
        │   │   │   └── RestTemplateConfig.java       ← Bean RestTemplate (timeout 10s)
        │   │   │
        │   │   ├── controller/
        │   │   │   └── ProductWebController.java   ← @Controller: trang web Thymeleaf (phân trang/sắp xếp/lọc)
        │   │   │
        │   │   ├── controller/api/
        │   │   │   └── ProductApiController.java     ← @RestController: Spring Proxy add/edit/delete (@Valid)
        │   │   │
        │   │   ├── service/
        │   │   │   └── ProductService.java         ← Business logic + gọi DummyJSON
        │   │   │
        │   │   ├── exception/
        │   │   │   ├── ProductNotFoundException.java  ← Ngoại lệ khi không tìm thấy sản phẩm
        │   │   │   ├── WebExceptionHandler.java       ← @ControllerAdvice → trang 404/500
        │   │   │   └── ApiExceptionHandler.java       ← @RestControllerAdvice → JSON 400/502
        │   │   │
        │   │   ├── model/
        │   │   │   ├── Product.java                  ← Entity map JSON sản phẩm
        │   │   │   └── Category.java                 ← Entity map JSON category
        │   │   │
        │   │   └── dto/
        │   │       ├── ProductListResponse.java      ← Wrapper danh sách SP (products, total, skip, limit)
        │   │       └── ProductRequest.java           ← Body request thêm/sửa SP (+ Bean Validation)
        │   │
        │   └── resources/
        │       ├── application.properties            ← server.port=8081
        │       ├── static/
        │       │   ├── css/style.css                 ← Custom CSS theme Fruitables
        │       │   └── js/
        │       │       ├── search.js                 ← Tìm kiếm: keyword > 2 ký tự → redirect
        │       │       ├── product-validation.js     ← Validation + hiển thị lỗi server (add/edit)
        │       │       ├── product-add.js            ← fetch POST /api/products
        │       │       ├── product-edit.js           ← fetch PUT /api/products/{id}
        │       │       └── product-detail.js         ← fetch DELETE /api/products/{id}
        │       └── templates/
        │           ├── fragments/layout.html         ← Navbar, footer, search box, scripts
        │           ├── error/
        │           │   ├── 404.html                  ← Trang lỗi không tìm thấy
        │           │   └── 500.html                  ← Trang lỗi hệ thống
        │           ├── home.html                     ← Danh sách SP (+ sắp xếp, phân trang, lọc nhóm)
        │           ├── category.html                 ← Danh sách nhóm SP (link lọc sản phẩm)
        │           ├── product-detail.html           ← Chi tiết SP (+ nút Xóa)
        │           ├── product-search.html             ← Kết quả tìm kiếm (+ sắp xếp, phân trang)
        │           ├── product-add.html                ← Form thêm SP
        │           └── product-edit.html               ← Form sửa SP (pre-fill)
        │
        └── test/
            └── java/vn/demo/
                └── MiniProjectBai9ApplicationTests.java  ← Context load test
```

### Giải thích phân tách package

| Package / Thư mục | Vai trò |
|-------------------|---------|
| `config/` | Cấu hình bean dùng chung (RestTemplate) |
| `controller/` | Xử lý request web, trả về view Thymeleaf |
| `controller/api/` | REST API nội bộ — proxy ghi dữ liệu ra DummyJSON |
| `service/` | Business logic, gọi API ngoài, xử lý lỗi |
| `exception/` | Ngoại lệ tùy biến + bộ xử lý lỗi tập trung (`@ControllerAdvice`/`@RestControllerAdvice`) |
| `model/` | Object map trực tiếp JSON response từ DummyJSON |
| `dto/` | Object request/response cho API và form |
| `templates/` | File HTML Thymeleaf (`.html`), gồm cả `error/` |
| `static/` | CSS, JavaScript, hình ảnh tĩnh |

---

## 5. Cách sử dụng

### Yêu cầu hệ thống

- **JDK 17** trở lên
- Kết nối internet (gọi DummyJSON API)
- Port **8081** chưa bị chiếm

### Chạy project

```bash
cd mini-project-bai9/java-springboot-bai9
./mvnw spring-boot:run
```

Hoặc Run class `MiniProjectBai9Application` trong IntelliJ IDEA.

Mở trình duyệt: **http://localhost:8081/home**

### Chạy test

```bash
./mvnw test
```

### Xử lý port bị chiếm

```bash
# Xem process đang dùng port 8081
lsof -i :8081

# Tắt process
lsof -ti :8081 | xargs kill
```

Hoặc đổi port trong `src/main/resources/application.properties`:

```properties
server.port=8082
```

### Hướng dẫn sử dụng từng tính năng

#### 1. Xem danh sách sản phẩm

Truy cập `http://localhost:8081/home` — hiển thị grid sản phẩm với hình, tên, giá, mô tả rút gọn. Bấm **Chi tiết** để xem thêm.

- **Sắp xếp:** chọn tiêu chí (Tên / Giá / Đánh giá) và thứ tự (Tăng / Giảm) rồi bấm **Áp dụng**.
- **Phân trang:** dùng nút **Trước / Sau** ở cuối trang. Có thể chỉnh URL: `?page=2&size=12`.

#### 2. Xem nhóm sản phẩm

Truy cập `http://localhost:8081/category` hoặc bấm **Nhóm sản phẩm** trên navbar. Bấm **Xem sản phẩm** ở mỗi nhóm để xem danh sách sản phẩm thuộc nhóm đó (`/home?category={slug}`).

#### 3. Xem chi tiết sản phẩm

```
http://localhost:8081/product_detail?id=1
```

Hiển thị: hình ảnh, giá, thương hiệu, đánh giá, tồn kho, bảo hành, vận chuyển… Bấm **Sửa sản phẩm** để chỉnh sửa, hoặc **Xóa sản phẩm** để xóa (có hộp thoại xác nhận, sau đó về trang chủ).

Nếu id không tồn tại → hiển thị **trang 404 thân thiện**.

#### 4. Tìm kiếm sản phẩm

1. Nhập từ khóa vào ô tìm kiếm trên navbar (phải **> 2 ký tự**)
2. Bấm **Tìm kiếm** hoặc nhấn Enter
3. Chuyển đến `/product_search?keyword=...`
4. Nếu không có kết quả → hiển thị "Không tìm thấy sản phẩm"
5. Kết quả tìm kiếm cũng hỗ trợ **sắp xếp** và **phân trang** như trang chủ

Ví dụ: `http://localhost:8081/product_search?keyword=apple`

#### 5. Thêm sản phẩm mới

1. Truy cập `http://localhost:8081/product_add` hoặc bấm **Thêm sản phẩm**
2. Điền đầy đủ các trường (tên, mô tả, giá, nhóm, URL hình, thương hiệu)
3. Bấm **Thêm sản phẩm**
4. JS validate (client) → gửi `POST /api/products` → server validate lại bằng `@Valid` → Spring proxy → DummyJSON
5. Thành công → `alert` hiển thị ID mới → chuyển trang chi tiết. Nếu dữ liệu sai → server trả `400`, lỗi hiển thị tại từng ô

#### 6. Cập nhật sản phẩm

1. Từ trang chi tiết, bấm **Sửa sản phẩm**
2. Hoặc truy cập `http://localhost:8081/product_edit?id=1`
3. Form tự động điền dữ liệu hiện tại
4. Sửa và bấm **Cập nhật**
5. JS validate → gửi `PUT /api/products/{id}` → Spring proxy → DummyJSON
6. Thành công → `alert` → chuyển trang chi tiết

#### 7. Xóa sản phẩm

1. Từ trang chi tiết, bấm **Xóa sản phẩm**
2. Xác nhận trong hộp thoại
3. JS gửi `DELETE /api/products/{id}` → Spring proxy → DummyJSON
4. Thành công → `alert` → quay về trang chủ

### API nội bộ (Spring Proxy)

Dùng bởi JavaScript trên form, có thể test bằng Postman:

| Method | URL | Body (JSON) |
|--------|-----|-------------|
| POST | `http://localhost:8081/api/products` | `{ "title", "description", "price", "category", "thumbnail", "brand" }` |
| PUT | `http://localhost:8081/api/products/{id}` | Giống POST |
| DELETE | `http://localhost:8081/api/products/{id}` | Không cần body |

> **Lưu ý:** DummyJSON chỉ **giả lập** POST/PUT/DELETE — dữ liệu không được lưu/xóa vĩnh viễn trên server DummyJSON.

> **Validation server:** nếu gửi dữ liệu sai (rỗng, giá ngoài 1–200), API trả về **HTTP 400** kèm JSON dạng `{ "price": "Giá phải từ 1 đến 200 USD.", ... }`.

---

## 6. Liên kết

- Syllabus đồ án: [`syllabus/module-2/java_m2_bai9_Project-2.pdf`](../../syllabus/module-2/java_m2_bai9_Project-2.pdf)
- Checklist chi tiết: [`../CHECKLIST.md`](../CHECKLIST.md)
- Demo bài 6 (nền tảng): [`demo-bai6-springmvc/java-springboot-bai6`](../../demo-bai6-springmvc/java-springboot-bai6)
- Theme tham khảo: [Fruitables](https://themewagon.github.io/fruitables/shop.html)
- API nguồn: [DummyJSON Products](https://dummyjson.com/docs/products)
