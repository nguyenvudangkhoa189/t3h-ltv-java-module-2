# Mini Project Bài 9 — Website bán hàng trực tuyến

Ứng dụng web bán hàng xây dựng bằng **Spring Boot + Thymeleaf**, lấy dữ liệu sản phẩm từ [DummyJSON API](https://dummyjson.com/docs/products). Các thao tác thêm/sửa sản phẩm đi qua **Spring Proxy** (backend gọi API ngoài, không gọi thẳng từ trình duyệt).

---

## Yêu cầu & tính năng

### Bối cảnh

Công ty A cần một website bán hàng trực tuyến với sản phẩm đa dạng về chủng loại và giá cả. Dự án tổng hợp kiến thức Module 2: Spring Boot, Spring MVC, Thymeleaf, gọi REST API bên ngoài.

### Tính năng

| # | Tính năng | URL | Mô tả |
|---|-----------|-----|-------|
| 1 | Danh sách sản phẩm | `GET /home` | Hiển thị hình, tên, giá, mô tả, nhóm sản phẩm |
| 2 | Nhóm sản phẩm | `GET /category` | Hiển thị danh sách category từ API |
| 3 | Chi tiết sản phẩm | `GET /product_detail?id={id}` | Hiển thị đầy đủ thông tin một sản phẩm |
| 4 | Tìm kiếm | `GET /product_search?keyword={kw}` | Tìm theo tên; không có kết quả → thông báo trống |
| 5 | Thêm sản phẩm | `GET /product_add` | Form nhập liệu + validation JavaScript |
| 6 | Cập nhật sản phẩm | `GET /product_edit?id={id}` | Form pre-fill dữ liệu + validation JavaScript |

### Quy tắc validation (form Thêm / Sửa)

- Tất cả trường nhập **không được rỗng**
- **Giá**: từ 1 – 200 USD
- **Mô tả**: tối đa **255 từ**
- Thành công → hiển thị `alert()` và chuyển về trang chi tiết

### Luồng kiến trúc

```
[Trình duyệt]
    │
    ├─ GET /home, /category, /product_detail, /product_search
    │       → ProductWebController → ProductService → DummyJSON → Thymeleaf render
    │
    └─ POST/PUT /api/products (Spring Proxy)
            → ProductApiController → ProductService → DummyJSON
```

---

## Công nghệ sử dụng

| Công nghệ | Phiên bản / Ghi chú | Vai trò |
|-----------|---------------------|---------|
| Java | 17 | Ngôn ngữ chính |
| Spring Boot | 3.5.15 | Framework, embedded Tomcat |
| Spring MVC | (starter-web) | `@Controller`, routing, request param |
| Thymeleaf | (starter-thymeleaf) | Server-side rendering HTML |
| RestTemplate | Spring Web | HTTP client gọi DummyJSON |
| Lombok | optional | Giảm boilerplate (`@Data`, `@RequiredArgsConstructor`) |
| Bean Validation | starter-validation | Sẵn sàng cho validation server-side |
| Bootstrap | 5.3.3 (CDN) | UI responsive, theme Fruitables |
| JavaScript | ES6+ | Search redirect, form validation, fetch API |
| Maven | mvnw wrapper | Build & dependency management |
| DummyJSON | External REST API | Nguồn dữ liệu sản phẩm (mock POST/PUT) |

**Không sử dụng:** Database, JPA, Spring Security, React/Vue (SSR bằng Thymeleaf).

---

## Cấu trúc thư mục

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
        │   │   │   └── ProductWebController.java   ← @Controller: trang web Thymeleaf
        │   │   │
        │   │   ├── controller/api/
        │   │   │   └── ProductApiController.java     ← @RestController: Spring Proxy add/edit
        │   │   │
        │   │   ├── service/
        │   │   │   └── ProductService.java         ← Business logic + gọi DummyJSON
        │   │   │
        │   │   ├── model/
        │   │   │   ├── Product.java                  ← Entity map JSON sản phẩm
        │   │   │   └── Category.java                 ← Entity map JSON category
        │   │   │
        │   │   └── dto/
        │   │       ├── ProductListResponse.java      ← Wrapper danh sách SP (products, total)
        │   │       └── ProductRequest.java           ← Body request thêm/sửa SP
        │   │
        │   └── resources/
        │       ├── application.properties            ← server.port=8081
        │       ├── static/
        │       │   ├── css/style.css                 ← Custom CSS theme Fruitables
        │       │   └── js/
        │       │       ├── search.js                 ← Tìm kiếm: keyword > 2 ký tự → redirect
        │       │       ├── product-validation.js     ← Validation dùng chung add/edit
        │       │       ├── product-add.js            ← fetch POST /api/products
        │       │       └── product-edit.js           ← fetch PUT /api/products/{id}
        │       └── templates/
        │           ├── fragments/layout.html         ← Navbar, footer, search box, scripts
        │           ├── home.html                     ← Danh sách sản phẩm
        │           ├── category.html                 ← Danh sách nhóm SP
        │           ├── product-detail.html           ← Chi tiết sản phẩm
        │           ├── product-search.html             ← Kết quả tìm kiếm
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
| `model/` | Object map trực tiếp JSON response từ DummyJSON |
| `dto/` | Object request/response cho API và form |
| `templates/` | File HTML Thymeleaf (`.html`) |
| `static/` | CSS, JavaScript, hình ảnh tĩnh |

---

## Cách sử dụng

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

#### 2. Xem nhóm sản phẩm

Truy cập `http://localhost:8081/category` hoặc bấm **Nhóm sản phẩm** trên navbar.

#### 3. Xem chi tiết sản phẩm

```
http://localhost:8081/product_detail?id=1
```

Hiển thị: hình ảnh, giá, thương hiệu, đánh giá, tồn kho, bảo hành, vận chuyển… Bấm **Sửa sản phẩm** để chỉnh sửa.

#### 4. Tìm kiếm sản phẩm

1. Nhập từ khóa vào ô tìm kiếm trên navbar (phải **> 2 ký tự**)
2. Bấm **Tìm kiếm** hoặc nhấn Enter
3. Chuyển đến `/product_search?keyword=...`
4. Nếu không có kết quả → hiển thị "Không tìm thấy sản phẩm"

Ví dụ: `http://localhost:8081/product_search?keyword=apple`

#### 5. Thêm sản phẩm mới

1. Truy cập `http://localhost:8081/product_add` hoặc bấm **Thêm sản phẩm**
2. Điền đầy đủ các trường (tên, mô tả, giá, nhóm, URL hình, thương hiệu)
3. Bấm **Thêm sản phẩm**
4. JS validate → gửi `POST /api/products` → Spring proxy → DummyJSON
5. Thành công → `alert` hiển thị ID mới → chuyển trang chi tiết

#### 6. Cập nhật sản phẩm

1. Từ trang chi tiết, bấm **Sửa sản phẩm**
2. Hoặc truy cập `http://localhost:8081/product_edit?id=1`
3. Form tự động điền dữ liệu hiện tại
4. Sửa và bấm **Cập nhật**
5. JS validate → gửi `PUT /api/products/{id}` → Spring proxy → DummyJSON
6. Thành công → `alert` → chuyển trang chi tiết

### API nội bộ (Spring Proxy)

Dùng bởi JavaScript trên form, có thể test bằng Postman:

| Method | URL | Body (JSON) |
|--------|-----|-------------|
| POST | `http://localhost:8081/api/products` | `{ "title", "description", "price", "category", "thumbnail", "brand" }` |
| PUT | `http://localhost:8081/api/products/{id}` | Giống POST |

> **Lưu ý:** DummyJSON chỉ **giả lập** POST/PUT — dữ liệu không được lưu vĩnh viễn trên server DummyJSON.

---

## Liên kết

- Syllabus đồ án: [`syllabus/module-2/java_m2_bai9_Project-2.pdf`](../../syllabus/module-2/java_m2_bai9_Project-2.pdf)
- Checklist chi tiết: [`../CHECKLIST.md`](../CHECKLIST.md)
- Demo bài 6 (nền tảng): [`demo-bai6-springmvc/java-springboot-bai6`](../../demo-bai6-springmvc/java-springboot-bai6)
- Theme tham khảo: [Fruitables](https://themewagon.github.io/fruitables/shop.html)
- API nguồn: [DummyJSON Products](https://dummyjson.com/docs/products)
