package edu.poly.thtechnology.controller.admin;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import edu.poly.thtechnology.service.StorageService;

@Controller
@RequestMapping("admin/products")
public class ProductController {
	@Autowired
	ProductService productService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	StorageService storageService;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	AccountService accountService;
	
	@ModelAttribute("categories")
	// trả về danh sách các category hiện có trong csdl để lựa chọn danh mục cho sản phẩm
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@GetMapping("addProduct")
	public String add(Model model) {
		if(session.getAttribute("username") != null) {
			Optional<Account> account = accountService.findById((String) session.getAttribute("username"));
			if(account.get().isAdmin() == true) {
				AdminLoginDto dto = new AdminLoginDto();
				dto.setIsAdmin(true);
				model.addAttribute("account", dto);
			}else {
				AdminLoginDto dto = new AdminLoginDto();
				dto.setIsAdmin(true);
				model.addAttribute("account", dto);
			}
			ProductDto dto = new ProductDto();
			dto.setIsEdit(false);
			model.addAttribute("product", dto);
			return "admin/products/addorEditProduct";
		}
		return "admin/products/addorEditProduct";
		
	}
	
	@GetMapping("edit/{productID}")
	public ModelAndView edit(ModelMap model, @PathVariable("productID") Long productID) {
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
		}
		Optional<Product> opt = productService.findById(productID);
		ProductDto dto = new ProductDto();
		
		if(opt.isPresent()) {
			Product entity = opt.get();
			// copy từ entity sang dto
			BeanUtils.copyProperties(entity, dto);
			
			dto.setCategoryID(entity.getCategory().getCategoryID());
			dto.setIsEdit(true);
			model.addAttribute("product", dto);
			
			return new ModelAndView("admin/products/addorEditProduct", model);
		}
		
		model.addAttribute("message", "Sản phẩm không tồn tại!!");
		
		return new ModelAndView("forward:/admin/products", model);
	}
	
	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	// đọc nội dung của file name được gửi tới sau đó trả nội dung của file name đó
	public ResponseEntity<Resource> serveFile(@PathVariable String filename){
		Resource file = storageService.loadAsResource(filename);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}
	
	@PostMapping("saveOrUpdateProduct")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("product") ProductDto dto, 
			BindingResult result) {
		if(session.getAttribute("username") != null) {
			Optional<Account> account = accountService.findById((String) session.getAttribute("username"));
			if(account.get().isAdmin() == true) {
				AdminLoginDto amdto = new AdminLoginDto();
				amdto.setIsAdmin(true);
				model.addAttribute("account", amdto);
			}else {
				AdminLoginDto amdto = new AdminLoginDto();
				amdto.setIsAdmin(false);
				model.addAttribute("account", amdto);
			}
			if(result.hasErrors()) {
				return new ModelAndView("admin/products/addorEditProduct");
			}
			Product entity = new Product();
			BeanUtils.copyProperties(dto, entity);
			
			// xử lý category
			Category category = new Category();
			category.setCategoryID(dto.getCategoryID());
			entity.setCategory(category);
//			xử lý ngày
			entity.setEnteredDate(new Date());
			
			//xử lý hình ảnh
			if(!dto.getImageFile().isEmpty()) {
				// sinh ra 1 dãy các ký tự để nhận dạng hình ảnh
				UUID uuid = UUID.randomUUID();
				String uuString = uuid.toString();
				
				entity.setImage(storageService.getStoredFilename(dto.getImageFile(), uuString));
				// xác định tên file đc lưu trữ
				storageService.store(dto.getImageFile(), entity.getImage());
			}
			productService.save(entity);
			
			if(dto.getIsEdit()) {
				model.addAttribute("message", "Sản phẩm đã được cập nhật!!!");
				return new ModelAndView("/admin/products/addorEditProduct", model);
			}
			
			model.addAttribute("message", "Sản phẩm đã được lưu!!!");
			
			return new ModelAndView("/admin/products/addorEditProduct", model);
		}
		return new ModelAndView("/admin/products/addorEditProduct", model);
		
	}
	
	@GetMapping("delete/{productID}")
	public ModelAndView delete(ModelMap model, @PathVariable("productID") Long productID) throws IOException {
		Optional<Product> opt = productService.findById(productID);
		
		if(opt.isPresent()) {
			if(!StringUtils.isEmpty(opt.get().getImage())) {
				storageService.delete(opt.get().getImage());
			}
			
			productService.delete(opt.get());
			
			model.addAttribute("message", "Sản phẩm đã được xóa!!");
		}else {
			model.addAttribute("message", "Sản phẩm không tìm thấy!!");
		}
		
		return new ModelAndView("forward:/admin/products", model);
	}
	
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
			return "admin/products/listProduct";
		}
		
		
		return "admin/products/listProduct";
	}
	
}
