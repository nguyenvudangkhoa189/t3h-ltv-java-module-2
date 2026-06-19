package vn.demo.servicelayer.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

	public List<String> getHistoricalOrders(String userId) {
		return List.of("pencil", "book", "ruler");
	}

}
