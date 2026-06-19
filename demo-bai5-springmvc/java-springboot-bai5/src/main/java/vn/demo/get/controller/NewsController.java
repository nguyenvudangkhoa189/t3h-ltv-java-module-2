package vn.demo.get.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.demo.get.dto.NewsDto;

@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

	@GetMapping(value = "/latest", produces = MediaType.APPLICATION_JSON_VALUE)
	public NewsDto getLatestNews() {
		return new NewsDto("Michael", 45);
	}

}
