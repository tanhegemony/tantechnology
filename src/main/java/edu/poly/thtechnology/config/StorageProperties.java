package edu.poly.thtechnology.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("storage")
@Data
public class StorageProperties {
	// vị trí lưu file đc upload lên server
	private String location;
}
