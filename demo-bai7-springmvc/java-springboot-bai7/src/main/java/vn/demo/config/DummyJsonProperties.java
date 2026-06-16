package vn.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.external.dummyjson")
public class DummyJsonProperties {

	private String baseUrl = "https://dummyjson.com";

}
