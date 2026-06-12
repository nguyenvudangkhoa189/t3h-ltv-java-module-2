package vn.demo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import vn.demo.model.Account;

@Repository
public class AccountRepository {

	private final List<Account> accounts = new ArrayList<>();

	public Account findByName(String name) {
		return accounts.stream()
				.filter(account -> name.equals(account.getUsername()))
				.findFirst()
				.orElse(null);
	}

	public void save(Account account) {
		accounts.add(account);
	}

}
