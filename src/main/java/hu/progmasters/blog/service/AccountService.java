package hu.progmasters.blog.service;

import hu.progmasters.blog.dto.account.AccountEditReq;
import hu.progmasters.blog.dto.account.PremiumUpdateReq;
import hu.progmasters.blog.dto.account.SubscriptionReq;
import hu.progmasters.blog.dto.account.UserInfo;
import hu.progmasters.blog.dto.security.NewPasswordReq;
import hu.progmasters.blog.dto.security.RegistrationReq;
import hu.progmasters.blog.dto.security.interfaces.PasswordConfirmable;
import hu.progmasters.blog.exception.account.AccountNotFoundException;
import hu.progmasters.blog.exception.account.PasswordMismatchException;
import hu.progmasters.blog.repository.PostRepository;
import hu.progmasters.blog.security.JwtTokenUtil;
import hu.progmasters.blog.domain.Account;
import hu.progmasters.blog.domain.Post;
import hu.progmasters.blog.domain.enums.AccountRole;
import hu.progmasters.blog.dto.post.GetPostsShortenedRes;
import hu.progmasters.blog.dto.security.RegistrationInfo;
import hu.progmasters.blog.exception.account.FieldNotAvailableException;
import hu.progmasters.blog.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final ModelMapper modelMapper;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final PostRepository postRepository;

    private final EmailService emailService;

    private final JwtTokenUtil jwtTokenUtil;


    public RegistrationInfo registerAccount(RegistrationReq data) {
        confirmPassword(data);
        checkFieldAvailability(data);
        Account account = modelMapper.map(data, Account.class);
        account.setPassword(passwordEncoder.encode(data.getPassword()));
        account.setRole(AccountRole.ROLE_USER);
        account.setPremium(false);
        account.setNewsletter(false);
        accountRepository.save(account);
        return new RegistrationInfo("Successful registration.");
    }

    public UserInfo getUserInfo(Long id) {
        Account userAccount = findAccountById(id);
        UserInfo userInfo = modelMapper.map(userAccount, UserInfo.class);
        List<Post> postsByUser = postRepository.findPostsByAccount_Id(userInfo.getId());
        List<GetPostsShortenedRes> shortenedPosts = postsByUser.stream()
                .map(post -> modelMapper.map(post, GetPostsShortenedRes.class))
                .collect(Collectors.toList());
        userInfo.setWrittenPosts(shortenedPosts);
        return userInfo;
    }

    public void editAccount(Long id, AccountEditReq accountEditReq) {
        Account editedAccount = findAccountById(id);
        modelMapper.map(accountEditReq, editedAccount);
        accountRepository.save(editedAccount);
    }

    public void editSubscription(Long id, SubscriptionReq subscriptionReq) {
        Account editedAccount = findAccountById(id);
        modelMapper.map(subscriptionReq, editedAccount);
        accountRepository.save(editedAccount);
    }

    public void editPremium(Long id, PremiumUpdateReq premiumUpdateReq) {
        Account editedAccount = findAccountById(id);
        modelMapper.map(premiumUpdateReq, editedAccount);
        accountRepository.save(editedAccount);
    }

    private void checkFieldAvailability(RegistrationReq data) {
        if (accountRepository.existsByUsername(data.getUsername())) {
            throw new FieldNotAvailableException("Account with " + data.getUsername() + " already exist.");
        }
        if (accountRepository.existsByEmail(data.getEmail())) {
            throw new FieldNotAvailableException("Account with " + data.getEmail() + " already exist.");
        }
    }
    private <T extends PasswordConfirmable> void confirmPassword(T data) {
        if (!data.getPassword().equals(data.getPasswordConfirm())) {
            throw new PasswordMismatchException();
        }
    }

    public Account findAccountById(Long id) {
        return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(AccountNotFoundException::new);
    }

    public RegistrationInfo resetPasswordRequest(String email) throws javax.security.auth.login.AccountNotFoundException {
        Account account = findByEmail(email);
        if (account == null) {
            throw new javax.security.auth.login.AccountNotFoundException("Account The specified email address cannot be found: " + email);
        }

        String token = jwtTokenUtil.generatePasswordResetToken(account);

        String resetLink = "http://localhost:8080/api/public/user/reset?token=" + token;
        emailService.sendPasswordResetEmail(email, account.getUsername(), resetLink);
        return new RegistrationInfo("A jelszó visszaállítási linket elküldtük az e-mail címére.");
    }

    public RegistrationInfo resetPassword(String token, NewPasswordReq data) throws javax.security.auth.login.AccountNotFoundException {
        if (!jwtTokenUtil.validateResetToken(token)) {
            log.error("Token is expired");
        }
        confirmPassword(data);
        String email = jwtTokenUtil.getEmailFromPasswordResetToken(token);
        Account account = findByEmail(email);
        if (account == null) {
            throw new javax.security.auth.login.AccountNotFoundException("Account The specified email address cannot be found: a token mókolva lett");
        }
        account.setPassword(passwordEncoder.encode(data.getPassword()));
        accountRepository.save(account);
        emailService.sendPasswordResetSuccessEmail(account.getEmail(), account.getRealName());
        return new RegistrationInfo("A jelszó sikeresen frissítve");
    }

    public void setPremium(Account account) {
        account.setPremium(true);
        accountRepository.save(account);
    }
}
