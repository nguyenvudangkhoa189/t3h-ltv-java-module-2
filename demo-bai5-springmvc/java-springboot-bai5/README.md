# Demo Bài 5 — REST API với Spring Boot (part 1)

Project demo cho syllabus `java_m2_bai5_SpringMVC.md`. Gom tất cả ví dụ REST API trong một Spring Boot app — chỉ `@RestController` trả JSON, không có Thymeleaf.

## Chạy project

```bash
cd demo-bai5-springmvc/java-springboot-bai5
./mvnw spring-boot:run
```

Hoặc Run `DemoBai5SpringmvcApplication` trong IntelliJ.

- Port mặc định: **8080**
- Nếu port bị chiếm: xem hướng dẫn ở [README gốc](../../README.md)
- Test API: **Postman** (hoặc trình duyệt chỉ với GET)

## Dependencies

| Dependency | Mục đích |
|------------|----------|
| `spring-boot-starter-web` | REST API, embedded Tomcat, Jackson (JSON) |
| `spring-boot-starter-test` | Unit test / context load |

---

## Cấu trúc project

```
demo-bai5-springmvc/
└── java-springboot-bai5/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai5SpringmvcApplication.java
        ├── controller/api/
        │   ├── ProductApiController.java    ← mục 4, 7, 8, 10: GET/POST/PATCH + @RequestParam (bắt buộc/không bắt buộc)
        │   ├── NewsApiController.java         ← mục 7.5–7.6: trả object JSON + produces
        │   ├── CategoryApiController.java   ← mục 8, 9: POST/PUT form vs JSON
        │   ├── UserApiController.java       ← mục 7.7, 9.4, 10.3: GET + PUT + PATCH
        │   ├── OrderApiController.java      ← mục 11: DELETE query / path / body
        │   └── BookApiController.java       ← phụ lục: search + CRUD, ResponseEntity + 404
        ├── service/
        │   └── BookService.java             ← lưu tạm List in-memory cho /api/v1/books
        ├── model/
        │   └── Book.java
        └── dto/
            ├── NewsDto.java
            ├── ProductRequest.java
            ├── ProductPatchRequest.java
            ├── GameCreateRequest.java
            ├── UserProfileRequest.java
            ├── UserPatchRequest.java
            ├── BookRequest.java
            └── BookPatchRequest.java
    └── src/main/resources/
        └── application.properties
```

### Phân tách package

| Package | Vai trò |
|---------|---------|
| `controller/api/` | `@RestController` — trả JSON, prefix `/api/v1/...` |
| `dto/` | Data Transfer Object — nhận/trả dữ liệu qua `@RequestBody` |
| `model/` | Entity nội bộ (Book — dùng trong `BookService`) |
| `service/` | Business logic in-memory (chỉ `BookService` — các API khác demo in log) |

### Luồng dạy gợi ý

```
1. @RestController vs @Controller     → so sánh với demo bài 4 (Thymeleaf)
2. ProductApiController — GET         → list, @RequestParam (bắt buộc / không bắt buộc), @PathVariable
3. NewsApiController                  → Jackson, DTO, produces APPLICATION_JSON
4. ProductApiController — POST        → form (@RequestParam) vs JSON (@RequestBody), status 201
5. CategoryApiController              → POST/PUT form + PUT JSON + POST games
6. UserApiController                  → PUT toàn bộ (form) vs PATCH một phần (JSON)
7. OrderApiController                 → DELETE query / path / batch body, status 204
8. BookApiController + BookService    → phụ lục: search + CRUD tổng hợp, ResponseEntity + 404
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | `@RestController` + GET list | Postman / Browser | `GET /api/v1/products` | `ProductApiController` |
| 2 | `@RequestParam` bắt buộc | Postman / Browser | `GET /api/v1/products/search?category=phone` | `ProductApiController` |
| 3 | `@RequestParam` không bắt buộc | Postman / Browser | `GET /api/v1/products/search?category=phone&brand=Samsung` | `ProductApiController` |
| 4 | `@RequestParam` + `defaultValue` | Postman / Browser | `GET /api/v1/products/search?category=laptop&sortBy=price` | `ProductApiController` |
| 5 | Thiếu param bắt buộc → `400` | Postman / Browser | `GET /api/v1/products/search` (không có `category`) | `ProductApiController` |
| 6 | `@PathVariable` | Postman / Browser | `GET /api/v1/products/5` | `ProductApiController` |
| 7 | Object → JSON + `produces` | Postman | `GET /api/v1/news/latest` (trả object trực tiếp) | `NewsApiController`, `NewsDto` |
| 7b | `ResponseEntity` + object JSON | Postman | `GET /api/v1/books/1` · `GET /api/v1/books/999` → `404` | `BookApiController` |
| 8 | POST form | Postman | `POST /api/v1/products` (x-www-form-urlencoded: `name`, `price`, `color`) | `ProductApiController` |
| 9 | POST JSON body | Postman | `POST /api/v1/products/json` | `ProductRequest`, `ProductApiController` |
| 10 | POST form (thực hành) | Postman | `POST /api/v1/categories` (`name`, `location?`) | `CategoryApiController` |
| 11 | POST JSON (thực hành) | Postman | `POST /api/v1/games` | `GameCreateRequest`, `CategoryApiController` |
| 12 | PUT form | Postman | `PUT /api/v1/categories/1` | `CategoryApiController` |
| 13 | PUT JSON body | Postman | `PUT /api/v1/categories/1/json` | `CategoryApiController` |
| 14 | GET users | Postman / Browser | `GET /api/v1/users`, `GET /api/v1/users/1` | `UserApiController` |
| 15 | PUT vs PATCH | Postman | `PUT /api/v1/users/1` (form) · `PATCH /api/v1/users/1` (JSON) | `UserApiController` |
| 16 | PUT profile JSON | Postman | `PUT /api/v1/users/1/profile` | `UserProfileRequest`, `UserApiController` |
| 17 | PATCH product | Postman | `PATCH /api/v1/products/1` body `{"price":899}` | `ProductPatchRequest`, `ProductApiController` |
| 18 | DELETE query | Postman | `DELETE /api/v1/orders?id=5` → `204` | `OrderApiController` |
| 19 | DELETE path | Postman | `DELETE /api/v1/orders/5` → `204` | `OrderApiController` |
| 20 | DELETE batch body | Postman | `DELETE /api/v1/orders/batch` body `{"ids":["aaa","bbb"]}` | `OrderApiController` |
| 21 | DELETE thực hành | Postman | `DELETE /api/v1/songs?title=...` · `DELETE /api/v1/songs/1` | `OrderApiController` |
| 22 | Phụ lục — search books | Postman | `GET /api/v1/books/search?author=Robert C. Martin&title=Clean` | `BookApiController`, `BookService` |
| 23 | Phụ lục — CRUD books | Postman | `GET/POST/PUT/PATCH/DELETE /api/v1/books[/{id}]` | `BookApiController`, `BookService` |

### HTTP status gợi ý khi test

| Method | Status thường gặp | Endpoint ví dụ |
|--------|-------------------|------------------|
| GET | `200 OK` | `/api/v1/products`, `/api/v1/products/search?category=phone` |
| GET | `400 Bad Request` | `/api/v1/products/search` (thiếu `category`) |
| GET | `200 OK` | `/api/v1/books/search?author=Robert C. Martin` |
| GET | `400 Bad Request` | `/api/v1/books/search` (thiếu `author`) |
| GET | `404 Not Found` | `/api/v1/books/999` |
| POST | `201 Created` | `/api/v1/products/json`, `/api/v1/books` |
| PUT / PATCH | `200 OK` | `/api/v1/users/1/profile` |
| DELETE | `204 No Content` | `/api/v1/orders/5` |

> **Postman:** POST/PUT/PATCH body JSON cần header `Content-Type: application/json`. POST form dùng **x-www-form-urlencoded**, không phải raw JSON.

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai5_SpringMVC.md`](../../syllabus/module-2/java_m2_bai5_SpringMVC.md)
- Demo bài 4 (Thymeleaf + `@Controller`): [`demo-bai4-springboot/java-springboot-bai4`](../../demo-bai4-springboot/java-springboot-bai4)
- Demo bài 6 (Service, Validation, Lombok): [`demo-bai6-springmvc/java-springboot-bai6`](../../demo-bai6-springmvc/java-springboot-bai6)
