package vn.demo.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.demo.external.config.DummyJsonProperties;

/**
 * Bài tập 2 — Lấy sản phẩm theo category.
 * Tái dùng bean RestClient + DummyJsonProperties của phần external.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HomeworkProductService {

	private final RestClient restClient;
	private final DummyJsonProperties dummyJsonProperties;

	public JsonNode fetchProductsByCategory(String category) {
		String url = dummyJsonProperties.getBaseUrl() + "/products/category/{category}";
		log.debug("Fetching products by category: {}", category);
		return restClient.get().uri(url, category).retrieve().body(JsonNode.class);
	}

}
