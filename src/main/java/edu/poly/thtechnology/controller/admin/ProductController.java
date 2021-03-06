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
	// tr??? v??? danh s??ch c??c category hi???n c?? trong csdl ????? l???a ch???n danh m???c cho s???n ph???m
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
			// copy t??? entity sang dto
			BeanUtils.copyProperties(entity, dto);
			
			dto.setCategoryID(entity.getCategory().getCategoryID());
			dto.setIsEdit(true);
			model.addAttribute("product", dto);
			
			return new ModelAndView("admin/products/addorEditProduct", model);
		}
		
		model.addAttribute("message", "S???n ph???m kh??ng t???n t???i!!");
		
		return new ModelAndView("forward:/admin/products", model);
	}
	
	@GetMapping("/images/{filename:.+}")
	@ResponseBody
	// ?????c n???i dung c???a file name ???????c g???i t???i sau ???? tr??? n???i dung c???a file name ????
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
			
			// x??? l?? category
			Category category = new Category();
			category.setCategoryID(dto.getCategoryID());
			entity.setCategory(category);
//			x??? l?? ng??y
			entity.setEnteredDate(new Date());
			
			//x??? l?? h??nh ???nh
			if(!dto.getImageFile().isEmpty()) {
				// sinh ra 1 d??y c??c k?? t??? ????? nh???n d???ng h??nh ???nh
				UUID uuid = UUID.randomUUID();
				String uuString = uuid.toString();
				
				entity.setImage(storageService.getStoredFilename(dto.getImageFile(), uuString));
				// x??c ?????nh t??n file ??c l??u tr???
				storageService.store(dto.getImageFile(), entity.getImage());
			}
			productService.save(entity);
			
			if(dto.getIsEdit()) {
				model.addAttribute("message", "S???n ph???m ???? ???????c c???p nh???t!!!");
				return new ModelAndView("/admin/products/addorEditProduct", model);
			}
			
			model.addAttribute("message", "S???n ph???m ???? ???????c l??u!!!");
			
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
			
			model.addAttribute("message", "S???n ph???m ???? ???????c x??a!!");
		}else {
			model.addAttribute("message", "S???n ph???m kh??ng t??m th???y!!");
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
			// m???c ?????nh l?? 1 trang
			int currentPage = page.orElse(1);
			// m???c ?????nh m???i trang c?? 5 ph???n t???
			int pageSize = size.orElse(5);
			
			// t???o ?????i t?????ng pageable g???m trang hi???n t???i, s??? ph???n t??? 1 trang v?? s???p x???p theo name
			Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("productID"));
			Page<Product> resultPage = null;
			
			// n???u name truy???n v??? c?? gi?? tr???
			if(StringUtils.hasText(name)) {
				// tr??? v??? danh s??ch c??c product c?? t??n l?? name
				resultPage = productService.findByNameContaining(name, pageable);
				model.addAttribute("name", name);
			}else {
				// tr??? v??? t???t c??? c??c product
				resultPage = productService.findAll(pageable);
			}
			
			// tr??? v??? t???ng s??? c??c trang
			int totalPages = resultPage.getTotalPages();
			
			if(totalPages>0) {
				// kh??ng th??? l?? s??? ??m
				int start = Math.max(1, currentPage-2);
				// kh??ng v?????t qu?? t???ng s??? trang
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
