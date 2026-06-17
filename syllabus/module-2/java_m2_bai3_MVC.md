# Bài 3: Mô hình MVC — Model, View, Controller

## Mục tiêu bài học

Sau bài này, học viên có thể:

- Giải thích **MVC** là gì và phân biệt **MVC (pattern)** với **Spring MVC (framework)**
- Mô tả trách nhiệm của **Model**, **View**, **Controller** và **Service**
- Vẽ được luồng xử lý từ người dùng → server → hiển thị kết quả
- Nhận biết từng layer tương ứng trong project Spring Boot (chuẩn bị cho Bài 4)
- Liệt kê ưu điểm, hạn chế và anti-pattern thường gặp khi áp dụng MVC

## Điều kiện tiên quyết

- Biết **Java OOP** cơ bản: class, interface, package
- Đã học **HTML** — hiểu trình duyệt gửi request và nhận response
- Nắm khái niệm **database** ở mức tổng quan (lưu trữ dữ liệu bền vững)

> **Ghi chú:** Bài này là **lý thuyết kiến trúc** — chưa viết code Spring Boot. Thực hành `@Controller`, Thymeleaf và REST API sẽ học ở **Bài 4** và **Bài 5**.

### Thời lượng gợi ý

| Phần | Thời gian |
|------|-----------|
| Lý thuyết §1–7 | ~40 phút |
| Thảo luận §8–12 | ~15 phút |
| Bài tập / ôn tập | ~15 phút |

## Nội dung

| # | Chủ đề |
|---|--------|
| 1 | MVC là gì? |
| 2 | Phân biệt MVC pattern vs Spring MVC |
| 3 | Model — dữ liệu và nghiệp vụ |
| 4 | View — giao diện người dùng |
| 5 | Controller & Service |
| 6 | Luồng xử lý (workflow) |
| 7 | Kiến trúc phân lớp trong Spring Boot |
| 8 | Tại sao nên sử dụng MVC |
| 9 | Hạn chế của MVC |
| 10 | Anti-pattern thường gặp |
| 11 | Các biến thể liên quan (MVP, MVVM) |
| 12 | Lỗi hiểu sai thường gặp |
| Phụ lục | Bài tập · Câu hỏi ôn tập · Liên kết tham khảo |

---

## 1. MVC là gì?

- **MVC** là viết tắt của **Model – View – Controller** — một **mô hình thiết kế** (design pattern) phổ biến trong phát triển phần mềm, đặc biệt là ứng dụng web.
- MVC chia ứng dụng thành **3 phần có trách nhiệm riêng**, phát triển tương đối độc lập nhưng phối hợp với nhau.
- Mỗi phần giao tiếp qua **interface rõ ràng** (gọi hàm, truyền dữ liệu) — không phụ thuộc cứng vào implementation bên trong.

> **Lịch sử ngắn:** MVC được Trygve Reenskaug đề xuất trong dự án Smalltalk (1970s). Sau đó được áp dụng rộng rãi trong web: Java Struts, Spring MVC, ASP.NET MVC, Ruby on Rails, …

### Ba thành phần cốt lõi

```mermaid
flowchart LR
    U["Người dùng"] --> V["View<br/>(Giao diện)"]
    V <-->|"HTTP request / response"| C["Controller<br/>(Điều phối)"]
    C <-->|"Đọc / ghi dữ liệu"| M["Model<br/>(Dữ liệu & logic)"]
```

| Thành phần | Câu hỏi trả lời | Ví dụ thực tế |
|------------|-----------------|---------------|
| **Model** | Dữ liệu là gì? Quy tắc nghiệp vụ ra sao? | `Product`, `Order`, `User` |
| **View** | Người dùng nhìn thấy gì? | Trang HTML, form, bảng danh sách |
| **Controller** | Ai điều phối khi người dùng thao tác? | Xử lý request, gọi service, chọn view |

> **Ẩn dụ:** Nhà hàng — **View** là thực đơn và bàn ăn (khách nhìn thấy); **Controller** là bồi bàn (nhận order, chuyển xuống bếp); **Model** là nguyên liệu và công thức nấu (dữ liệu + quy tắc).

> **Ngữ cảnh khóa học:** Trong **Web MVC**, View và Controller giao tiếp qua **HTTP request/response** (trình duyệt gửi request tới server). Ở ứng dụng desktop cổ điển, View có thể gọi trực tiếp method của Controller — khác với web nhưng ý tưởng tách layer vẫn giống nhau.

---

## 2. Phân biệt MVC pattern vs Spring MVC

Hai khái niệm này thường bị nhầm lẫn:

| | **MVC (pattern)** | **Spring MVC (framework)** |
|--|-------------------|----------------------------|
| **Bản chất** | Mô hình thiết kế / kiến trúc phần mềm | Module của Spring Framework **triển khai** pattern MVC |
| **Phạm vi** | Khái niệm chung, áp dụng nhiều ngôn ngữ | Cụ thể cho Java / Spring |
| **Thành phần** | Model, View, Controller | `DispatcherServlet`, `@Controller`, `ViewResolver`, Thymeleaf, … |
| **Quan hệ** | Ý tưởng thiết kế | Công cụ thực thi ý tưởng đó |

**Front Controller pattern:** Trong Spring MVC, mọi HTTP request đều đi qua **`DispatcherServlet`** trước — đây là biến thể *Front Controller* của MVC. Chi tiết sẽ thấy khi chạy Spring Boot ở Bài 4.

```
MVC pattern (lý thuyết)          Spring MVC (thực tế)
─────────────────────────        ─────────────────────────────
Controller                  →    @Controller / @RestController
View                        →    Thymeleaf template / JSON response
Model (layer)               →    Domain class / Entity (Product, User)
Spring Model (object)       →    model.addAttribute(...) — hộp dữ liệu truyền sang View
DTO                         →    Class trao đổi dữ liệu qua API (học ở Bài 5)
                                 + DispatcherServlet điều phối
```

---

## 3. Model — dữ liệu và nghiệp vụ

### 3.1. Model làm gì?

- **Định nghĩa cấu trúc dữ liệu** của ứng dụng — các thuộc tính và quan hệ giữa chúng.
- **Mô tả quy tắc nghiệp vụ** (business rules) — ví dụ: giá sản phẩm không âm, đơn hàng phải có ít nhất 1 sản phẩm.
- Cung cấp dữ liệu cho Controller (thông qua Service) để hiển thị hoặc xử lý.

**Ví dụ cấu trúc dữ liệu:**

| Đối tượng | Thuộc tính | Quan hệ |
|-----------|------------|---------|
| `Product` | id, tên, giá, số lượng, hình ảnh | 1 sản phẩm thuộc 1 nhóm hàng |
| `Order` | id, ngày tạo, tổng tiền | 1 đơn hàng có nhiều sản phẩm |
| `User` | id, tên, email, địa chỉ | 1 user có thể đặt nhiều đơn hàng |

### 3.2. Model ≠ Repository (quan trọng)

Trong lý thuyết MVC cổ điển, "Model" gộp cả **dữ liệu** và **truy cập dữ liệu**. Trong thực tế Spring Boot, ta **tách rõ** các tầng:

| Tầng | Trách nhiệm | Ví dụ |
|------|-------------|-------|
| **Domain / Entity** | Class mô tả dữ liệu | `Product.java`, `User.java` |
| **Repository / DAO** | Kết nối database, CRUD | `ProductRepository`, JPA |
| **Service** | Logic nghiệp vụ, điều phối nhiều repository | `ProductService` |

> **Ghi nhớ:** Khi nói "Model" trong bài học MVC, ta thường ý **domain data + business logic**. Việc **kết nối database** thuộc **Repository** — không nhét SQL vào class `Product`.

**Ví dụ Java (pseudocode — chưa dùng Spring):**

```java
// Domain — mô tả dữ liệu
class Product {
    private Long id;
    private String name;
    private double price;
    private int quantity;
    // getter / setter
}

// Repository — truy cập dữ liệu (tách khỏi domain)
interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    void deleteById(Long id);
}

// Service — logic nghiệp vụ
class ProductService {
    private final ProductRepository repository;

    ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    List<Product> getAvailableProducts() {
        return repository.findAll().stream()
            .filter(p -> p.getQuantity() > 0)
            .toList();
    }
}
```

### 3.3. DTO — Data Transfer Object

**DTO** là class dùng để **trao đổi dữ liệu** giữa các layer hoặc qua API — tách khỏi Entity để không lộ cấu trúc database ra ngoài.

| Khái niệm | Mục đích | Ví dụ |
|-----------|----------|-------|
| **Entity** | Ánh xạ bảng database | `Product` có `@Id`, `@Column` |
| **DTO** | Gửi/nhận qua API hoặc form | `ProductRequest`, `ProductResponse` |

```java
// Entity — gắn với database (học ở bài JPA sau)
class Product {
    private Long id;
    private String name;
    private double costPrice;  // giá vốn — không muốn lộ ra API
}

// DTO — chỉ gửi field cần thiết cho client
class ProductResponse {
    private Long id;
    private String name;
    private double price;
}
```

> Chi tiết dùng DTO với `@RequestBody` / `@RestController` học ở **Bài 5**. Ở Bài 3 chỉ cần nhớ: **Entity** (trong app) và **DTO** (trao đổi ra ngoài) có thể khác nhau — tránh nhầm với MVC Model layer.

---

## 4. View — giao diện người dùng

### 4.1. View làm gì?

- Là phần **người dùng nhìn thấy và tương tác** — GUI desktop, trang web HTML, mobile screen.
- **Nhận dữ liệu** từ Controller và **hiển thị** lên giao diện.
- **Gửi thao tác** của người dùng (click nút, submit form) về Controller — thường qua HTTP request.

**Ví dụ hiển thị:**

- Danh sách tất cả sản phẩm trong đơn hàng
- Sản phẩm thuộc một nhóm hàng
- Form đăng ký user với các trường đã điền sẵn (khi có lỗi validation)

### 4.2. View KHÔNG làm gì?

| ❌ Sai | ✅ Đúng |
|--------|---------|
| View tự kết nối database để xóa sản phẩm | View hiển thị nút "Xóa"; khi bấm, gửi request — **Controller** xử lý xóa |
| View chứa logic tính tổng tiền đơn hàng | Logic tính toán nằm ở **Service**; View chỉ hiển thị kết quả |
| View quyết định user có quyền xóa không | **Service** kiểm tra quyền; Controller quyết định hiển thị hay trả lỗi |

> **Nguyên tắc:** View càng "ngu ngốc" (dumb) càng tốt — chỉ hiển thị và thu thập input, không chứa logic nghiệp vụ.

### 4.3. Hai kiểu View phổ biến trong khóa học

| Kiểu | Mô tả | Học ở bài |
|------|-------|-----------|
| **Server-side rendering (SSR)** | Server render HTML (Thymeleaf) rồi trả về trình duyệt | Bài 4 |
| **Client + REST API** | Frontend (React, mobile) gọi API, nhận JSON, tự render | Bài 5–6 |

Cả hai đều tuân thủ MVC — chỉ khác **View nằm ở đâu** (server hay client).

---

## 5. Controller & Service

### 5.1. Controller

- Là **trung gian** giữa View và Model (thông qua Service).
- **Điều khiển luồng** chương trình: nhận input → gọi xử lý → trả output.
- **Không** chứa logic nghiệp vụ phức tạp — chỉ điều phối.

**Trách nhiệm điển hình:**

1. Nhận HTTP request (URL, form data, JSON body)
2. Gọi **Service** để xử lý
3. Đưa kết quả vào **Spring `Model`** (`model.addAttribute(...)`) hoặc trả JSON
4. Chọn **View** để render (hoặc trả status code + body)

> **Chú ý thuật ngữ — từ "Model" có 2 nghĩa:**
>
> | Nghĩa | Là gì | Ví dụ |
> |-------|-------|-------|
> | **MVC Model (layer)** | Class dữ liệu / nghiệp vụ | `Product.java`, `BookService` |
> | **Spring Model (object)** | Hộp chứa attribute truyền sang View | `model.addAttribute("products", list)` |
>
> Hai khái niệm **không trùng nhau** — đừng nhầm `org.springframework.ui.Model` với MVC Model layer.

### 5.2. Service — tầng bổ sung thực tế

Trong ứng dụng thực tế, Controller thường **không gọi Repository trực tiếp** mà qua **Service**:

```mermaid
flowchart TD
    C["Controller<br/>Điều phối request"] --> S["Service<br/>Logic nghiệp vụ"]
    S --> R["Repository<br/>Truy cập database"]
    R --> DB[("Database")]
    S --> S2["Service khác<br/>(nếu cần)"]
```

| Layer | Vai trò | Ví dụ Spring |
|-------|---------|--------------|
| **Controller** | Nhận request, trả response | `@Controller`, `@RestController` |
| **Service** | Logic nghiệp vụ, transaction | `@Service` |
| **Repository** | CRUD database | `@Repository`, JpaRepository |

**Ví dụ pseudocode — luồng xóa sản phẩm:**

```java
// Controller — chỉ điều phối
class ProductController {
    private final ProductService productService;

    String deleteProduct(Long id) {
        productService.delete(id);
        return "redirect:/products";  // quay về danh sách
    }
}

// Service — logic nghiệp vụ
class ProductService {
    void delete(Long id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.isInActiveOrder()) {
            throw new BusinessException("Cannot delete product in active order");
        }
        repository.deleteById(id);
    }
}
```

> View chỉ hiển thị nút "Xóa" (form `POST` hoặc `DELETE` tới `/products/{id}`). Toàn bộ kiểm tra và xóa nằm ở Controller → Service → Repository.

---

## 6. Luồng xử lý (workflow)

### 6.1. Ba bước cốt lõi

1. **Nhận input** — Controller nhận thao tác từ người dùng (HTTP request từ trình duyệt hoặc client).
2. **Xử lý** — Controller gọi Service → Service gọi Repository/Model để đọc hoặc ghi dữ liệu.
3. **Trả output** — Controller gửi kết quả về View (HTML) hoặc client (JSON).

### 6.2. Luồng MVC truyền thống (Server-side)

Dùng khi View là **Thymeleaf** — học ở Bài 4.

```mermaid
sequenceDiagram
    participant U as Browser
    participant DS as DispatcherServlet
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant DB as Database
    participant V as View (Thymeleaf)

    U->>DS: HTTP GET /products
    DS->>C: dispatch request
    C->>S: getAllProducts()
    S->>R: findAll()
    R->>DB: SELECT ...
    DB-->>R: rows
    R-->>S: List<Product>
    S-->>C: List<Product>
    C->>C: model.addAttribute("products", list)
    C-->>DS: return "products"
    DS->>V: render template products.html
    V-->>U: HTTP 200 + HTML
```

> **Lưu ý:** Trên nhánh **request**, trình duyệt gửi HTTP **thẳng tới server** (qua `DispatcherServlet` → Controller) — View (Thymeleaf) **không** nằm giữa Browser và Controller. View chỉ tham gia ở nhánh **response** khi server render HTML.

### 6.3. Luồng MVC + REST API

Dùng khi View là **frontend riêng** (React, mobile app) — học ở Bài 5–6.

```mermaid
sequenceDiagram
    participant C as Client (React / Mobile)
    participant CT as @RestController
    participant S as Service
    participant R as Repository
    participant DB as Database

    C->>CT: GET /api/v1/products
    CT->>S: getAllProducts()
    S->>R: findAll()
    R->>DB: SELECT ...
    DB-->>R: rows
    R-->>S: List<Product>
    S-->>CT: List<Product>
    CT-->>C: HTTP 200 + JSON
    C->>C: Render UI từ JSON
```

### 6.4. So sánh hai luồng

| Tiêu chí | MVC + Thymeleaf (SSR) | MVC + REST API |
|----------|----------------------|----------------|
| **View ở đâu** | Server (Thymeleaf template) | Client (React, Vue, mobile) |
| **Response** | HTML | JSON |
| **Controller** | `@Controller` + `return "view-name"` | `@RestController` + return object |
| **Phù hợp** | Website truyền thống, admin panel | SPA, mobile app, microservices |

**Tóm tắt luồng (SSR):**

```
Request:  Browser → Tomcat → DispatcherServlet → Controller → Service → Repository → DB
Response: DB → Repository → Service → Controller → Thymeleaf → HTML → Browser
```

### 6.5. Ví dụ end-to-end — Xem danh sách sách

Kịch bản: độc giả mở trang danh sách sách trên web thư viện.

| Bước | Layer | Việc xảy ra |
|------|-------|-------------|
| 1 | **Browser** | Gửi `GET /books` |
| 2 | **Controller** | Nhận request, gọi `bookService.getAvailableBooks()` |
| 3 | **Service** | Lọc sách còn tồn kho, gọi `bookRepository.findAll()` |
| 4 | **Repository** | `SELECT * FROM books WHERE quantity > 0` |
| 5 | **Controller** | `model.addAttribute("books", list)` → `return "books/list"` |
| 6 | **View** | Thymeleaf render `books/list.html` với `${books}` |
| 7 | **Browser** | Nhận HTML, hiển thị bảng danh sách sách |

```
User click "Danh sách sách"
  → Browser: GET /books
  → BookController.list()
  → BookService.getAvailableBooks()
  → BookRepository.findAll()
  → BookController: model + "books/list"
  → Thymeleaf render HTML
  → User thấy trang web
```

---

## 7. Kiến trúc phân lớp trong Spring Boot

Bảng map từ khái niệm MVC sang cấu trúc project Spring Boot — sẽ thực hành chi tiết từ Bài 4:

| Khái niệm MVC | Package / Annotation | Vai trò |
|---------------|----------------------|---------|
| **Model (Domain)** | `…model` hoặc `…entity` | Class dữ liệu: `Product`, `User` |
| **Repository** | `…repository` + `@Repository` | Giao tiếp database |
| **Service** | `…service` + `@Service` | Logic nghiệp vụ |
| **Controller** | `…controller` + `@Controller` | Nhận request, trả HTML |
| **View** | `src/main/resources/templates/` | File `.html` Thymeleaf |
| **REST Controller** | `…controller` + `@RestController` | Nhận request, trả JSON |

**Cấu trúc thư mục gợi ý (học dần qua các bài):**

```
com.myapp.demo/
├── DemoApplication.java
├── controller/
│   ├── ProductController.java      ← @Controller (HTML)
│   └── ProductApiController.java   ← @RestController (JSON) — Bài 5
├── service/
│   └── ProductService.java         ← @Service
├── repository/
│   └── ProductRepository.java      ← @Repository — bài sau
└── model/
    └── Product.java                ← Domain class

src/main/resources/templates/
└── products/
    └── list.html                   ← View (Thymeleaf)
```

```mermaid
flowchart TD
  subgraph Client
    B["Browser / Mobile App"]
  end
  subgraph Server["Spring Boot Server"]
    C["Controller"]
    S["Service"]
    R["Repository"]
    V["View (Thymeleaf)"]
    C --> S --> R
    C --> V
  end
  DB[("Database")]
  B -->|"HTTP Request"| C
  V -->|"HTML"| B
  C -->|"JSON (@RestController)"| B
  R --> DB
```

---

## 8. Tại sao nên sử dụng MVC

| Lợi ích | Giải thích | Ví dụ |
|---------|------------|-------|
| **Phát triển song song** | Nhiều thành viên làm các layer khác nhau | Team A làm API, Team B làm giao diện |
| **Dễ đổi database** | Chỉ sửa Repository | MySQL → PostgreSQL |
| **Dễ đổi giao diện** | Chỉ sửa View / template | Đổi theme, layout, thư viện CSS |
| **Dễ đổi logic xử lý** | Tập trung ở Service | Thêm validation, đổi quy tắc tính giá |
| **Dễ test** | Test Service độc lập, không cần UI | Unit test `ProductService` |
| **Tái sử dụng** | Cùng Service cho web + REST API | `ProductService` dùng cho cả Thymeleaf và `@RestController` |

**Ví dụ thay đổi theo layer:**

| Thay đổi | Layer bị ảnh hưởng | Layer không đổi |
|----------|-------------------|-----------------|
| Thêm thuộc tính `discount` cho Product | Model, có thể Service | Controller (nếu API không đổi) |
| Đổi màu sắc trang danh sách sản phẩm | View (CSS, template) | Service, Repository |
| Thêm endpoint API mới | Controller | Service (tái sử dụng method cũ) |
| Đổi từ MySQL sang PostgreSQL | Repository, config | Controller, View |

---

## 9. Hạn chế của MVC

| Hạn chế | Giải thích |
|---------|------------|
| **Tăng độ phức tạp** | App CRUD đơn giản có thể cần 4–5 class cho 1 entity |
| **Nhiều nguồn lực** | Cần hiểu kiến trúc trước khi code hiệu quả |
| **Không phù hợp app nhỏ** | Script nội bộ, prototype nhanh — cấu trúc phẳng có thể đủ |
| **Code trùng lặp** | Mapping Entity ↔ DTO; validation ở nhiều layer |
| **Learning curve** | Học viên mới dễ nhầm trách nhiệm từng layer |

### Khi nào vẫn nên dùng MVC?

- Team từ **2 người** trở lên
- Ứng dụng **sống lâu**, nhiều tính năng
- Cần **cả web UI và API** (mobile, tích hợp bên thứ ba)
- Yêu cầu **bảo trì, mở rộng** rõ ràng

### Khi có thể đơn giản hóa

- Demo / POC trong vài ngày
- Ứng dụng 1 người viết, 1–2 màn hình, không mở rộng

---

## 10. Anti-pattern thường gặp

| Anti-pattern | Mô tả | Cách tránh |
|--------------|-------|------------|
| **Fat Controller** | Nhét toàn bộ logic nghiệp vụ vào Controller | Chuyển logic sang **Service** |
| **Anemic Model** | Class Model chỉ có getter/setter, không có quy tắc | Đặt validation / business rule hợp lý vào Model hoặc Service |
| **View gọi database** | Template hoặc JSP chứa SQL | View chỉ hiển thị; data qua Controller |
| **Controller gọi Repository trực tiếp** | Bỏ qua Service layer | Luôn qua Service (trừ demo rất nhỏ) |
| **God Service** | 1 Service class xử lý mọi thứ | Chia theo domain: `ProductService`, `OrderService` |

**Ví dụ Fat Controller (tránh):**

```java
// ❌ Anti-pattern — logic nghiệp vụ trong Controller
@GetMapping("/products/{id}/delete")
public String delete(@PathVariable Long id) {
    Product p = repository.findById(id).orElseThrow();
    if (p.getQuantity() > 0 && orderRepository.existsByProductId(id)) {
        throw new RuntimeException("Cannot delete");
    }
    repository.deleteById(id);
    // ... thêm 50 dòng logic khác
    return "redirect:/products";
}
```

```java
// ✅ Đúng — Controller mỏng
@DeleteMapping("/products/{id}")
public String delete(@PathVariable Long id) {
    productService.delete(id);
    return "redirect:/products";
}
```

> Ví dụ ❌ còn dùng `GET` để xóa dữ liệu — vi phạm HTTP semantics (thao tác thay đổi dữ liệu nên dùng `POST`/`DELETE`, không dùng `GET`). Quy ước RESTful học chi tiết ở **Bài 5**.

---

## 11. Các biến thể liên quan (MVP, MVVM)

MVC không phải mô hình duy nhất. Biết thêm để mở rộng tầm nhìn — **không bắt buộc đi sâu** trong khóa này.

| Mô hình | Đặc điểm | Dùng phổ biến ở |
|---------|----------|-----------------|
| **MVC** | Controller điều phối View và Model | Spring, Ruby on Rails |
| **MVP** | Presenter thay Controller; View "passive" hơn | Android (một số pattern cũ) |
| **MVVM** | ViewModel + data binding 2 chiều | WPF, Angular, Vue |

Trong khóa **Lập trình viên Java**, ta tập trung **MVC qua Spring Boot** — nền tảng đủ cho hầu hết dự án web doanh nghiệp.

---

## 12. Lỗi hiểu sai thường gặp

| Hiểu sai | Sự thật |
|----------|---------|
| "Model = bảng database" | Model (layer) = class domain; bảng DB là persistence — qua **Repository** |
| "Thymeleaf là Controller" | Thymeleaf là **View**; Controller chỉ trả tên template |
| "REST API không phải MVC" | Vẫn là MVC — **View nằm ở client** (React, mobile), server trả JSON |
| "Controller = Service" | Controller **điều phối**; Service **xử lý nghiệp vụ** |
| "`model.addAttribute` = MVC Model layer" | Đó là **Spring Model object** — khác với domain class `Product` |
| "View gọi API trong SSR" | Trong SSR, **trình duyệt** gửi HTTP request; View chỉ render HTML từ server |
| "Một class Product là đủ cho mọi layer" | Có thể cần **Entity**, **DTO**, **Form object** — tùy ngữ cảnh |

---

## Tóm tắt

| Khái niệm | Ý chính |
|-----------|---------|
| **MVC** | Model – View – Controller; tách dữ liệu, giao diện, điều phối |
| **Model** | Domain data + business rules; **không** đồng nghĩa Repository |
| **View** | Hiển thị UI, gửi thao tác user; **không** chứa logic nghiệp vụ |
| **Controller** | Nhận request, gọi Service, chọn view/response |
| **Service** | Logic nghiệp vụ — tầng bổ sung thực tế giữa Controller và Repository |
| **MVC vs Spring MVC** | Pattern (ý tưởng) vs Framework (công cụ triển khai) |
| **Spring Model vs MVC Model** | `model.addAttribute(...)` ≠ domain class `Product` |
| **DTO** | Class trao đổi dữ liệu — tách khỏi Entity |
| **Hai luồng** | SSR (Thymeleaf) — Bài 4; REST + JSON — Bài 5–6 |

**Chuẩn bị cho Bài 4:** Tạo project Spring Boot, viết `@Controller` + trang Thymeleaf Hello World — áp dụng trực tiếp mô hình MVC vừa học.

---

## Phụ lục

### Bài tập 1 — Vẽ sơ đồ MVC

Vẽ sơ đồ luồng xử lý (giấy hoặc công cụ như draw.io) cho chức năng **"Độc giả mượn sách"** trong hệ thống Quản lý thư viện (đồ án Module 1).

**Yêu cầu:**

1. Xác định ít nhất 3 class **Model** (ví dụ: `Book`, `Reader`, `Loan`)
2. Mô tả **View** — màn hình/form nào người dùng thấy?
3. **Controller** nhận request gì? Gọi Service nào?
4. Vẽ mũi tên: **Request** User → Browser → Controller → Service → Repository → Database; **Response** Controller → View (Thymeleaf) → Browser

### Bài tập 2 — Phân loại trách nhiệm

Với hệ thống bán hàng online, phân loại các hành động sau vào đúng layer:

| Hành động | Layer (điền) |
|-----------|--------------|
| Hiển thị danh sách sản phẩm dạng bảng HTML | |
| Kiểm tra số lượng tồn kho trước khi đặt hàng | |
| Lưu đơn hàng vào bảng `orders` | |
| Nhận HTTP POST `/api/v1/orders` | |
| Render template `orders/detail.html` | |

<details>
<summary>Đáp án gợi ý</summary>

| Hành động | Layer |
|-----------|-------|
| Hiển thị danh sách sản phẩm dạng bảng HTML | **View** |
| Kiểm tra số lượng tồn kho trước khi đặt hàng | **Service** |
| Lưu đơn hàng vào bảng `orders` | **Repository** |
| Nhận HTTP POST `/api/v1/orders` | **Controller** |
| Render template `orders/detail.html` | **View** (Thymeleaf) — được Controller chọn |

</details>

### Bài tập 3 — Trace luồng xử lý

Điền layer đảm nhiệm từng bước khi user **đặt hàng** trên web:

| Bước | Mô tả | Layer |
|------|-------|-------|
| 1 | Trình duyệt gửi `POST /orders` kèm form data | |
| 2 | Kiểm tra tồn kho đủ trước khi tạo đơn | |
| 3 | `INSERT INTO orders (...)` | |
| 4 | `return "orders/success"` | |
| 5 | Hiển thị trang "Đặt hàng thành công" | |

<details>
<summary>Đáp án gợi ý</summary>

| Bước | Layer |
|------|-------|
| 1 | **Controller** (nhận HTTP request) |
| 2 | **Service** (logic nghiệp vụ) |
| 3 | **Repository** (ghi database) |
| 4 | **Controller** (chọn view trả về) |
| 5 | **View** (Thymeleaf render HTML) |

</details>

### Câu hỏi ôn tập

1. MVC là viết tắt của gì? Mỗi thành phần có vai trò gì?
2. Tại sao không nên để View tự kết nối database?
3. Service layer giải quyết vấn đề gì mà Controller không nên làm trực tiếp?
4. Khác biệt giữa **MVC pattern** và **Spring MVC**?
5. Phân biệt **MVC Model layer** và **Spring `Model` object** (`model.addAttribute`)?
6. DTO là gì? Khác Entity ở điểm nào?
7. Khi nào dùng `@Controller` và khi nào dùng `@RestController`? *(gợi ý: xem Bài 5)*
8. Nêu 2 ưu điểm và 2 hạn chế của MVC.
9. Fat Controller là gì? Làm sao tránh?

### Liên kết tham khảo

- [Spring Web MVC — Introduction](https://docs.spring.io/spring-framework/reference/web/webmvc.html#mvc-introduction)
- [Martin Fowler — GUI Architectures (MVC)](https://martinfowler.com/eaaDev/uiArchs.html)
- [Baeldung — A Quick Intro to the Spring MVC](https://www.baeldung.com/spring-mvc)
- [Baeldung — The MVC Pattern](https://www.baeldung.com/java-web-app-architectures)
