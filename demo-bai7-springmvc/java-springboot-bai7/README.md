# Demo Bài 7 — Upload file & Gọi External API

Project demo cho syllabus `java_m2_bai7_SpringMVC.md`. Gom tất cả ví dụ trong một Spring Boot app — chỉ dùng `@RestController` (JSON): upload ảnh với `MultipartFile` và proxy DummyJSON qua `RestClient`.

## Chạy project

```bash
cd demo-bai7-springmvc/java-springboot-bai7
./mvnw spring-boot:run
```

Hoặc Run `DemoBai7SpringmvcApplication` trong IntelliJ.

- Port mặc định: **8080**
- Nếu port bị chiếm: xem hướng dẫn ở [README gốc](../../README.md)
- Cần kết nối Internet để gọi DummyJSON

## Dependencies

| Dependency | Mục đích |
|------------|----------|
| `spring-boot-starter-web` | REST API + `MultipartFile` + Jackson (`JsonNode`) |
| `lombok` | Giảm boilerplate (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`) |

---

## Cấu trúc project đề xuất

```
demo-bai7-springmvc/
└── java-springboot-bai7/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai7SpringmvcApplication.java
        ├── config/
        │   ├── RestClientConfig.java            ← mục 2.3: Bean RestClient
        │   ├── DummyJsonProperties.java         ← mục 2.4: @ConfigurationProperties base URL
        │   └── UploadResourceConfig.java        ← mục 1.5: map /uploads/** → thư mục ổ đĩa
        ├── controller/api/
        │   ├── FileUploadController.java        ← mục 1.6: POST upload + phụ lục bài 1 upload-avatar
        │   └── ExternalApiController.java       ← mục 2.6: proxy DummyJSON + phụ lục bài 2 category
        ├── dto/
        │   └── FileUploadResponse.java          ← mục 1.4: JSON trả về sau upload
        └── service/
            ├── FileStorageService.java          ← mục 1.3: validate ảnh, UUID, lưu file
            └── ExternalApiService.java          ← mục 2.5: gọi RestClient, trả JsonNode
    └── src/main/resources/
        └── application.properties               ← mục 1.2: multipart limit, upload dir, API URL
```

### Phân tách package

| Package | Vai trò |
|---------|---------|
| `config/` | Cấu hình bean (`RestClient`), properties, `ResourceHandler` |
| `controller/api/` | `@RestController` — nhận request, trả JSON |
| `service/` | Business logic: lưu file, gọi API ngoài — inject qua constructor |
| `dto/` | Response object cho API |

### Luồng dạy gợi ý

```
1. application.properties                    → giới hạn upload, đường dẫn lưu file, URL DummyJSON
2. FileStorageService                        → @Value, validate loại file, đổi tên UUID
3. FileUploadResponse + UploadResourceConfig  → DTO + xem ảnh qua /uploads/**
4. FileUploadController                      → MultipartFile, Postman upload
5. RestClientConfig + DummyJsonProperties    → cấu hình gọi API ngoài
6. ExternalApiService + ExternalApiController → JsonNode, proxy DummyJSON
7. Phụ lục: upload-avatar + products/category
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | Cấu hình upload | — | Xem `application.properties` | `application.properties` |
| 2 | Lưu file an toàn | — | Logic trong Service | `FileStorageService` |
| 3 | Upload ảnh | Postman | `POST /api/files/upload` (form-data, key `file` type File) | `FileUploadController`, `FileUploadResponse` |
| 4 | Xem ảnh upload | Browser | `GET /uploads/misc/{uuid}.jpg` | `UploadResourceConfig` |
| 5 | Upload file sai loại | Postman | `POST /api/files/upload` (file không phải ảnh) → `400` | `FileStorageService` |
| 6 | RestClient bean | — | Bean trong context | `RestClientConfig` |
| 7 | Danh sách sản phẩm | Postman | `GET /api/external/products?limit=10` | `ExternalApiService`, `ExternalApiController` |
| 8 | Categories | Postman | `GET /api/external/categories` | `ExternalApiService`, `ExternalApiController` |
| 9 | Danh sách users | Postman | `GET /api/external/users?limit=10` | `ExternalApiService`, `ExternalApiController` |
| 10 | User theo id | Postman | `GET /api/external/users/1` | `ExternalApiService`, `ExternalApiController` |
| 11 | Phụ lục — upload avatar | Postman | `POST /api/files/upload-avatar` → `/uploads/avatars/...` | `FileUploadController` |
| 12 | Phụ lục — products theo category | Postman | `GET /api/external/products/category/smartphones` | `ExternalApiService`, `ExternalApiController` |

> **Lombok** (`@RequiredArgsConstructor`, `@Slf4j`): dùng xuyên suốt trên controller/service — xem file tương ứng.

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai7_SpringMVC.md`](../../syllabus/module-2/java_m2_bai7_SpringMVC.md)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../demo-bai5-springmvc/java-springboot-bai5)
- Demo bài 6 (Service layer): [`demo-bai6-springmvc/java-springboot-bai6`](../demo-bai6-springmvc/java-springboot-bai6)
- Tiếp theo — Bài 8 (Thymeleaf CRUD): [`syllabus/module-2/java_m2_bai8_SpringMVC.md`](../../syllabus/module-2/java_m2_bai8_SpringMVC.md)
