package edu.poly.thtechnology.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.ModelAndView;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.domain.Category;
import edu.poly.thtechnology.model.AdminLoginDto;
import edu.poly.thtechnology.model.CategoryDto;
import edu.poly.thtechnology.service.AccountService;
import edu.poly.thtechnology.service.CategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	AccountService accountService;
	
	@GetMapping("addCategory")
	public String add(Model model) {
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
			model.addAttribute("category", new CategoryDto());
			return "admin/categories/addorEditCategory";
		}
		return "admin/categories/addorEditCategory";
		
	}
	
	@GetMapping("edit/{categoryID}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryID") Long categoryID) {
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
			Optional<Category> opt = categoryService.findById(categoryID);
			CategoryDto dto = new CategoryDto();
			
			if(opt.isPresent()) {
				Category entity = opt.get();
				// copy t??? entity sang dto
				BeanUtils.copyProperties(entity, dto);
				dto.setIsEdit(true);
				model.addAttribute("category", dto);
				
				return new ModelAndView("admin/categories/addorEditCategory", model);
			}
			
			model.addAttribute("message", "Danh m???c kh??ng t???n t???i!!");
			
			return new ModelAndView("forward:/admin/categories", model);
		}
		return new ModelAndView("forward:/admin/categories", model);
	}
	
	@PostMapping("saveOrUpdateCategory")
	public ModelAndView saveOrUpdate(ModelMap model, 
			@Valid @ModelAttribute("category") CategoryDto dto, 
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
				return new ModelAndView("admin/categories/addorEditCategory");
			}
			Category entity = new Category();
			BeanUtils.copyProperties(dto, entity);
			
			categoryService.save(entity);
			
			if(dto.getIsEdit()) {
				model.addAttribute("message", "Danh m???c ???? ???????c c???p nh???t!!!");
				return new ModelAndView("admin/categories/addorEditCategory", model);
			}
			
			model.addAttribute("message", "Danh m???c ???? ???????c l??u!!!");
			
			return new ModelAndView("admin/categories/addorEditCategory", model);
		}
		return new ModelAndView("admin/categories/addorEditCategory", model);
		
	}
	
	@GetMapping("delete/{categoryID}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryID") Long categoryID) {
		categoryService.deleteById(categoryID);
		
		model.addAttribute("message", "Danh m???c ???? ???????c x??a!!");
		
		return new ModelAndView("forward:/admin/categories", model);
	}
	
	@ModelAttribute("categories")
	// tr??? v??? danh s??ch c??c category hi???n c?? trong csdl ????? l???a ch???n danh m???c cho s???n ph???m
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@RequestMapping("")
	public String searchCategory(ModelMap model
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
			Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("categoryID"));
			Page<Category> resultPage = null;
			
			// n???u name truy???n v??? c?? gi?? tr???
			if(StringUtils.hasText(name)) {
				// tr??? v??? danh s??ch c??c category c?? t??n l?? name
				resultPage = categoryService.findByNameContaining(name, pageable);
				model.addAttribute("name", name);
			}else {
				// tr??? v??? t???t c??? c??c category
				resultPage = categoryService.findAll(pageable);
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
			model.addAttribute("categoryPage", resultPage);
			return "admin/categories/listCategory";
		}
		return "admin/categories/listCategory";
	}
	
	
}
