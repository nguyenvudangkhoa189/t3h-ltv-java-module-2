package vn.demo.service;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

	public boolean isValidPrice(double price) {
		return price > 0;
	}

}
