package edu.poly.thtechnology.controller.admin;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.model.AccountDto;
import edu.poly.thtechnology.model.AdminLoginDto;
import edu.poly.thtechnology.model.CategoryDto;
import edu.poly.thtechnology.service.AccountService;
import edu.poly.thtechnology.service.CategoryService;

@Controller
@RequestMapping("admin/accounts")
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	CategoryService categoryService;
	
	@ModelAttribute("categories")
	// trả về danh sách các category hiện có trong csdl để lựa chọn danh mục cho sản phẩm
	public List<CategoryDto> getCategories(){
		return categoryService.findAll().stream().map(item->{
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}
	
	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("account") AccountDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/accounts/registration");
		}
		Account entity = new Account();
		BeanUtils.copyProperties(dto, entity);
		accountService.save(entity);
		model.addAttribute("message", "Đăng ký tài khoản thành công!!");

		return new ModelAndView("admin/accounts/registration");
	}

	@RequestMapping("")
	public String list(ModelMap model) {
		List<Account> list = accountService.findAll();
		model.addAttribute("accounts", list);
		return "admin/listAccounts";
	}

	@GetMapping("edit/{username}")
	public ModelAndView edit(ModelMap model, @PathVariable("username") String username) {
		Optional<Account> opt = accountService.findById(username);

		AccountDto dto = new AccountDto();
		// nếu có dữ liệu id thì trả về (dùng isPresent)
		if (opt.isPresent()) {
			Account entity = opt.get();
			// copy từ entity sang dto
			BeanUtils.copyProperties(entity, dto);
			// pw là tt nhạy cảm nên k gửi kèm tt pw
			dto.setPassword("");

			model.addAttribute("account", dto);

			return new ModelAndView("admin/accounts/addorEditAccount", model);
		}
		model.addAttribute("message", "Tài khoản không tồn tại!!");

		return new ModelAndView("forward:/admin/accounts", model);
	}

	public ModelAndView delete(ModelMap model, @PathVariable("username") String username) {
		accountService.deleteById(username);
		model.addAttribute("message", "Tài khoản đã được xóa!!");

		return new ModelAndView("forward:/admin/accounts", model);
	}

}
