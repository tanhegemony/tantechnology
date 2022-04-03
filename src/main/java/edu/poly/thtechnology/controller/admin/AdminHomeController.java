package edu.poly.thtechnology.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.domain.Product;
import edu.poly.thtechnology.model.AdminLoginDto;
import edu.poly.thtechnology.service.AccountService;
import edu.poly.thtechnology.service.CategoryService;
import edu.poly.thtechnology.service.ProductService;

@Controller
@RequestMapping("admin")
public class AdminHomeController {
	@Autowired
	AccountService accountService;

	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	HttpSession session;
	
	@RequestMapping("")
	public String searchProduct(ModelMap model
			,@RequestParam(name = "name", required = false) String name
			,@RequestParam("page") Optional<Integer> page
			,@RequestParam("size") Optional<Integer> size) {

		if(session.getAttribute("username") != null) {
			Optional<Account> account = accountService.findById((String) session.getAttribute("username"));
			if(account.get().isAdmin() == true) {
				AdminLoginDto dto = new AdminLoginDto();
				dto.setIsAdmin(true);
				model.addAttribute("account", dto);
			}else {
				AdminLoginDto dto = new AdminLoginDto();
				dto.setIsAdmin(false);
				model.addAttribute("account", dto);
			}
			// mặc định là 1 trang
			int currentPage = page.orElse(1);
			// mặc định mỗi trang có 5 phần tử
			int pageSize = size.orElse(5);
			
			// tạo đối tượng pageable gồm trang hiện tại, số phần tử 1 trang và sắp xếp theo name
			Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("productID"));
			Page<Product> resultPage = null;
			
			// nếu name truyền về có giá trị
			if(StringUtils.hasText(name)) {
				// trả về danh sách các product có tên là name
				resultPage = productService.findByNameContaining(name, pageable);
				model.addAttribute("name", name);
			}else {
				// trả về tất cả các product
				resultPage = productService.findAll(pageable);
			}
			
			// trả về tổng số các trang
			int totalPages = resultPage.getTotalPages();
			
			if(totalPages>0) {
				// không thể là số âm
				int start = Math.max(1, currentPage-2);
				// không vượt quá tổng số trang
				int end = Math.min(currentPage+2, totalPages);
				
				if(totalPages>5) {
					if(end == totalPages) {
						start = end - 5; 
					}else if(start == 1) {
						end = start + 5;
					}
				}
				
				List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
						.boxed().collect(Collectors.toList());
				model.addAttribute("pageNumbers", pageNumbers);
	 		}
			model.addAttribute("productPage", resultPage);
			return "admin/adminHome";
		}
		
		
		return "admin/adminHome";
	}
}
