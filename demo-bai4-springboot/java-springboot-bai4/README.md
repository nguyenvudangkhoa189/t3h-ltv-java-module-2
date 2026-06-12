# Demo Bài 4 — Spring Boot cơ bản & Thymeleaf

Project demo cho syllabus `java_m2_bai4_SpringBoot.md`. Gom tất cả ví dụ trong một Spring Boot app: Hello World Thymeleaf, static CSS, hai cách render template, và enterprise pattern với `StudentController`.

## Chạy project

```bash
cd demo-bai4-springboot/java-springboot-bai4
./mvnw spring-boot:run
```

Hoặc Run `DemoBai4SpringbootApplication` trong IntelliJ.

- Port mặc định: **8080**
- Nếu port bị chiếm: xem hướng dẫn ở [README gốc](../../README.md)

## Dependencies

| Dependency | Mục đích |
|------------|----------|
| `spring-boot-starter-web` | Spring MVC + embedded Tomcat |
| `spring-boot-starter-thymeleaf` | Render HTML phía server |
| `spring-boot-starter-validation` | Bean Validation cho form (`StudentForm`) |

---

## Cấu trúc project đề xuất

```
demo-bai4-springboot/
└── java-springboot-bai4/
    ├── pom.xml
    ├── mvnw
    └── src/main/java/vn/demo/
        ├── DemoBai4SpringbootApplication.java
        ├── controller/
        │   ├── HelloController.java           ← mục 5: Hello World cơ bản (Cách 1)
        │   ├── HelloStyleController.java      ← mục 5 mở rộng: Redirect + CSS + LocalDateTime
        │   ├── EngineDemoController.java      ← mục 6: SpringTemplateEngine (Cách 2)
        │   └── StudentController.java         ← mục 6.5: Enterprise pattern
        ├── service/
        │   ├── EmailService.java              ← mục 6.6: process() trong Service (email demo)
        │   └── StudentService.java            ← business logic in-memory
        └── model/
            ├── Student.java
            └── StudentForm.java               ← validation form
    └── src/main/resources/
        ├── application.properties
        ├── static/css/style.css               ← file tĩnh (/css/style.css)
        └── templates/
            ├── hello.html                     ← Hello cơ bản
            ├── hello-style.html               ← Hello có CSS + thời gian
            ├── emails/
            │   └── welcome.html               ← template email (EngineDemo / EmailService)
            └── students/
                ├── list.html
                ├── detail.html
                └── form.html                  ← th:field, th:errors
```

### Phân tách package

| Package | Vai trò |
|---------|---------|
| `controller/` | `@Controller` — trả view Thymeleaf; `@RestController` — demo render HTML thủ công |
| `service/` | Business logic, `@Service`, inject qua constructor |
| `model/` | Dữ liệu hiển thị / form object |

### Luồng dạy gợi ý

```
1. DemoBai4SpringbootApplication + Whitelabel   → chạy server, chưa có route /
2. HelloController + hello.html                 → @Controller + Model + Thymeleaf (Cách 1)
3. HelloStyleController + hello-style.html      → redirect, static CSS, LocalDateTime
4. EngineDemoController                         → SpringTemplateEngine.process() (Cách 2)
5. EmailService + emails/welcome.html           → process() trong Service, không qua ViewResolver
6. StudentController + StudentService           → enterprise: DI, validation, redirect sau POST
```

---

## Bảng URL demo — tra nhanh khi dạy

| Thứ tự dạy | Mục | Tool test | URL / Method | File chính |
|------------|-----|-----------|--------------|------------|
| 1 | Khởi động server | Browser | `GET /` → Whitelabel hoặc redirect | `DemoBai4SpringbootApplication` |
| 2 | Hello World cơ bản | Browser | `GET /hello` | `HelloController`, `templates/hello.html` |
| 3 | Redirect trang chủ | Browser | `GET /` → redirect `/hello-style` | `HelloStyleController` |
| 4 | CSS + thời gian động | Browser | `GET /hello-style` | `HelloStyleController`, `static/css/style.css` |
| 5 | Render thủ công (Cách 2) | Browser | `GET /demo/engine/hello` | `EngineDemoController` |
| 6 | Email template trong Service | Browser | `GET /demo/email/preview` | `EmailService`, `templates/emails/welcome.html` |
| 7 | Danh sách sinh viên | Browser | `GET /students` | `StudentController`, `StudentService` |
| 8 | Chi tiết sinh viên | Browser | `GET /students/1` | `StudentController`, `templates/students/detail.html` |
| 9 | Form + validation | Browser | `GET /students/new` → submit form | `StudentForm`, `templates/students/form.html` |
| 10 | Post-Redirect-Get | Browser | `POST /students` → redirect `/students` | `StudentController` |

> **Hai cách render Thymeleaf:** Trang web SSR → `@Controller` + `return "view-name"` (Cách 1). Email / job nền → `SpringTemplateEngine.process()` trong **Service** (Cách 2).

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai4_SpringBoot.md`](../../syllabus/module-2/java_m2_bai4_SpringBoot.md)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../../demo-bai5-springmvc/java-springboot-bai5)
- Demo bài 6 (Service, Validation, Lombok): [`demo-bai6-springmvc/java-springboot-bai6`](../../demo-bai6-springmvc/java-springboot-bai6)
