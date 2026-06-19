package vn.demo.servicelayer.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.demo.servicelayer.model.Account;
import vn.demo.servicelayer.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

	private static final String EMAIL_PATTERN =
			"^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	private final AccountRepository accountRepository;
	private final OrderService orderService;

	public Account findAnyAccountByName(String name) {
		return accountRepository.findByName(name);
	}

	public Boolean validEmailFormat(String emailAddress) {
		return Pattern.compile(EMAIL_PATTERN)
				.matcher(emailAddress)
				.matches();
	}

	public List<String> getOrders(String userId) {
		return orderService.getHistoricalOrders(userId);
	}

	public void save(Account account) {
		accountRepository.save(account);
	}

}
