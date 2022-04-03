package edu.poly.thtechnology.controller.user;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.domain.Category;
import edu.poly.thtechnology.domain.Product;
import edu.poly.thtechnology.model.AdminLoginDto;
import edu.poly.thtechnology.model.CategoryDto;
import edu.poly.thtechnology.model.ProductDto;
import edu.poly.thtechnology.service.AccountService;
import edu.poly.thtechnology.service.CategoryService;
import edu.poly.thtechnology.service.ProductService;

@Controller
@RequestMapping("admin/home")
public class HomeController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;

	// trả về danh sách các category hiện có trong csdl để lựa chọn danh mục cho sản phẩm
	@ModelAttribute("categories")
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
//	tìm kiếm sản phẩm theo danh mục
	@GetMapping("listProductsByCategory/{categoryID}")
	public ModelAndView listProductByCategory(ModelMap model, @PathVariable("categoryID") Long categoryID
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
			// Sản phẩm
			// mặc định là trang 1
			int currentPage = page.orElse(1);
			// mặc định mỗi trang có 9 phần tử
			int pageSize = size.orElse(9);
			
			// tạo đối tượng pageable gồm trang hiện tại, số phần tử 1 trang và sắp xếp theo ngày nhập
			Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("enteredDate"));
			Page<Product> resultPage = null;
			resultPage = productService.findByCategoryId(categoryID, pageable);
			
			// trả về tổng số các trang
			int totalPages = resultPage.getTotalPages();
			
			if(totalPages>0) {
				// không thể là số âm
				int start = Math.max(1, currentPage-2);
				// không vượt quá tổng số trang
				int end = Math.min(currentPage+2, totalPages);
				
				if(totalPages>9) {
					if(end == totalPages) { 
						start = end - 9; 
					}else if(start == 1) {
						end = start + 9;
					}
				}
				
				List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
						.boxed().collect(Collectors.toList());
				model.addAttribute("pageNumbers", pageNumbers);
	 		}
			model.addAttribute("productPage", resultPage);
			List<Product> listProduct = productService.findByCategoryId(categoryID);
			Optional<Category> optC = categoryService.findById(categoryID);
			if(optC.get().getName().equals("Điện thoại")) {
				model.addAttribute("dienthoai", optC.get().getName());
			}else if(optC.get().getName().equals("Laptop")) {
				model.addAttribute("laptop", optC.get().getName());
			}else if(optC.get().getName().equals("Máy tính bảng")) {
				model.addAttribute("maytinhbang", optC.get().getName());
			}else if(optC.get().getName().equals("Apple Store")) {
				model.addAttribute("applestore", optC.get().getName());
			}else if(optC.get().getName().equals("Desktop")) {
				model.addAttribute("desktop", optC.get().getName());
			}else if(optC.get().getName().equals("Phụ kiện")) {
				model.addAttribute("phukien", optC.get().getName());
			}else if(optC.get().getName().equals("Thiết bị văn phòng")) {
				model.addAttribute("thietbivanphong", optC.get().getName());
			}else if(optC.get().getName().equals("Thiết bị mạng")) {
				model.addAttribute("thietbimang", optC.get().getName());
			}
			if(listProduct.isEmpty()) {
				model.addAttribute("message", "Không tìm thấy sản phẩm trong danh mục " + optC.get().getName());
				return new ModelAndView("admin/products/listProductsByCategory", model);
			}
			return new ModelAndView("admin/products/listProductsByCategory", model);
		}
		return new ModelAndView("admin/products/listProductsByCategory", model);
	}
	
	// Sản phẩm trang chủ
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
			// Sản phẩm
			// mặc định là 1 trang
			int currentPage = page.orElse(1);
			// mặc định mỗi trang có 5 phần tử
			int pageSize = size.orElse(10);
			
			// tạo đối tượng pageable gồm trang hiện tại, số phần tử 1 trang và sắp xếp theo name
			Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("enteredDate"));
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
				
				if(totalPages>10) {
					if(end == totalPages) {
						start = end - 10; 
					}else if(start == 1) {
						end = start + 10;
					}
				}
				
				List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
						.boxed().collect(Collectors.toList());
				model.addAttribute("pageNumbers", pageNumbers);
	 		}
			model.addAttribute("productPage", resultPage);
			return "admin/home/home";
		}
		return "admin/home/home";
	}
}
