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

```
demo-bai6-springmvc/
└── java-springboot-bai6/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai6SpringmvcApplication.java
        ├── controller/
        │   ├── RegisterController.java          ← mục 7: Validation Thymeleaf (Account)
        │   ├── BookWebController.java           ← phụ lục bài 3: Validation Thymeleaf (Book)
        │   └── api/
        │       ├── AccountApiController.java    ← mục 2, 3: @Service + constructor injection
        │       ├── FormApiController.java       ← mục 6: Validation REST API (Account JSON)
        │       ├── DemoApiController.java       ← mục 8: @RequestMapping class level
        │       ├── ProductApiController.java    ← mục 9 + phụ lục bài 1: HTTP Header + validate price
        │       ├── BookApiController.java       ← phụ lục bài 2: Validation REST API (Book)
        │       └── MeApiController.java         ← phụ lục bài 4: Header bắt buộc X-User-Id
        ├── service/
        │   ├── AccountService.java              ← business logic + gọi OrderService
        │   ├── OrderService.java                ← mục 3: liên thông Service
        │   ├── ProductService.java              ← phụ lục bài 1: isValidPrice()
        │   └── BookService.java
        ├── repository/
        │   └── AccountRepository.java             ← stub in-memory (chưa JPA)
        ├── model/
        │   ├── Account.java                     ← mục 5: DTO dùng chung REST + Thymeleaf
        │   └── Book.java
        └── dto/
            ├── BookRequest.java                 ← validation cho Book (REST + form)
            └── MeResponse.java
    └── src/main/resources/
        ├── application.properties
        ├── static/css/style.css
        └── templates/
            ├── register/
            │   ├── form.html                    ← th:field, th:errors
            │   └── success.html
            └── books/
                ├── form.html
                └── success.html
```

### Phân tách package

| Package | Vai trò |
|---------|---------|
| `controller/` | `@Controller` — trả view Thymeleaf |
| `controller/api/` | `@RestController` — trả JSON |
| `service/` | Business logic, `@Service`, inject qua constructor |
| `repository/` | Truy cập dữ liệu (stub — bài JPA sẽ thay bằng JPA) |
| `model/` | Entity / form object dùng chung |
| `dto/` | Request/Response cho API |

### Luồng dạy gợi ý

```
1. AccountService + AccountApiController     → DI, không dùng new
2. OrderService → AccountService             → liên thông Service
3. Refactor Lombok                           → @Data, @RequiredArgsConstructor
4. Account + FormApiController               → REST validation (Postman)
5. RegisterController + form.html            → Thymeleaf validation (Browser)
6. DemoApiController                         → @RequestMapping class
7. ProductApiController                      → @RequestHeader
8. Phụ lục: Book REST + Book form + /api/v1/me
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | Service + DI | Postman | `POST /api/account/signUp?emailAddress=john@example.com` | `AccountService`, `AccountApiController` |
| 2 | Service chain | Postman | `GET /api/account/orders?userId=1` | `OrderService`, `AccountService` |
| 3 | REST validation | Postman | `POST /api/form/fill` (JSON body `Account`) | `Account`, `FormApiController` |
| 4 | Thymeleaf validation | Browser | `GET /register` → submit form | `RegisterController`, `templates/register/` |
| 5 | `@RequestMapping` nhóm | Postman | `GET /api/demo/order/list`, `DELETE /api/demo/order/detail?id=1` | `DemoApiController` |
| 6 | HTTP Header | Postman | `GET /products`, `GET /profile` (kèm headers) | `ProductApiController` |
| 7 | Phụ lục — price | Postman | `GET /api/v1/products/validate-price?price=100` | `ProductService`, `ProductApiController` |
| 8 | Phụ lục — Book REST | Postman | `POST /api/v1/books` | `BookRequest`, `BookApiController` |
| 9 | Phụ lục — Book form | Browser | `GET /books/new` | `BookWebController`, `templates/books/` |
| 10 | Phụ lục — Header bắt buộc | Postman | `GET /api/v1/me` + header `X-User-Id` | `MeApiController`, `MeResponse` |

> **Lombok** (`@Data`, `@RequiredArgsConstructor`): dùng xuyên suốt từ bước 1 — xem trên model/controller/service tương ứng.

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai6_SpringMVC.md`](../../syllabus/module-2/java_m2_bai6_SpringMVC.md)
- Demo bài 4 (Thymeleaf cơ bản): [`demo-bai4-springboot/java-springboot-bai4`](../demo-bai4-springboot/java-springboot-bai4)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../demo-bai5-springmvc/java-springboot-bai5)
