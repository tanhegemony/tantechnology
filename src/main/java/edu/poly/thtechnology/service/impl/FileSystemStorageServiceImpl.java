package edu.poly.thtechnology.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.poly.thtechnology.config.StorageProperties;
import edu.poly.thtechnology.exception.StorageException;
import edu.poly.thtechnology.exception.StorageFileNotFoundException;
import edu.poly.thtechnology.service.StorageService;

@Service
public class FileSystemStorageServiceImpl implements StorageService{
	// xác định đường dẫn gốc để lưu hình ảnh
	private final Path rootLocation;
	
	// lưu thông tin và lấy thông tin tên file được lưu trữ
	@Override
	public String getStoredFilename(MultipartFile file, String id) {
//		đuôi file ảnh
		String ext = FilenameUtils.getExtension(file.getOriginalFilename());
		return "p" + id + "." + ext;
	}
	
	// truyền thông tin cấu hình cho phần lưu trữ
	public FileSystemStorageServiceImpl(StorageProperties properties) {
		this.rootLocation=Paths.get(properties.getLocation());
	}
	
	// lưu nội dung của file từ thành phần multipartfile
	@Override
	public void store(MultipartFile file, String storedFilename) {
		try {
			if(file.isEmpty()) {
				throw new StorageException("Failed to store empty file");
			}
			Path destinationFile = this.rootLocation.resolve(Paths.get(storedFilename)).normalize()
					.toAbsolutePath();
			if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Cannot store file outside current directory");
			}
			try(InputStream inputStream = file.getInputStream()){
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			throw new StorageException("Failed to store file", e);
		}
	}
	
	// nạp nội dung của file dưới dạng resource
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if(resource.exists() || resource.isReadable()) {
				return resource;
			}
			throw new StorageFileNotFoundException("Could not read file: " + filename);
		} catch (Exception e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename);
		}
	}
	
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}
	
	// xóa file khi không cần thiết
	@Override
	public void delete(String storedFilename) throws IOException {
		Path destinationFile = rootLocation.resolve(Paths.get(storedFilename)).normalize().toAbsolutePath();
		Files.delete(destinationFile);
	}
	
	// khởi tạo các thư mục
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
			System.out.println(rootLocation.toString());
		} catch (Exception e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}
