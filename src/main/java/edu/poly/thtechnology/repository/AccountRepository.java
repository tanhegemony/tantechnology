package edu.poly.thtechnology.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.poly.thtechnology.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

}
