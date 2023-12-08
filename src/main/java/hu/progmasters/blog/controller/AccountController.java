package hu.progmasters.blog.controller;

import hu.progmasters.blog.dto.account.AccountEditReq;
import hu.progmasters.blog.dto.account.PremiumUpdateReq;
import hu.progmasters.blog.dto.account.SubscriptionReq;
import hu.progmasters.blog.dto.account.UserInfo;
import hu.progmasters.blog.dto.security.RegistrationReq;
import hu.progmasters.blog.dto.security.RegistrationInfo;
import hu.progmasters.blog.dto.security.NewPasswordReq;
import hu.progmasters.blog.dto.security.ResetPasswordWithEmailReq;
import hu.progmasters.blog.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/registration")
    public ResponseEntity<RegistrationInfo> registerAccount(@Valid @RequestBody RegistrationReq registrationReq) {
        RegistrationInfo registrationInfo = accountService.registerAccount(registrationReq);
        log.info("Http request, POST /api/user/registration New account added");
        return new ResponseEntity(registrationInfo, HttpStatus.CREATED);
    }
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping("/request")
    public ResponseEntity<RegistrationInfo> requestPasswordReset(@Valid @RequestBody ResetPasswordWithEmailReq withEmailFromData) throws AccountNotFoundException {
       log.info(withEmailFromData.getEmail() + "---------------------------------");
        RegistrationInfo registrationInfo = accountService.resetPasswordRequest(withEmailFromData.getEmail());
        log.info("Http request, POST /api/user/request Password reset request");
        return new ResponseEntity<>(registrationInfo, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PostMapping("/reset")
    public ResponseEntity<RegistrationInfo> resetPassword(@RequestParam String token, @Valid @RequestBody NewPasswordReq data) throws AccountNotFoundException {
        RegistrationInfo registrationInfo = accountService.resetPassword(token, data);
        log.info("Http request, POST /api/user/reset Password reset completed");
        return new ResponseEntity<>(registrationInfo, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserInfo> getUserProfile(@PathVariable("userId") Long id) {
        UserInfo userInfo = accountService.getUserInfo(id);
        log.info("Http request, GET /api/user/profile/{userId}" + id + " Profil find by id");
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') and@customPermissionEvaluator.isOwnAccountById(authentication, #id) or " +
            "(hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnAccountById(authentication, #id)) or " +
            "(hasRole('ROLE_USER') and @customPermissionEvaluator.isOwnAccountById(authentication, #id))")
    @PutMapping("/profile/update/{userId}")
    public ResponseEntity updateAccountInfo(@PathVariable("userId") Long id,
                                           @RequestBody AccountEditReq accountEditReq) {
        accountService.editAccount(id, accountEditReq);
        log.info("Http request, PUT /api/user/update/{userId}" + id + " Profil update by id");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') and@customPermissionEvaluator.isOwnAccountById(authentication, #id) or " +
            "(hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnAccountById(authentication, #id)) or " +
            "(hasRole('ROLE_USER') and @customPermissionEvaluator.isOwnAccountById(authentication, #id))")
    @PutMapping("/profile/newsletter/{userId}")
    public ResponseEntity subscribeNewsletter(@PathVariable("userId") Long id,
                                              @Valid @RequestBody SubscriptionReq subscriptionReq) {
        accountService.editSubscription(id, subscriptionReq);
        log.info("Http request, PUT /api/user/profile/newsletter/{userId}" + id + " Profil newsLetter added");
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') and@customPermissionEvaluator.isOwnAccountById(authentication, #id) or " +
            "(hasRole('ROLE_AUTHOR') and @customPermissionEvaluator.isOwnAccountById(authentication, #id)) or " +
            "(hasRole('ROLE_USER') and @customPermissionEvaluator.isOwnAccountById(authentication, #id))")
    @PutMapping("/profile/premium/{userId}")
    public ResponseEntity updatePremium(@PathVariable("userId") Long id,
                                        @Valid @RequestBody PremiumUpdateReq premiumUpdateReq) {
        accountService.editPremium(id, premiumUpdateReq);
        log.info("Http request, PUT /api/user/profile/premium/{userId}" + id + " Profil premium update");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
