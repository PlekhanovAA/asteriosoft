package com.asteriosoft.security.config;

import com.asteriosoft.utils.TokenHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Date;

public class AuthenticationSuccessCustomHandler implements AuthenticationSuccessHandler {

    @Autowired
    TokenHelper tokenHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Logger logger = LoggerFactory.getLogger(AuthenticationSuccessCustomHandler.class);

        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        String token = tokenHelper.generateToken(user, now);
        tokenHelper.addAuthorityEntryLog(user.getUsername(), token, now);
        logger.info("Authentication for ={}= success", user.getUsername());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(HttpHeaders.AUTHORIZATION, token);
    }
}
