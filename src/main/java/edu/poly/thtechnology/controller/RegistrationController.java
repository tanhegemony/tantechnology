package edu.poly.thtechnology.controller;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.model.AccountDto;
import edu.poly.thtechnology.service.AccountService;

@Controller
public class RegistrationController {
	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("adminRegistration")
	public String registration(Model model) {
		model.addAttribute("account", new AccountDto());
		return "/admin/accounts/registration";
	}
	
	@PostMapping("/adminRegistration")
	public ModelAndView saveAccount(ModelMap model,
			@Valid @ModelAttribute("account") AccountDto dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("/admin/accounts/registration");
		}
		Account account = accountService.registration(dto.getUsername());
		if(account == null) {
			if(accountService.duplicate(dto.getPassword(), dto.getConfirmPassword()) == false) {
				model.addAttribute("message", "Mật khẩu không trùng khớp!!");
				return new ModelAndView("/admin/accounts/registration");
			}
			Account entity = new Account();
			BeanUtils.copyProperties(dto, entity);
			accountService.save(entity);
			model.addAttribute("message", "Đăng ký tài khoản thành công!!");
		}else {
			model.addAttribute("message", "Tài khoản đã tồn tại!!");
		}
		return new ModelAndView("/admin/accounts/registration");
	}
	
}
