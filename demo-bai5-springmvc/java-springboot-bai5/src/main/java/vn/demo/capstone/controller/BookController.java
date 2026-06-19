package vn.demo.capstone.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.capstone.dto.BookPatchRequest;
import vn.demo.capstone.dto.BookRequest;
import vn.demo.capstone.model.Book;
import vn.demo.capstone.service.BookService;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public ResponseEntity<List<String>> getAllBooks() {
		List<String> titles = bookService.findAll().stream()
				.map(Book::getTitle)
				.collect(Collectors.toList());
		return ResponseEntity.ok(titles);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Book>> searchBooks(
			@RequestParam String author,
			@RequestParam(required = false) String title) {
		List<Book> result = bookService.search(author, title);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> getBookById(@PathVariable Long id) {
		return bookService.findById(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Book> createBook(@RequestBody BookRequest request) {
		Book created = bookService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> replaceBook(@PathVariable Long id, @RequestBody BookRequest request) {
		return bookService.replace(id, request)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Book> patchBook(@PathVariable Long id, @RequestBody BookPatchRequest request) {
		return bookService.patch(id, request)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
		if (bookService.delete(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

}
