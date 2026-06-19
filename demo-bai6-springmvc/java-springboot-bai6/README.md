# Demo Bài 6 — Service Layer, Validation, Lombok & HTTP Header

Project demo cho syllabus `java_m2_bai6_SpringMVC.md`. Gom tất cả ví dụ trong một Spring Boot app, theme **Online Shopping** (Account, Order, Product, Book).

## Chạy project

```bash
cd demo-bai6-springmvc/java-springboot-bai6
./mvnw spring-boot:run
```

Hoặc Run `DemoBai6SpringmvcApplication` trong IntelliJ.

- Port mặc định: **8080**
- Nếu port bị chiếm: xem hướng dẫn ở [README gốc](../../README.md)

## Dependencies

| Dependency | Mục đích |
|------------|----------|
| `spring-boot-starter-web` | REST API + Spring MVC |
| `spring-boot-starter-thymeleaf` | Form HTML |
| `spring-boot-starter-validation` | Bean Validation (`@NotBlank`, `@Email`, …) |
| `lombok` | Giảm boilerplate (`@Data`, `@RequiredArgsConstructor`) |

---

## Cấu trúc project đề xuất

Project **chia package theo từng phần demo (feature-based)** — nhìn tên package là biết đang demo cho mục nào của bài. Bên trong mỗi package vẫn tách lớp `controller` / `service` / `model` / `repository`.

```
demo-bai6-springmvc/
└── java-springboot-bai6/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai6SpringmvcApplication.java
        ├── servicelayer/                        ← mục 1-3: @Service, DI, gọi liên thông Service
        │   ├── controller/AccountController.java
        │   ├── service/AccountService.java
        │   ├── service/OrderService.java
        │   ├── repository/AccountRepository.java  (stub in-memory, chưa JPA)
        │   └── model/Account.java                 (không validation — tập trung DI)
        ├── validation/                          ← mục 5-7: Bean Validation REST + Thymeleaf
        │   ├── controller/FormApiController.java   (REST: /api/form/fill)
        │   ├── controller/RegisterController.java  (Thymeleaf: /register)
        │   └── model/Account.java                 (@Data + @NotBlank/@Email/@Size/@Min)
        ├── grouping/                            ← mục 8: @RequestMapping class level
        │   └── controller/DemoController.java
        ├── header/                              ← mục 9: @RequestHeader
        │   └── controller/ProductController.java   (/products, /profile)
        └── homework/                            ← phụ lục bài 1-4
            ├── controller/ProductController.java   (bài 1: validate-price)
            ├── controller/BookApiController.java   (bài 2: POST /api/v1/books)
            ├── controller/BookWebController.java   (bài 3: form Thymeleaf)
            ├── controller/MeController.java        (bài 4: header X-User-Id)
            ├── service/ProductService.java
            ├── service/BookService.java
            ├── model/Book.java
            └── dto/BookRequest.java, MeResponse.java
    └── src/main/resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/
            ├── validation/                      ← form đăng ký (mục 7)
            │   ├── form.html                    ← th:field, th:errors
            │   └── success.html
            └── homework/                        ← form thêm sách (phụ lục bài 3)
                ├── form.html
                └── success.html
```

### Phân tách package (theo phần demo)

| Package | Demo cho phần | Nội dung chính |
|---------|---------------|----------------|
| `servicelayer` | Mục 1-3 | `@Service`, constructor injection, gọi liên thông Service (`AccountService` → `OrderService`) |
| `validation` | Mục 5-7 | Bean Validation dùng chung cho REST (`@RequestBody`) và Thymeleaf (`@ModelAttribute`) |
| `grouping` | Mục 8 | Nhóm API bằng `@RequestMapping` ở class level |
| `header` | Mục 9 | Đọc HTTP Header bằng `@RequestHeader` |
| `homework` | Phụ lục | 4 bài tập tổng hợp |

> **Lombok** (mục 4) là kỹ thuật xuyên suốt mọi package (`@Data`, `@RequiredArgsConstructor`) — không tách package riêng.

### Luồng dạy gợi ý

```
1. servicelayer.AccountController + AccountService → DI, không dùng new
2. servicelayer: OrderService → AccountService     → liên thông Service
3. Lombok                                           → @Data, @RequiredArgsConstructor (xuyên suốt)
4. validation.FormApiController                     → REST validation (Postman)
5. validation.RegisterController + form.html        → Thymeleaf validation (Browser)
6. grouping.DemoController                          → @RequestMapping class
7. header.ProductController                         → @RequestHeader
8. homework: Book REST + Book form + /api/v1/me
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | Service + DI | Postman | `POST /api/account/signUp?emailAddress=john@example.com` | `servicelayer/controller/AccountController`, `servicelayer/service/AccountService` |
| 2 | Service chain | Postman | `GET /api/account/orders?userId=1` | `servicelayer/service/OrderService`, `servicelayer/service/AccountService` |
| 3 | REST validation | Postman | `POST /api/form/fill` (JSON body `Account`) | `validation/controller/FormApiController`, `validation/model/Account` |
| 4 | Thymeleaf validation | Browser | `GET /register` → submit form | `validation/controller/RegisterController`, `templates/validation/` |
| 5 | `@RequestMapping` nhóm | Postman | `GET /api/demo/order/list`, `DELETE /api/demo/order/detail?id=1` | `grouping/controller/DemoController` |
| 6 | HTTP Header | Postman | `GET /products`, `GET /profile` (kèm headers) | `header/controller/ProductController` |
| 7 | Phụ lục — price | Postman | `GET /api/v1/products/validate-price?price=100` | `homework/controller/ProductController`, `homework/service/ProductService` |
| 8 | Phụ lục — Book REST | Postman | `POST /api/v1/books` | `homework/controller/BookApiController`, `homework/dto/BookRequest` |
| 9 | Phụ lục — Book form | Browser | `GET /books/new` | `homework/controller/BookWebController`, `templates/homework/` |
| 10 | Phụ lục — Header bắt buộc | Postman | `GET /api/v1/me` + header `X-User-Id` | `homework/controller/MeController`, `homework/dto/MeResponse` |

> **Lombok** (`@Data`, `@RequiredArgsConstructor`): dùng xuyên suốt mọi package — xem trên model/controller/service tương ứng.

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai6_SpringMVC.md`](../../syllabus/module-2/java_m2_bai6_SpringMVC.md)
- Demo bài 4 (Thymeleaf cơ bản): [`demo-bai4-springboot/java-springboot-bai4`](../demo-bai4-springboot/java-springboot-bai4)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../demo-bai5-springmvc/java-springboot-bai5)
