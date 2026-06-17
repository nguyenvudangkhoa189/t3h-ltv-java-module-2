package vn.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import vn.demo.dto.BookPatchRequest;
import vn.demo.dto.BookRequest;
import vn.demo.model.Book;

@Service
public class BookService {

	private final List<Book> books = new ArrayList<>();
	private final AtomicLong idSequence = new AtomicLong(1);

	public BookService() {
		books.add(new Book(idSequence.getAndIncrement(), "Clean Code", "Robert C. Martin", 35.0));
		books.add(new Book(idSequence.getAndIncrement(), "Effective Java", "Joshua Bloch", 45.0));
	}

	public List<Book> findAll() {
		return List.copyOf(books);
	}

	public List<Book> search(String author, String title) {
		return books.stream()
				.filter(book -> book.getAuthor().equalsIgnoreCase(author))
				.filter(book -> title == null || title.isBlank()
						|| book.getTitle().toLowerCase().contains(title.toLowerCase()))
				.toList();
	}

	public Optional<Book> findById(Long id) {
		return books.stream()
				.filter(book -> book.getId().equals(id))
				.findFirst();
	}

	public Book create(BookRequest request) {
		Book book = new Book(
				idSequence.getAndIncrement(),
				request.getTitle(),
				request.getAuthor(),
				request.getPrice());
		books.add(book);
		return book;
	}

	public Optional<Book> replace(Long id, BookRequest request) {
		return findById(id).map(book -> {
			book.setTitle(request.getTitle());
			book.setAuthor(request.getAuthor());
			book.setPrice(request.getPrice());
			return book;
		});
	}

	public Optional<Book> patch(Long id, BookPatchRequest request) {
		return findById(id).map(book -> {
			if (request.getTitle() != null) {
				book.setTitle(request.getTitle());
			}
			if (request.getAuthor() != null) {
				book.setAuthor(request.getAuthor());
			}
			if (request.getPrice() != null) {
				book.setPrice(request.getPrice());
			}
			return book;
		});
	}

	public boolean delete(Long id) {
		return books.removeIf(book -> book.getId().equals(id));
	}

}
