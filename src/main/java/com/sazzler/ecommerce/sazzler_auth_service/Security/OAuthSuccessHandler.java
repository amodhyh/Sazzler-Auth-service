package com.sazzler.ecommerce.sazzler_auth_service.Security;

import com.sazzler.ecommerce.sazzler_auth_service.Services.OAuthService;
import com.sazzler.ecommerce.util.JWTutil.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuthService oAuthService;
    private final JWTUtil jwtUtil;

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Autowired
    public OAuthSuccessHandler(OAuthService oAuthService, JWTUtil jwtUtil) {
        this.oAuthService = oAuthService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();

        String providerId;
        String email;
        String firstName;
        String lastName;

        if ("GOOGLE".equals(provider)) {
            providerId = oAuth2User.getAttribute("sub");
            email = oAuth2User.getAttribute("email");
            firstName = oAuth2User.getAttribute("given_name");
            lastName = oAuth2User.getAttribute("family_name");
        } else if ("GITHUB".equals(provider)) {
            Object idObj = oAuth2User.getAttribute("id");
            providerId = idObj != null ? String.valueOf(idObj) : null;
            email = oAuth2User.getAttribute("email");
            String fullName = oAuth2User.getAttribute("name");
            if (fullName != null && fullName.contains(" ")) {
                firstName = fullName.substring(0, fullName.indexOf(' '));
                lastName = fullName.substring(fullName.indexOf(' ') + 1);
            } else {
                firstName = fullName != null ? fullName : "";
                lastName = "";
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported OAuth provider: " + provider);
            return;
        }

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Email not provided by OAuth provider. Please grant email access and try again.");
            return;
        }

        SazzlerUserDetails userDetails = oAuthService.processOAuthUser(provider, providerId, email, firstName, lastName);

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String token = jwtUtil.generateToken(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                authorities
        );

        response.sendRedirect(redirectUri + "?token=" + token);
    }
}
