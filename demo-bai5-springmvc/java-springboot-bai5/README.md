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

Package chia **theo HTTP method** (đúng thứ tự chương §7-11 của syllabus) — mỗi package tự chứa `controller` + `dto` (riêng `capstone` có thêm `model` + `service`). Nhìn tên package biết ngay đang demo method nào.

```
demo-bai5-springmvc/
└── java-springboot-bai5/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai5SpringmvcApplication.java
        ├── get/                              ← §7 GET API
        │   ├── controller/
        │   │   ├── ProductQueryController.java   GET /products, /products/search, /products/{id}
        │   │   ├── NewsController.java           GET /news/latest (object JSON + produces)
        │   │   └── UserQueryController.java      GET /users, /users/{id}
        │   └── dto/NewsDto.java
        ├── post/                             ← §8 POST API
        │   ├── controller/
        │   │   ├── ProductCreateController.java  POST /products (form), /products/json
        │   │   └── CategoryCreateController.java POST /categories (form), POST /games
        │   └── dto/{ProductRequest, GameCreateRequest}.java
        ├── put/                              ← §9 PUT API
        │   ├── controller/
        │   │   ├── CategoryUpdateController.java PUT /categories/{id}, /categories/{id}/json
        │   │   └── UserUpdateController.java     PUT /users/{id}, /users/{id}/profile
        │   └── dto/{CategoryRequest, UserProfileRequest}.java
        ├── patch/                            ← §10 PATCH API
        │   ├── controller/
        │   │   ├── ProductPatchController.java   PATCH /products/{id}
        │   │   └── UserPatchController.java      PATCH /users/{id}
        │   └── dto/{ProductPatchRequest, UserPatchRequest}.java
        ├── delete/                           ← §11 DELETE API
        │   └── controller/
        │       ├── OrderController.java          DELETE /orders, /orders/{id}, /orders/batch
        │       └── SongController.java           DELETE /songs, /songs/{id}
        └── capstone/                         ← Phụ lục: Bài tập tổng hợp (books)
            ├── controller/BookController.java    search + CRUD, ResponseEntity + 404
            ├── dto/{BookRequest, BookPatchRequest}.java
            ├── model/Book.java
            └── service/BookService.java          List in-memory
    └── src/main/resources/
        └── application.properties
```

### Phân tách package

| Package | Chương syllabus | Vai trò |
|---------|-----------------|---------|
| `get/` | §7 | GET API — list, `@RequestParam`, `@PathVariable`, object → JSON |
| `post/` | §8 | POST API — form (`@RequestParam`) vs JSON (`@RequestBody`), `201` |
| `put/` | §9 | PUT API — cập nhật toàn bộ qua form / JSON |
| `patch/` | §10 | PATCH API — cập nhật một phần qua JSON |
| `delete/` | §11 | DELETE API — query / path / batch body, `204` |
| `capstone/` | Phụ lục | Bài tập tổng hợp `books` — đủ CRUD + search, có `model` + `service` |

> Mỗi package có `controller` + `dto` riêng để tự chứa, không phụ thuộc chéo. Endpoint cùng base path (vd `/api/v1/products`) nằm ở nhiều package nhưng khác HTTP method nên Spring đăng ký độc lập, không xung đột.

### Luồng dạy gợi ý

```
1. @RestController vs @Controller   → so sánh với demo bài 4 (Thymeleaf)
2. get/      → GET list, @RequestParam (bắt buộc/không bắt buộc), @PathVariable, object → JSON
3. post/     → form (@RequestParam) vs JSON (@RequestBody), status 201
4. put/      → cập nhật toàn bộ qua form / JSON
5. patch/    → cập nhật một phần qua JSON
6. delete/   → query / path / batch body, status 204
7. capstone/ → search + CRUD tổng hợp books, ResponseEntity + 404
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | `@RestController` + GET list | Postman / Browser | `GET /api/v1/products` | `get/ProductQueryController` |
| 2 | `@RequestParam` bắt buộc | Postman / Browser | `GET /api/v1/products/search?category=phone` | `get/ProductQueryController` |
| 3 | `@RequestParam` không bắt buộc | Postman / Browser | `GET /api/v1/products/search?category=phone&brand=Samsung` | `get/ProductQueryController` |
| 4 | `@RequestParam` + `defaultValue` | Postman / Browser | `GET /api/v1/products/search?category=laptop&sortBy=price` | `get/ProductQueryController` |
| 5 | Thiếu param bắt buộc → `400` | Postman / Browser | `GET /api/v1/products/search` (không có `category`) | `get/ProductQueryController` |
| 6 | `@PathVariable` | Postman / Browser | `GET /api/v1/products/5` | `get/ProductQueryController` |
| 7 | Object → JSON + `produces` | Postman | `GET /api/v1/news/latest` (trả object trực tiếp) | `get/NewsController`, `get/NewsDto` |
| 7b | `ResponseEntity` + object JSON | Postman | `GET /api/v1/books/1` · `GET /api/v1/books/999` → `404` | `capstone/BookController` |
| 8 | POST form | Postman | `POST /api/v1/products` (x-www-form-urlencoded: `name`, `price`, `color`) | `post/ProductCreateController` |
| 9 | POST JSON body | Postman | `POST /api/v1/products/json` | `post/ProductRequest`, `post/ProductCreateController` |
| 10 | POST form (thực hành) | Postman | `POST /api/v1/categories` (`name`, `location?`) | `post/CategoryCreateController` |
| 11 | POST JSON (thực hành) | Postman | `POST /api/v1/games` | `post/GameCreateRequest`, `post/CategoryCreateController` |
| 12 | PUT form | Postman | `PUT /api/v1/categories/1` | `put/CategoryUpdateController` |
| 13 | PUT JSON body | Postman | `PUT /api/v1/categories/1/json` | `put/CategoryRequest`, `put/CategoryUpdateController` |
| 14 | GET users | Postman / Browser | `GET /api/v1/users`, `GET /api/v1/users/1` | `get/UserQueryController` |
| 15 | PUT vs PATCH | Postman | `PUT /api/v1/users/1` (form) · `PATCH /api/v1/users/1` (JSON) | `put/UserUpdateController`, `patch/UserPatchController` |
| 16 | PUT profile JSON | Postman | `PUT /api/v1/users/1/profile` | `put/UserProfileRequest`, `put/UserUpdateController` |
| 17 | PATCH product | Postman | `PATCH /api/v1/products/1` body `{"price":899}` | `patch/ProductPatchRequest`, `patch/ProductPatchController` |
| 18 | DELETE query | Postman | `DELETE /api/v1/orders?id=5` → `204` | `delete/OrderController` |
| 19 | DELETE path | Postman | `DELETE /api/v1/orders/5` → `204` | `delete/OrderController` |
| 20 | DELETE batch body | Postman | `DELETE /api/v1/orders/batch` body `{"ids":["aaa","bbb"]}` | `delete/OrderController` |
| 21 | DELETE thực hành | Postman | `DELETE /api/v1/songs?title=...` · `DELETE /api/v1/songs/1` | `delete/SongController` |
| 22 | Phụ lục — search books | Postman | `GET /api/v1/books/search?author=Robert C. Martin&title=Clean` | `capstone/BookController`, `capstone/BookService` |
| 23 | Phụ lục — CRUD books | Postman | `GET/POST/PUT/PATCH/DELETE /api/v1/books[/{id}]` | `capstone/BookController`, `capstone/BookService` |

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
