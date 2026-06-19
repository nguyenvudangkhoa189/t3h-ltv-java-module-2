package vn.demo.upload.service;

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

@Slf4j
@Service
public class FileStorageService {

	private static final Set<String> ALLOWED_TYPES = Set.of(
			"image/jpeg", "image/png", "image/gif", "image/webp"
	);

	private final Path uploadRoot;

	public FileStorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
		this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
		Files.createDirectories(uploadRoot);
		log.info("Upload root initialized: {}", uploadRoot);
	}

	/**
	 * Lưu file vào subFolder (vd: "misc", "avatars") và trả URL public.
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
