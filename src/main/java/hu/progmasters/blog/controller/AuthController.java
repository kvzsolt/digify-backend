package hu.progmasters.blog.controller;

import hu.progmasters.blog.security.JwtTokenUtil;
import hu.progmasters.blog.dto.security.AuthenticationErrorRes;
import hu.progmasters.blog.dto.security.LoginReq;
import hu.progmasters.blog.dto.security.JwtToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static hu.progmasters.blog.controller.constants.Endpoints.AUTH_MAPPING;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(AUTH_MAPPING)
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginData) {
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword())

            );
            User user = (User) authenticate.getPrincipal();
            JwtToken jwtToken = new JwtToken(jwtTokenUtil.generateToken(user));
            log.info("Http request, POST /api/user/login User login");
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);

        } catch (BadCredentialsException ex) {
            AuthenticationErrorRes error = new AuthenticationErrorRes("Authentication Failed");
            log.error("Http request, POST /api/user/login User login failed");
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }


}