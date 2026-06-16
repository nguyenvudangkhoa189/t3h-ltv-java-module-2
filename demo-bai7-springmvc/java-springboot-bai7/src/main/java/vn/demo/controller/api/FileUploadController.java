package vn.demo.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.demo.dto.FileUploadResponse;
import vn.demo.service.FileStorageService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileUploadController {

	private final FileStorageService fileStorageService;

	@PostMapping("/upload")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
		return handleUpload(file, "misc");
	}

	@PostMapping("/upload-avatar")
	public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
		return handleUpload(file, "avatars");
	}

	private ResponseEntity<?> handleUpload(MultipartFile file, String subFolder) {
		try {
			String url = fileStorageService.store(file, subFolder);
			return ResponseEntity.ok(new FileUploadResponse(url));
		} catch (IllegalArgumentException e) {
			log.warn("Upload rejected: {}", e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (Exception e) {
			log.error("Upload failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Upload failed: " + e.getMessage());
		}
	}

}
