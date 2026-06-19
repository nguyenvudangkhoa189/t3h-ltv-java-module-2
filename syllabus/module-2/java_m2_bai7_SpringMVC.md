# Bài 7: Spring Boot MVC (part 3a) — Upload file & Gọi External API

## Mục tiêu bài học

Sau bài này, học viên có thể:

- Upload file từ client lên server bằng `MultipartFile` và cấu hình giới hạn dung lượng
- Lưu file an toàn (đổi tên UUID, kiểm tra loại file) trong **Service layer**
- Cấu hình `ResourceHandler` để truy cập file upload qua URL trên trình duyệt
- Gọi REST API bên ngoài bằng **`RestClient`** (Spring Boot 3.x)
- Đọc JSON từ API ngoài bằng Jackson (`JsonNode`) và trả về qua `@RestController`
- Test upload và external API bằng **Postman**
- Áp dụng quy ước code **enterprise**: Lombok, constructor injection, `@Value`, logging

## Điều kiện tiên quyết

- Đã hoàn thành **Bài 5 (part 1)**: `@RestController`, `ResponseEntity`, HTTP method, `@RequestParam`
- Đã hoàn thành **Bài 6 (part 2)**: `@Service`, `@RequiredArgsConstructor`, Lombok
- Biết Java cơ bản: class, exception, `List`
- Project có dependency **`spring-boot-starter-web`**, **Lombok**
- Đã cài **Postman**: [postman.com/downloads](https://www.postman.com/downloads/)
- Máy có kết nối Internet (gọi DummyJSON)

> **Ghi chú:** Bài này chỉ dùng **`@RestController`** (trả JSON / chuỗi). Phần giao diện HTML (Thymeleaf CRUD) học ở **[Bài 8](./java_m2_bai8_SpringMVC.md)**.

## Nội dung

| # | Chủ đề |
|---|--------|
| 0 | Quy ước code enterprise & dependencies |
| 1 | Upload file với `MultipartFile` |
| 2 | Gọi API bên ngoài với `RestClient` |
| 3 | Thực hành & checkpoint |
| 4 | Lỗi thường gặp |
| Phụ lục | Bài tập · Checklist · Liên kết |

---

## 0. Quy ước code enterprise & dependencies

### 0.1. Dependencies (`pom.xml`)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

> Bật **Annotation Processing** cho Lombok trong IntelliJ *(Settings → Build → Compiler → Annotation Processors)*.

### 0.2. Quy ước inject dependency *(ôn Bài 6)*

| Quy ước | Cách làm | Không làm |
|---------|----------|-----------|
| Inject Service | `@RequiredArgsConstructor` + field `final` | `new FileStorageService()` |
| Controller | `@RequiredArgsConstructor` — Spring inject qua constructor | `@Autowired` trên field |
| Config value | `@Value("${...}")` đọc từ `application.properties` | Hard-code path / URL trong code |
| Log lỗi | `@Slf4j` + `log.error(...)` | `e.printStackTrace()` |

```java
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;   // final → Lombok sinh constructor
}
```

### 0.3. Quy ước chia package theo **từng phần demo**

Thay vì gom tất cả vào `controller` / `service` chung (chia theo layer), bài này chia package theo **feature** — nhìn tên package biết ngay code thuộc phần nào:

| Package | Phần | Bên trong |
|---------|------|-----------|
| `com.example.demo.upload` | Phần 1 — Upload file | `config` / `controller` / `dto` / `service` |
| `com.example.demo.external` | Phần 2 — External API | `config` / `controller` / `service` |
| `com.example.demo.homework` | Bài tập về nhà | `controller` / `service` |

> Bài 7 không có package `model` vì dữ liệu API ngoài được đọc bằng `JsonNode`. Package `homework` đứng riêng và **tái dùng** bean dùng chung (`FileStorageService`, `RestClient`, `DummyJsonProperties`) — xem [Phụ lục](#bài-tập).

---

## 1. Upload file với `MultipartFile`

### 1.1. Mục đích

- Cho phép client gửi file (ảnh, tài liệu, …) từ máy tính lên server
- Server lưu file và trả về đường dẫn để hiển thị hoặc tải về sau này
- Ví dụ thực tế: upload avatar, đính kèm hóa đơn, upload ảnh sản phẩm

```mermaid
flowchart LR
    A["Postman / Browser"] -->|POST multipart/form-data| B["@RestController"]
    B --> C["FileStorageService"]
    C --> D["Thư mục trên ổ đĩa"]
    D --> E["Trả URL /uploads/..."]
    E --> F["Mở URL trên trình duyệt"]
```

### 1.2. Cấu hình `application.properties`

```properties
# Giới hạn kích thước file upload
spring.servlet.multipart.max-file-size=2MB
spring.servlet.multipart.max-request-size=5MB

# Thư mục lưu file — NGOÀI source code (khuyến nghị)
app.upload.dir=${user.home}/demo-uploads

# Base URL DummyJSON — dễ đổi môi trường (dev/staging)
app.external.dummyjson.base-url=https://dummyjson.com
```

> **Vì sao không lưu vào `src/main/resources/static/`?** Thư mục đó nằm trong project — phù hợp demo nhanh, nhưng file sẽ mất khi build/deploy lại. Production thường lưu thư mục riêng hoặc cloud (S3, …).

### 1.3. FileStorageService

Tách logic lưu file ra **Service** — Controller chỉ nhận request và trả response.  
Đọc đường dẫn upload bằng **`@Value`** — đủ cho bài học; chưa cần `@ConfigurationProperties`.

```java
package com.example.demo.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadRoot);
        log.info("Upload root initialized: {}", uploadRoot);
    }

    /**
     * Lưu file vào subFolder (vd: "misc", "avatars") và trả URL public.
     */
    public String store(MultipartFile file, String subFolder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh: JPEG, PNG, GIF, WEBP");
        }

        String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0) {
            extension = originalName.substring(dot);
        }

        String savedName = UUID.randomUUID() + extension;
        Path targetDir = uploadRoot.resolve(subFolder);
        Files.createDirectories(targetDir);
        Path targetFile = targetDir.resolve(savedName);

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        String publicUrl = "/uploads/" + subFolder + "/" + savedName;
        log.debug("Stored file: {} → {}", originalName, publicUrl);
        return publicUrl;
    }
}
```

### 1.4. FileUploadResponse — DTO trả về API

```java
package com.example.demo.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResponse {

    private String url;
}
```

### 1.5. UploadResourceConfig — cho trình duyệt xem được ảnh

```java
package com.example.demo.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
```

### 1.6. FileUploadController

```java
package com.example.demo.upload.controller;

import com.example.demo.upload.dto.FileUploadResponse;
import com.example.demo.upload.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.store(file, "misc");
            return ResponseEntity.ok(new FileUploadResponse(url));
        } catch (IllegalArgumentException e) {
            log.warn("Upload rejected: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
```

| Thành phần | Vai trò |
|------------|---------|
| `@PostMapping` | File lớn — không dùng GET |
| `@RequestParam("file")` | Tên key phải khớp Postman / form HTML |
| `MultipartFile` | Kiểu dữ liệu file upload trong Spring |
| `ResponseEntity<?>` | Trả URL thành công hoặc message lỗi |

### 1.7. Test bằng Postman

| Bước | Thiết lập |
|------|-----------|
| 1 | Method: `POST` |
| 2 | URL: `http://localhost:8080/api/files/upload` |
| 3 | Body → **form-data** |
| 4 | Key: `file` — đổi type sang **File** → chọn ảnh |
| 5 | Send → nhận `200 OK` + JSON `{"url":"/uploads/misc/uuid.jpg"}` |
| 6 | Mở trình duyệt: `http://localhost:8080/uploads/misc/uuid.jpg` |

### 1.8. Ghi nhớ cho form HTML *(dùng ở Bài 8)*

```html
<form method="post" enctype="multipart/form-data" action="/api/files/upload">
    <input type="file" name="file" accept="image/*"/>
    <button type="submit">Upload</button>
</form>
```

| Thuộc tính | Vì sao bắt buộc |
|------------|-----------------|
| `method="post"` | Không gửi file bằng GET |
| `enctype="multipart/form-data"` | Thiếu → server không nhận file |
| `name="file"` | Khớp `@RequestParam("file")` |

---

## 2. Gọi API bên ngoài với `RestClient`

### 2.1. Mục đích

Từ **server Java** gửi HTTP request tới dịch vụ khác để lấy hoặc gửi dữ liệu.

| Ví dụ | Hướng |
|-------|-------|
| Lấy danh sách sản phẩm đối tác | Server → External API (GET) |
| Gửi thanh toán Momo | Server → Payment API (POST) |
| Lấy tỷ giá, thời tiết | Server → Public API (GET) |

**Cần biết khi kết nối:**

- URL API (endpoint)
- HTTP method (GET, POST, …)
- Tham số, header, body *(tuỳ API)*
- API Key / token *(nếu có — lưu trong `application.properties`, không hard-code)*

### 2.2. Không cần thêm thư viện JSON

`spring-boot-starter-web` đã có **Jackson**. Dùng `JsonNode` hoặc class Java — **không** cần `org.json`.

### 2.3. RestClientConfig

```java
package com.example.demo.external.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
```

### 2.4. DummyJsonProperties

```java
package com.example.demo.external.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.external.dummyjson")
public class DummyJsonProperties {

    private String baseUrl = "https://dummyjson.com";
}
```

### 2.5. ExternalApiService

**DummyJSON** — API công khai miễn phí để học: [dummyjson.com](https://dummyjson.com/)

```java
package com.example.demo.external.service;

import com.example.demo.external.config.DummyJsonProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final RestClient restClient;
    private final DummyJsonProperties dummyJsonProperties;

    public JsonNode fetchProducts(int limit) {
        String url = dummyJsonProperties.getBaseUrl() + "/products?limit={limit}";
        log.debug("Fetching products: limit={}", limit);
        return restClient.get().uri(url, limit).retrieve().body(JsonNode.class);
    }

    public JsonNode fetchCategories() {
        String url = dummyJsonProperties.getBaseUrl() + "/products/categories";
        return restClient.get().uri(url).retrieve().body(JsonNode.class);
    }

    public JsonNode fetchUsers(int limit) {
        String url = dummyJsonProperties.getBaseUrl() + "/users?limit={limit}";
        return restClient.get().uri(url, limit).retrieve().body(JsonNode.class);
    }

    public JsonNode fetchUserById(long id) {
        String url = dummyJsonProperties.getBaseUrl() + "/users/{id}";
        return restClient.get().uri(url, id).retrieve().body(JsonNode.class);
    }
}
```

> **Tại sao trả `JsonNode`?** Người mới học chưa cần tạo class mapping phức tạp — trả JSON nguyên bản qua API để quan sát cấu trúc. **Bài 8** tập trung CRUD Thymeleaf với dữ liệu mẫu local — không dùng External API.

### 2.6. ExternalApiController

```java
package com.example.demo.external.controller;

import com.example.demo.external.service.ExternalApiService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/products")
    public ResponseEntity<JsonNode> getProducts(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(externalApiService.fetchProducts(limit));
    }

    @GetMapping("/categories")
    public ResponseEntity<JsonNode> getCategories() {
        return ResponseEntity.ok(externalApiService.fetchCategories());
    }

    @GetMapping("/users")
    public ResponseEntity<JsonNode> getUsers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(externalApiService.fetchUsers(limit));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<JsonNode> getUserById(@PathVariable long id) {
        JsonNode user = externalApiService.fetchUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
```

### 2.7. Test bằng Postman

| API | Method | URL |
|-----|--------|-----|
| Danh sách sản phẩm | GET | `http://localhost:8080/api/external/products` |
| Categories | GET | `http://localhost:8080/api/external/categories` |
| Danh sách users | GET | `http://localhost:8080/api/external/users` |
| 1 user theo id | GET | `http://localhost:8080/api/external/users/1` |

**Quan sát JSON trả về** — làm quen cấu trúc dữ liệu từ hệ thống bên ngoài.

### 2.8. DummyJSON không phải database

| Hành động | DummyJSON |
|-----------|-----------|
| `GET` | Trả dữ liệu mẫu — dùng được |
| `POST` / `PUT` / `DELETE` | Chỉ **giả lập** response — **không lưu thật** |

> Trong thực tế: server **đọc** từ API ngoài → **lưu** vào DB nội bộ. Ở **Bài 8** ta dùng dữ liệu mẫu hard-code trong `UserService` để học viên tập trung CRUD + upload avatar, không phụ thuộc mạng.

---

## 3. Thực hành & checkpoint

### 3.1. Cấu trúc project sau Bài 7

```
src/main/java/com/example/demo/
├── DemoApplication.java
├── upload/                          ← Phần 1: Upload file
│   ├── config/
│   │   └── UploadResourceConfig.java
│   ├── controller/
│   │   └── FileUploadController.java
│   ├── dto/
│   │   └── FileUploadResponse.java
│   └── service/
│       └── FileStorageService.java
├── external/                        ← Phần 2: External API
│   ├── config/
│   │   ├── RestClientConfig.java
│   │   └── DummyJsonProperties.java
│   ├── controller/
│   │   └── ExternalApiController.java
│   └── service/
│       └── ExternalApiService.java
└── homework/                        ← Bài tập về nhà (Phụ lục)
    ├── controller/
    │   ├── AvatarUploadController.java
    │   └── ProductCategoryController.java
    └── service/
        └── HomeworkProductService.java

src/main/resources/
└── application.properties
```

### 3.2. Các bước thực hành

1. Tạo project [start.spring.io](https://start.spring.io/) — chọn **Spring Web**, **Lombok**
2. Thêm `application.properties` (mục 1.2)
3. Tạo package theo feature: `upload` (Phần 1) → `external` (Phần 2) → `homework` (Phụ lục). Trong mỗi package copy theo thứ tự: Config → Service → DTO → Controller
4. Run app → test lần lượt bằng Postman (mục 1.7 và 2.7)

### 3.3. Checkpoint — tự kiểm tra trước khi sang Bài 8

- [ ] `POST /api/files/upload` → `200` + đường dẫn `/uploads/misc/...`
- [ ] Mở URL ảnh trên trình duyệt → thấy ảnh
- [ ] Upload file không phải ảnh → `400` + message lỗi
- [ ] `GET /api/external/products` → JSON có mảng `products`
- [ ] `GET /api/external/categories` → JSON mảng tên category
- [ ] `GET /api/external/users` → JSON có mảng `users`
- [ ] Controller dùng `@RequiredArgsConstructor` + field `final` — không `new` Service
- [ ] `app.upload.dir` đọc được qua `@Value` trong `FileStorageService`

---

## 4. Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Cách xử lý |
|-------------|-------------|------------|
| **`413 Payload Too Large`** | File vượt giới hạn | Tăng `spring.servlet.multipart.max-file-size` |
| **`file` null trong Postman** | Chưa chọn type **File** trong form-data | Đổi key `file` sang type File |
| **Ảnh upload 404 trên browser** | Thiếu `UploadResourceConfig` | Kiểm tra mục 1.5 |
| **`Connection refused` / timeout** | Mất mạng hoặc DummyJSON down | Kiểm tra Internet; thử lại sau |
| **`NullPointerException` trong Service** | `new FileStorageService()` thay vì inject | Dùng constructor injection |
| **JSON trả về `null`** | URL sai hoặc API đổi format | Test trực tiếp URL trên browser |
| **404 `/api/external/...`** | Thiếu `@RestController` | Thêm annotation trên Controller |

---

## Tóm tắt

| Khái niệm | Ý chính |
|-----------|---------|
| **`MultipartFile`** | Đại diện file upload từ client |
| **`@RequiredArgsConstructor`** | Lombok sinh constructor inject — chuẩn enterprise |
| **`@Value`** | Đọc config đơn giản từ `application.properties` |
| **`FileStorageService`** | Validate + đổi tên UUID + lưu file |
| **`UploadResourceConfig`** | Map URL `/uploads/**` → thư mục trên ổ đĩa |
| **`RestClient`** | Gọi HTTP API bên ngoài (Spring Boot 3.x) |
| **`JsonNode`** | Đọc JSON linh hoạt — phù hợp người mới |
| **DummyJSON** | API mẫu — chỉ tin `GET` cho dữ liệu thật |

---

## Phụ lục

### Bài tập

> Code bài tập đặt trong package riêng **`com.example.demo.homework`** để tách biệt với phần demo chính. Bài tập **tái dùng** các bean dùng chung: `FileStorageService` (phần upload), `RestClient` + `DummyJsonProperties` (phần external).

#### Bài 1 — Upload nhiều loại thư mục

1. Thêm API `POST /api/files/upload-avatar` — lưu vào subFolder `"avatars"`
2. Test Postman — kiểm tra URL `/uploads/avatars/...`

```java
package com.example.demo.homework.controller;

import com.example.demo.upload.dto.FileUploadResponse;
import com.example.demo.upload.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class AvatarUploadController {

    private final FileStorageService fileStorageService;   // tái dùng service phần upload

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.store(file, "avatars");
            return ResponseEntity.ok(new FileUploadResponse(url));
        } catch (IllegalArgumentException e) {
            log.warn("Avatar upload rejected: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Avatar upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
```

#### Bài 2 — Products theo category

1. Thêm method `fetchProductsByCategory(String category)` gọi `https://dummyjson.com/products/category/{category}`
2. API `GET /api/external/products/category/{name}` — test với `smartphones`

```java
package com.example.demo.homework.service;

import com.example.demo.external.config.DummyJsonProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkProductService {

    private final RestClient restClient;                       // tái dùng bean phần external
    private final DummyJsonProperties dummyJsonProperties;

    public JsonNode fetchProductsByCategory(String category) {
        String url = dummyJsonProperties.getBaseUrl() + "/products/category/{category}";
        log.debug("Fetching products by category: {}", category);
        return restClient.get().uri(url, category).retrieve().body(JsonNode.class);
    }
}
```

```java
package com.example.demo.homework.controller;

import com.example.demo.homework.service.HomeworkProductService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ProductCategoryController {

    private final HomeworkProductService homeworkProductService;

    @GetMapping("/products/category/{name}")
    public ResponseEntity<JsonNode> getProductsByCategory(@PathVariable String name) {
        return ResponseEntity.ok(homeworkProductService.fetchProductsByCategory(name));
    }
}
```

### Checklist nộp bài

- [ ] Upload hoạt động qua Postman
- [ ] Ảnh xem được trên trình duyệt
- [ ] Ít nhất 3 API external chạy được
- [ ] `@RequiredArgsConstructor` + field `final` trên Controller/Service
- [ ] Dùng `@Slf4j` thay `printStackTrace`
- [ ] Không hard-code API key *(không cần cho DummyJSON)*

### Liên kết tham khảo

- [Spring Boot File Upload](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.multipart)
- [RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
- [DummyJSON Docs](https://dummyjson.com/docs)
- [Bài 5 — REST API](./java_m2_bai5_SpringMVC.md)
- [Bài 6 — Service layer](./java_m2_bai6_SpringMVC.md)
- **Tiếp theo:** [Bài 8 — Thymeleaf CRUD User](./java_m2_bai8_SpringMVC.md)
