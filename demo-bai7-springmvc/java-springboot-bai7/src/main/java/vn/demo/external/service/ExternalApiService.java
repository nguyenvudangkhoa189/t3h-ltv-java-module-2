package vn.demo.external.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.demo.external.config.DummyJsonProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

	private final RestClient restClient;
	private final DummyJsonProperties dummyJsonProperties;

	public JsonNode fetchProducts(int limit) {
		String url = dummyJsonProperties.getBaseUrl() + "/products?limit={limit}";
		log.debug("Fetching products: limit={}", limit);
		return restClient.get().uri(url, limit).retrieve().body(JsonNode.class);
	}

	public JsonNode fetchCategories() {
		String url = dummyJsonProperties.getBaseUrl() + "/products/categories";
		return restClient.get().uri(url).retrieve().body(JsonNode.class);
	}

	public JsonNode fetchUsers(int limit) {
		String url = dummyJsonProperties.getBaseUrl() + "/users?limit={limit}";
		return restClient.get().uri(url, limit).retrieve().body(JsonNode.class);
	}

	public JsonNode fetchUserById(long id) {
		String url = dummyJsonProperties.getBaseUrl() + "/users/{id}";
		return restClient.get().uri(url, id).retrieve().body(JsonNode.class);
	}

}
