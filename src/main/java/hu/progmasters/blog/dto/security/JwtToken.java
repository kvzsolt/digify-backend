package hu.progmasters.blog.dto.security;

import lombok.Value;

@Value
public class JwtToken {
    String token;
}