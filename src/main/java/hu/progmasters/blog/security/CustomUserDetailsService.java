package hu.progmasters.blog.security;

import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.repository.AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;
    public CustomUserDetailsService(AccountRepository userRepository) {
        this.accountRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        Account account = accountOptional.orElseThrow(() -> new UsernameNotFoundException("Account with " + username + " username not found."));
        GrantedAuthority authority = new SimpleGrantedAuthority(account.getRole().name());

        return new User(account.getUsername(), account.getPassword(), Arrays.asList(authority));
    }
    public boolean isUserPremium(String username){
        Optional<Account> accountOptional = accountRepository.findByUsername(username);
        Account account = accountOptional.orElseThrow(() -> new UsernameNotFoundException("Account with " + username + " username not found."));
        return account.isPremium();

    }
}