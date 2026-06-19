package vn.demo.delete.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/songs")
public class SongController {

	@DeleteMapping
	public ResponseEntity<Void> deleteSongByQuery(
			@RequestParam String title,
			@RequestParam(required = false) String theme) {
		System.out.println("Delete song title: " + title);
		System.out.println("Theme: " + theme);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSongById(@PathVariable String id) {
		System.out.println("Delete song id: " + id);
		return ResponseEntity.noContent().build();
	}

}
