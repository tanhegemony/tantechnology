package edu.poly.thtechnology.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.thtechnology.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	// tìm kiếm theo tên
	List<Category> findByNameContaining(String name);
	// phân trang
	Page<Category> findByNameContaining(String name, Pageable pageable);
}
