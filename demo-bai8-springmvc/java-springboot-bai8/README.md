# Demo Bài 8 — Thymeleaf CRUD User + Search + Pagination + Upload Avatar

Project demo cho syllabus `java_m2_bai8_SpringMVC.md`. Mở rộng phần upload từ Bài 7, thêm giao diện Thymeleaf CRUD User với dữ liệu **in-memory** (12 user mẫu) — học viên không cần DB hay External API.

## Chạy project

```bash
cd demo-bai8-springmvc/java-springboot-bai8
./mvnw spring-boot:run
```

Mở trình duyệt: **http://localhost:8080/users**

## Tính năng demo

| Tính năng | URL / cách test |
|-----------|-----------------|
| **Danh sách** | `GET /users` — 12 user mẫu, 5 user/trang |
| **Tìm kiếm** | `GET /users?q=nguyen` — lọc theo họ, tên, email, SĐT |
| **Phân trang** | `GET /users?page=2` — giữ `q` khi chuyển trang |
| **Tạo mới** | `GET /users/new` → `POST /users` + upload avatar |
| **Chi tiết** | `GET /users/{id}` |
| **Sửa** | `GET /users/{id}/edit` → `POST /users/{id}` |
| **Xóa** | `POST /users/{id}/delete` |
| **Validation** | Submit form trống email → `th:errors` trên form |
| **PRG** | Sau create/update/delete → `redirect:/users` + flash message |
| **Not found** | `GET /users/99999` → trang thân thiện |
| **Upload avatar** | `enctype="multipart/form-data"` + `FileStorageService` |

## Dependencies

| Dependency | Mục đích |
|------------|----------|
| `spring-boot-starter-web` | MVC + `MultipartFile` |
| `spring-boot-starter-thymeleaf` | Template HTML |
| `spring-boot-starter-validation` | `@Valid`, `@NotBlank`, `@Email` |
| `lombok` | `@Data`, `@RequiredArgsConstructor`, `@Slf4j` |

## Cấu trúc project

```
demo-bai8-springmvc/
└── java-springboot-bai8/
    └── src/main/java/vn/demo/
        ├── controller/
        │   ├── HomeController.java          ← redirect / → /users
        │   └── UserViewController.java      ← CRUD + search + pagination
        ├── model/UserForm.java
        ├── dto/UserPage.java                ← kết quả phân trang
        ├── service/
        │   ├── UserService.java             ← Map in-memory + @PostConstruct
        │   └── FileStorageService.java      ← từ Bài 7
        └── config/UploadResourceConfig.java
    └── src/main/resources/
        ├── templates/users/                 ← list, form, detail, not-found
        ├── templates/fragments/layout.html
        └── static/css/users.css
```

## Kịch bản demo gợi ý (15 phút)

1. Mở `/users` — 5 user trang 1 / 12 user, avatar mặc định
2. Chuyển trang 2, 3 — phân trang hoạt động
3. Tìm `?q=trần` — lọc theo tên
4. Thêm user mới + upload ảnh JPG — redirect, avatar hiện trên list
5. Sửa email, **không** chọn ảnh mới — avatar giữ nguyên
6. Submit form thiếu email — validation hiện lỗi
7. Xóa user — biến mất sau redirect
8. `/users/99999` — not-found
9. F5 sau tạo user — không duplicate (PRG)
10. Restart app — 12 user mẫu load lại; user tự tạo mất (in-memory)

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai8_SpringMVC.md`](../../syllabus/module-2/java_m2_bai8_SpringMVC.md)
- Demo Bài 7 (upload): [`demo-bai7-springmvc`](../demo-bai7-springmvc)
