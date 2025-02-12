package com.gentlecorp.customer.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtToUserDetailsConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtUserDetailsService jwtUserDetailsService;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        CustomUserDetails userDetails = (CustomUserDetails) jwtUserDetailsService.loadUserDetailsFromJwt(jwt);
        return new CustomAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
    }
}
