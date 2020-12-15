package ar.com.manflack.ada.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.com.manflack.ada.domain.Repository.AccountRepository;
import ar.com.manflack.ada.domain.model.Account;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping(path = "account")

public class AccountController {
	
	@Autowired
	AccountRepository AccountRepository; 
	
	@PostMapping(path = "")
	public ResponseEntity<Account> saveAccount(@RequestBody Account account) {
		Account response = AccountRepository.saveAndFlush(account);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
