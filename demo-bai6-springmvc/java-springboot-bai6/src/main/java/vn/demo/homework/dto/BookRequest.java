package vn.demo.homework.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRequest {

	@NotBlank(message = "Title is required")
	@Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
	private String title;

	@NotBlank(message = "Author is required")
	private String author;

	@NotNull(message = "Price is required")
	@Min(value = 0, message = "Price must be greater than or equal to 0")
	private Double price;

}
