package vn.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Service lưu file upload (ảnh avatar) xuống thư mục trên ổ đĩa.
 *
 * <p>Chỉ chấp nhận một số định dạng ảnh, đổi tên file thành UUID để tránh trùng,
 * và trả về URL công khai dạng {@code /uploads/...} để hiển thị trên trình duyệt.</p>
 */
@Slf4j
@Service
public class FileStorageService {

	/** Các loại ảnh được phép upload. */
	private static final Set<String> ALLOWED_TYPES = Set.of(
			"image/jpeg", "image/png", "image/gif", "image/webp"
	);

	/** Thư mục gốc chứa file upload (đường dẫn tuyệt đối). */
	private final Path uploadRoot;

	/**
	 * Tạo service và đảm bảo thư mục upload đã tồn tại.
	 *
	 * @param uploadDir đường dẫn thư mục upload (key {@code app.upload.dir})
	 * @throws IOException nếu không tạo được thư mục
	 */
	public FileStorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
		this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(uploadRoot);
		log.info("Upload root initialized: {}", uploadRoot);
	}

	/**
	 * Lưu một file ảnh vào thư mục con và trả về URL công khai.
	 *
	 * @param file      file upload từ form
	 * @param subFolder thư mục con để phân loại (vd. {@code "avatars"})
	 * @return URL công khai dạng {@code /uploads/{subFolder}/{uuid.ext}}
	 * @throws IllegalArgumentException nếu file rỗng hoặc sai định dạng ảnh
	 * @throws IOException              nếu ghi file thất bại
	 */
	public String store(MultipartFile file, String subFolder) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File is empty");
		}

		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
			throw new IllegalArgumentException("Chỉ chấp nhận ảnh: JPEG, PNG, GIF, WEBP");
		}

		String originalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
		String extension = "";
		int dot = originalName.lastIndexOf('.');
		if (dot > 0) {
			extension = originalName.substring(dot);
		}

		String savedName = UUID.randomUUID() + extension;
		Path targetDir = uploadRoot.resolve(subFolder);
		Files.createDirectories(targetDir);
		Path targetFile = targetDir.resolve(savedName);

		Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
		String publicUrl = "/uploads/" + subFolder + "/" + savedName;
		log.debug("Stored file: {} → {}", originalName, publicUrl);
		return publicUrl;
	}

}
