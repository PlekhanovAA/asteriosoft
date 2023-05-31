package com.asteriosoft.security.config;

import com.asteriosoft.utils.TokenHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.util.Optional;

public class LogoutCustomHandler implements LogoutHandler {
    Logger logger = LoggerFactory.getLogger(LogoutCustomHandler.class);

    @Autowired
    TokenHelper tokenHelper;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (authHeader.isPresent() && tokenHelper.validateToken(authHeader.get())) {
            String userName = tokenHelper.getTokenBody(authHeader.get()).getSubject();
            tokenHelper.expiryTokenForUserName(userName);
            logger.info("Logout for ={}= success", userName);
        }
    }
}
