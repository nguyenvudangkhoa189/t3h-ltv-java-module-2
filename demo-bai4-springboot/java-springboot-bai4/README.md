# Demo Bài 4 — Spring Boot cơ bản & Thymeleaf

Project demo cho syllabus `java_m2_bai4_SpringBoot.md`. Code được **chia package theo từng phần demo** để dễ hình dung package nào minh hoạ mục nào trong bài.

| Package | Mục syllabus | Demo gì |
|---------|--------------|---------|
| `vn.demo.basic` | Mục 5 | Hello World cơ bản — `@Controller` + Thymeleaf (Cách 1) |
| `vn.demo.extended` | Mục 5 — Bài mở rộng | Redirect trang chủ + CSS tĩnh + `LocalDateTime` |
| `vn.demo.engine` | Mục 6 | `SpringTemplateEngine.process()` (Cách 2) + email trong Service |
| `vn.demo.enterprise` | Mục 6.5 | Enterprise best practice — DI, validation, Post-Redirect-Get |

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

## Cấu trúc project

```
java-springboot-bai4/
├── pom.xml
├── mvnw
└── src/main/
    ├── java/vn/demo/
    │   ├── DemoBai4SpringbootApplication.java   ← entry point (@SpringBootApplication)
    │   │
    │   ├── basic/                               ← Mục 5: Hello World cơ bản
    │   │   └── controller/HelloController.java
    │   │
    │   ├── extended/                            ← Mục 5 mở rộng: redirect + CSS + thời gian
    │   │   └── controller/HelloStyleController.java
    │   │
    │   ├── engine/                              ← Mục 6: SpringTemplateEngine (Cách 2)
    │   │   ├── controller/EngineDemoController.java
    │   │   └── service/EmailService.java
    │   │
    │   └── enterprise/                          ← Mục 6.5: enterprise best practice
    │       ├── controller/StudentController.java
    │       ├── service/StudentService.java
    │       └── model/
    │           ├── Student.java
    │           └── StudentForm.java
    │
    └── resources/
        ├── application.properties
        ├── static/css/style.css                 ← file tĩnh dùng chung (/css/style.css)
        └── templates/                           ← mỗi feature một thư mục con
            ├── basic/hello.html
            ├── extended/hello-style.html
            ├── engine/
            │   ├── hello.html                   ← template cho process() demo
            │   └── welcome-email.html           ← template email (EmailService)
            └── enterprise/students/
                ├── list.html
                ├── detail.html
                └── form.html
```

> `@SpringBootApplication` nằm ở `vn.demo` nên quét được toàn bộ package con (`basic`, `extended`, `engine`, `enterprise`). View name trả về khớp thư mục template, ví dụ `return "basic/hello"` → `templates/basic/hello.html`.

### Quy ước trong mỗi package

| Sub-package | Vai trò |
|-------------|---------|
| `controller/` | `@Controller` (trả view) hoặc `@RestController` (engine demo) |
| `service/` | Business logic, `@Service`, inject qua constructor |
| `model/` | Dữ liệu hiển thị / form object |

### Luồng dạy gợi ý

```
1. basic       → @Controller + Model + Thymeleaf (Cách 1)
2. extended    → redirect, static CSS, LocalDateTime
3. engine      → SpringTemplateEngine.process() (Cách 2) + email trong Service
4. enterprise  → DI, validation, redirect sau POST
```

---

## Bảng URL demo — tra nhanh khi dạy

| # | Package | Tool test | URL / Method | File chính |
|---|---------|-----------|--------------|------------|
| 1 | `basic` | Browser | `GET /hello` | `HelloController`, `templates/basic/hello.html` |
| 2 | `extended` | Browser | `GET /` → redirect `/hello-style` | `HelloStyleController` |
| 3 | `extended` | Browser | `GET /hello-style` | `HelloStyleController`, `static/css/style.css` |
| 4 | `engine` | Browser | `GET /demo/engine/hello` | `EngineDemoController`, `templates/engine/hello.html` |
| 5 | `engine` | Browser | `GET /demo/email/preview` | `EmailService`, `templates/engine/welcome-email.html` |
| 6 | `enterprise` | Browser | `GET /students` | `StudentController`, `StudentService` |
| 7 | `enterprise` | Browser | `GET /students/1` | `StudentController`, `templates/enterprise/students/detail.html` |
| 8 | `enterprise` | Browser | `GET /students/new` → submit form | `StudentForm`, `templates/enterprise/students/form.html` |
| 9 | `enterprise` | Browser | `POST /students` → redirect `/students` | `StudentController` |

> **Hai cách render Thymeleaf:** Trang web SSR → `@Controller` + `return "view-name"` (package `basic`/`extended`/`enterprise`). Email / job nền → `SpringTemplateEngine.process()` trong **Service** (package `engine`).

---

## Liên kết

- Syllabus: [`syllabus/module-2/java_m2_bai4_SpringBoot.md`](../../syllabus/module-2/java_m2_bai4_SpringBoot.md)
- Demo bài 5 (REST API part 1): [`demo-bai5-springmvc/java-springboot-bai5`](../../demo-bai5-springmvc/java-springboot-bai5)
- Demo bài 6 (Service, Validation, Lombok): [`demo-bai6-springmvc/java-springboot-bai6`](../../demo-bai6-springmvc/java-springboot-bai6)
