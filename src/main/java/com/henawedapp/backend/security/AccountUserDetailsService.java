package com.henawedapp.backend.security;

import com.henawedapp.backend.model.entity.Account;
import com.henawedapp.backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Custom UserDetailsService để load user từ database.
 */
@Service
@RequiredArgsConstructor
public class AccountUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm account bằng email hoặc phone
        Account account = accountRepository.findByEmail(username)
                .or(() -> accountRepository.findByPhone(username))
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

        return new AccountUserDetails(account);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

        return new AccountUserDetails(account);
    }
}