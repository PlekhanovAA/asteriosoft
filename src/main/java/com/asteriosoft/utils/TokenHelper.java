package com.asteriosoft.utils;

import com.asteriosoft.security.entities.AuthorityLog;
import com.asteriosoft.repository.AuthorityLogRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class TokenHelper {
    Logger logger = LoggerFactory.getLogger(TokenHelper.class);

    @Value("${app.token.secret}")
    private String secret;

    @Value("${app.token.expirationMs}")
    private int expirationMs;

    @Autowired
    AuthorityLogRepository authorityLogRepository;

    public String generateToken(User user, Date createdAt) {
        logger.info("Generate token for: {}, now: {}", user.getUsername(), createdAt);
        return Jwts
                .builder()
                .setSubject(user.getUsername())
                .setIssuedAt(createdAt)
                .setExpiration(new Date(createdAt.getTime() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes()).compact();
    }

    public void addAuthorityEntryLog(String userName, String token, Date createdAt) {
        AuthorityLog authorityLog = AuthorityLog.builder().
                username(userName).
                token(token).
                createdAt(createdAt).
                expired(false).
                expiredAt(null).
                build();

        authorityLogRepository.save(authorityLog);
    }

    public Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {
        boolean result = false;
        try {
            result = inDataBaseValidateToken(token, getTokenBody(token).getSubject());
        } catch (SignatureException e) {
            logger.error("Invalid token signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Token claims string is empty: {}", e.getMessage());
        }

        return result;
    }

    private boolean inDataBaseValidateToken(String token, String userNameFromToken) {
        Optional<AuthorityLog> authorityLogOptional = Optional.ofNullable(
                authorityLogRepository.findTopByUsernameAndExpiredFalseOrderByCreatedAtDesc(userNameFromToken));
        return authorityLogOptional.filter(authorityLog -> token.equals(authorityLog.getToken())).isPresent();
    }

    public void expiryTokenForUserName(String userName) {
        Optional<AuthorityLog> authorityLogOptional = Optional.ofNullable(
                authorityLogRepository.findTopByUsernameAndExpiredFalseOrderByCreatedAtDesc(userName));
        authorityLogOptional.ifPresent(authorityLog -> {
            authorityLog.setExpired(true);
            authorityLog.setExpiredAt(new Date());
            authorityLogRepository.save(authorityLog);
        });
    }

}
