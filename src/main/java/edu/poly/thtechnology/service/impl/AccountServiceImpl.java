package edu.poly.thtechnology.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import edu.poly.thtechnology.domain.Account;
import edu.poly.thtechnology.repository.AccountRepository;
import edu.poly.thtechnology.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService{
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	public Account login(String username, String password) {
		Optional<Account> optExist = findById(username);
//		matches thực hiện so sánh trường pw vs giá trị pw đã đc mã hóa trong csdl
		if(optExist.isPresent() && bCryptPasswordEncoder.matches(password, optExist.get().getPassword())) {
//			xóa trắng pw
			optExist.get().getPassword();
			return optExist.get();
		}
		return null;
	}
//	Đăng ký tài khoản
	@Override
	public Account registration(String username) {
		Optional<Account> optExist = findById(username);
		if(optExist.isPresent()) {
			return optExist.get();
		}
		return null;
	}
//	COnfirm password
	@Override
	public boolean duplicate(String password, String confirmPassword) {
		if(password.equals(confirmPassword)) {
			return true;
		}
		return false;
	}

	@Override
	public <S extends Account> S save(S entity) {
		Optional<Account> optExist = findById(entity.getUsername());
		if(optExist.isPresent()) {
//			nếu người dùng k nhập pw mới thì lấy thông tin pw cũ
			if(StringUtils.isEmpty(entity.getPassword())) {
				entity.setPassword(optExist.get().getPassword());
			}else {
				entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
			}
		}else {
			entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
		}
		return accountRepository.save(entity);
	}


	@Override
	public <S extends Account> Optional<S> findOne(Example<S> example) {
		return accountRepository.findOne(example);
	}


	@Override
	public Page<Account> findAll(Pageable pageable) {
		return accountRepository.findAll(pageable);
	}


	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
	}


	@Override
	public List<Account> findAll(Sort sort) {
		return accountRepository.findAll(sort);
	}


	@Override
	public List<Account> findAllById(Iterable<String> ids) {
		return accountRepository.findAllById(ids);
	}


	@Override
	public Optional<Account> findById(String id) {
		return accountRepository.findById(id);
	}


	@Override
	public <S extends Account> List<S> saveAll(Iterable<S> entities) {
		return accountRepository.saveAll(entities);
	}


	@Override
	public void flush() {
		accountRepository.flush();
	}


	@Override
	public <S extends Account> S saveAndFlush(S entity) {
		return accountRepository.saveAndFlush(entity);
	}


	@Override
	public boolean existsById(String id) {
		return accountRepository.existsById(id);
	}


	@Override
	public <S extends Account> List<S> saveAllAndFlush(Iterable<S> entities) {
		return accountRepository.saveAllAndFlush(entities);
	}


	@Override
	public <S extends Account> Page<S> findAll(Example<S> example, Pageable pageable) {
		return accountRepository.findAll(example, pageable);
	}


	@Override
	public <S extends Account> long count(Example<S> example) {
		return accountRepository.count(example);
	}


	@Override
	public <S extends Account> boolean exists(Example<S> example) {
		return accountRepository.exists(example);
	}


	@Override
	public void deleteAllInBatch(Iterable<Account> entities) {
		accountRepository.deleteAllInBatch(entities);
	}


	@Override
	public long count() {
		return accountRepository.count();
	}


	@Override
	public void deleteById(String id) {
		accountRepository.deleteById(id);
	}


	@Override
	public void deleteAllByIdInBatch(Iterable<String> ids) {
		accountRepository.deleteAllByIdInBatch(ids);
	}


	@Override
	public void delete(Account entity) {
		accountRepository.delete(entity);
	}


	@Override
	public void deleteAllById(Iterable<? extends String> ids) {
		accountRepository.deleteAllById(ids);
	}


	@Override
	public void deleteAllInBatch() {
		accountRepository.deleteAllInBatch();
	}


	@Override
	public void deleteAll(Iterable<? extends Account> entities) {
		accountRepository.deleteAll(entities);
	}


	@Override
	public void deleteAll() {
		accountRepository.deleteAll();
	}


	@Override
	public Account getById(String id) {
		return accountRepository.getById(id);
	}


	@Override
	public <S extends Account> List<S> findAll(Example<S> example) {
		return accountRepository.findAll(example);
	}


	@Override
	public <S extends Account> List<S> findAll(Example<S> example, Sort sort) {
		return accountRepository.findAll(example, sort);
	}
	
	
}
