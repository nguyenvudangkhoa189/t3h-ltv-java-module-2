# t3h-ltv-java-module-2

## Port 8080 bị chiếm (macOS)

Xem process đang dùng port:

```bash
lsof -i :8080
```

Tắt process (graceful):

```bash
lsof -ti :8080 | xargs kill
```

Force kill nếu cần:

```bash
lsof -ti :8080 | xargs kill -9
```

Hoặc đổi port trong `application.properties`:

```properties
server.port=8081
```
