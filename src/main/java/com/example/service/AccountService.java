package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account UserAdd(Account account) {
        if (account.getUsername() == null || account.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be blank");
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 4 characters long");
        }
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account with this username already exists");
        }
    
        return accountRepository.save(account);
    }    

    public Account UserLoggedin(Account account) {
        Account foundAccount = accountRepository.findByUsernameAndPassword(account.getUsername(), account.getPassword());
        if (foundAccount == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return foundAccount;
    }
}
