package edu.poly.thtechnology.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.poly.thtechnology.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	// tìm kiếm theo tên
		List<Product> findByNameContaining(String name);
		// phân trang
		Page<Product> findByNameContaining(String name, Pageable pageable);
		
		@Query("SELECT p FROM Product p WHERE p.category.id=?1")
		List<Product> findByCategoryId(Long categoryId);
		
		@Query("SELECT p FROM Product p WHERE p.category.id=?1")
		Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
