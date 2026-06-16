package vn.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.demo.model.UserForm;

@Data
@AllArgsConstructor
public class UserPage {

	private List<UserForm> users;
	private String query;
	private int page;
	private int pageSize;
	private int totalItems;
	private int totalPages;

	public boolean hasPrevious() {
		return page > 1;
	}

	public boolean hasNext() {
		return page < totalPages;
	}

}
