# Demo Bài 7 — Upload file & Gọi External API

Project demo cho syllabus `java_m2_bai7_SpringMVC.md`. Gom tất cả ví dụ trong một Spring Boot app — chỉ dùng `@RestController` (JSON): upload ảnh với `MultipartFile` và proxy DummyJSON qua `RestClient`.

> **Quy ước package:** chia theo **từng phần demo** (feature-based) thay vì theo layer. Nhìn tên package biết ngay code demo cho mục nào:
> - `upload` → Phần 1 (Upload file)
> - `external` → Phần 2 (External API)
> - `homework` → Bài tập về nhà

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
        ├── ServletInitializer.java
        ├── upload/                              ← Phần 1: Upload file
        │   ├── config/UploadResourceConfig.java     map /uploads/** → thư mục ổ đĩa
        │   ├── controller/FileUploadController.java  POST /api/files/upload
        │   ├── dto/FileUploadResponse.java           JSON trả về sau upload
        │   └── service/FileStorageService.java       validate ảnh, UUID, lưu file
        ├── external/                            ← Phần 2: External API
        │   ├── config/RestClientConfig.java          Bean RestClient
        │   ├── config/DummyJsonProperties.java       @ConfigurationProperties base URL
        │   ├── controller/ExternalApiController.java proxy DummyJSON
        │   └── service/ExternalApiService.java       gọi RestClient, trả JsonNode
        └── homework/                            ← Bài tập về nhà
            ├── controller/AvatarUploadController.java   BT1: POST /api/files/upload-avatar
            ├── controller/ProductCategoryController.java BT2: GET products/category/{name}
            └── service/HomeworkProductService.java       BT2: fetchProductsByCategory
    └── src/main/resources/
        └── application.properties               multipart limit, upload dir, API URL
```

### Phân tách package

| Package | Phần demo | Vai trò |
|---------|-----------|---------|
| `upload` | Phần 1 | Nhận `MultipartFile`, validate + lưu file, cho browser xem ảnh |
| `external` | Phần 2 | Gọi API ngoài bằng `RestClient`, trả `JsonNode` |
| `homework` | Bài tập | Code bài tập đứng riêng, tái dùng bean dùng chung (`FileStorageService`, `RestClient`, `DummyJsonProperties`, `FileUploadResponse`) |

> Mỗi package con dùng layer chuẩn `config` / `controller` / `dto` / `service`. Bài 7 không có `model` vì dữ liệu API ngoài đọc bằng `JsonNode`.

### Luồng dạy gợi ý

```
1. application.properties                       → giới hạn upload, đường dẫn lưu file, URL DummyJSON
2. upload.service.FileStorageService            → @Value, validate loại file, đổi tên UUID
3. upload.dto + upload.config                   → DTO + xem ảnh qua /uploads/**
4. upload.controller.FileUploadController       → MultipartFile, Postman upload
5. external.config (RestClient + Properties)    → cấu hình gọi API ngoài
6. external.service + external.controller       → JsonNode, proxy DummyJSON
7. homework.*                                   → BT1 upload-avatar, BT2 products/category
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | Cấu hình upload | — | Xem `application.properties` | `application.properties` |
| 2 | Lưu file an toàn | — | Logic trong Service | `upload/service/FileStorageService` |
| 3 | Upload ảnh | Postman | `POST /api/files/upload` (form-data, key `file` type File) | `upload/controller/FileUploadController`, `upload/dto/FileUploadResponse` |
| 4 | Xem ảnh upload | Browser | `GET /uploads/misc/{uuid}.jpg` | `upload/config/UploadResourceConfig` |
| 5 | Upload file sai loại | Postman | `POST /api/files/upload` (file không phải ảnh) → `400` | `upload/service/FileStorageService` |
| 6 | RestClient bean | — | Bean trong context | `external/config/RestClientConfig` |
| 7 | Danh sách sản phẩm | Postman | `GET /api/external/products?limit=10` | `external/service/ExternalApiService`, `external/controller/ExternalApiController` |
| 8 | Categories | Postman | `GET /api/external/categories` | `external/service/ExternalApiService`, `external/controller/ExternalApiController` |
| 9 | Danh sách users | Postman | `GET /api/external/users?limit=10` | `external/service/ExternalApiService`, `external/controller/ExternalApiController` |
| 10 | User theo id | Postman | `GET /api/external/users/1` | `external/service/ExternalApiService`, `external/controller/ExternalApiController` |
| 11 | Phụ lục — upload avatar | Postman | `POST /api/files/upload-avatar` → `/uploads/avatars/...` | `homework/controller/AvatarUploadController` |
| 12 | Phụ lục — products theo category | Postman | `GET /api/external/products/category/smartphones` | `homework/service/HomeworkProductService`, `homework/controller/ProductCategoryController` |

> **Lombok** (`@RequiredArgsConstructor`, `@Slf4j`): dùng xuyên suốt trên controller/service — xem file tương ứng.

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai7_SpringMVC.md`](../../syllabus/module-2/java_m2_bai7_SpringMVC.md)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../demo-bai5-springmvc/java-springboot-bai5)
- Demo bài 6 (Service layer): [`demo-bai6-springmvc/java-springboot-bai6`](../demo-bai6-springmvc/java-springboot-bai6)
- Tiếp theo — Bài 8 (Thymeleaf CRUD): [`syllabus/module-2/java_m2_bai8_SpringMVC.md`](../../syllabus/module-2/java_m2_bai8_SpringMVC.md)
