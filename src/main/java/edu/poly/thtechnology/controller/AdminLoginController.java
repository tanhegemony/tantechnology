package edu.poly.thtechnology.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
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
import edu.poly.thtechnology.service.AccountService;

@Controller
public class AdminLoginController {
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("adminLogin")
	public String login(ModelMap model) {
		model.addAttribute("account", new AdminLoginDto());
		return "/admin/accounts/login";
	}
	
	@PostMapping("/adminLogin")
	public ModelAndView login(ModelMap model,
			@Valid @ModelAttribute("account") AdminLoginDto dto,
			BindingResult result) {
		if(result.hasErrors()) {
			return new ModelAndView("/admin/accounts/login", model);
		}
		
		Account account = accountService.login(dto.getUsername(), dto.getPassword());
		
		if(account == null) {
			model.addAttribute("message", "Tài khoản và mật khẩu không đúng!!Vui lòng kiểm tra lại");
			return new ModelAndView("/admin/accounts/login", model);
		}
		session.setAttribute("username", account.getUsername());
		
		Object ruri = session.getAttribute("redirect-uri");
//		nếu uri khác null thì sẽ chuyển hướng ts uri đó - có nghĩa là đang ở trang nào đó 
		// nhưng trang đó chưa đăng nhập , nếu đăng nhập r thì sẽ đứng ngay trang đó 
		if(ruri != null) {
			session.removeAttribute("redirect-uri");
			System.out.println(ruri);
			return new ModelAndView("redirect:" + ruri);
		}
		return new ModelAndView("forward:/site/home", model);
	}
	
	
}
