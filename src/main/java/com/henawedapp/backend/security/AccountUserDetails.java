package com.henawedapp.backend.security;

import com.henawedapp.backend.model.entity.Account;
import com.henawedapp.backend.model.entity.AccountRoleMap;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation cho Account entity.
 */
@Getter
public class AccountUserDetails implements UserDetails {

    private final UUID accountId;
    private final String email;
    private final String phone;
    private final String password;
    private final Boolean isActivated;
    private final Set<GrantedAuthority> authorities;
    private final AccountStatusWrapper accountStatus;

    public enum AccountStatusWrapper {
        ACTIVE, LOCKED, NOT_ACTIVATED, DELETED
    }

    public AccountUserDetails(Account account) {
        this.accountId = account.getId();
        this.email = account.getEmail();
        this.phone = account.getPhone();
        this.password = account.getPasswordHash();
        this.isActivated = account.getIsActivated();

        // Map roles to authorities
        this.authorities = account.getAccountRoleMaps().stream()
                .map(AccountRoleMap::getRole)
                .filter(role -> Boolean.TRUE.equals(role.getIsActive()))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCode()))
                .collect(Collectors.toSet());

        // Determine account status
        this.accountStatus = determineAccountStatus(account);
    }

    private AccountStatusWrapper determineAccountStatus(Account account) {
        if (!account.getIsActivated()) {
            return AccountStatusWrapper.NOT_ACTIVATED;
        }

        return switch (account.getStatus()) {
            case ACTIVE -> AccountStatusWrapper.ACTIVE;
            case SUSPENDED, BANNED, DELETED -> AccountStatusWrapper.LOCKED;
            case PENDING, PENDING_APPROVAL -> AccountStatusWrapper.NOT_ACTIVATED;
        };
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email != null ? email : phone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountStatus != AccountStatusWrapper.LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return accountStatus == AccountStatusWrapper.ACTIVE;
    }

    public boolean isActivated() {
        return isActivated != null && isActivated;
    }
}