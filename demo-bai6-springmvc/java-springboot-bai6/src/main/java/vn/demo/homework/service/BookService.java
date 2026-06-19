package vn.demo.homework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import vn.demo.homework.dto.BookRequest;
import vn.demo.homework.model.Book;

@Service
public class BookService {

	private final List<Book> books = new ArrayList<>();
	private final AtomicLong idSequence = new AtomicLong(1);

	public BookService() {
		books.add(new Book(idSequence.getAndIncrement(), "Clean Code", "Robert C. Martin", 35.0));
		books.add(new Book(idSequence.getAndIncrement(), "Effective Java", "Joshua Bloch", 45.0));
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

}
