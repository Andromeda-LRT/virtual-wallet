package com.virtualwallet.model_helpers;

import com.virtualwallet.exceptions.AuthenticationFailureException;
import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.User;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import com.virtualwallet.services.contracts.UserService;
import org.springframework.web.server.ResponseStatusException;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

import java.io.UnsupportedEncodingException;

import static org.apache.tomcat.websocket.Constants.AUTHORIZATION_HEADER_NAME;

@Component
public class AuthenticationHelper {

    private final UserService service;


    @Autowired
    public AuthenticationHelper(UserService service) {
        this.service = service;
    }

    public User tryGetUser(HttpHeaders headers) {
        if (!headers.containsKey(AUTHORIZATION_HEADER_NAME)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    THE_REQUESTED_RESOURCE_REQUIRES_AUTHENTICATION);
        }
        try {
            String authorizationHeader = headers.getFirst(AUTHORIZATION_HEADER_NAME);
            if (authorizationHeader.contains("Basic ") &&
                    Base64.isBase64(authorizationHeader.substring("Basic ".length()))) {
                authorizationHeader = new String(Base64.decodeBase64(authorizationHeader
                        .substring("Basic ".length())), "UTF-8").replace(":", " ");
            }
            String username = getUsername(authorizationHeader);
            String password = getPassword(authorizationHeader);

            User user = service.getByUsername(username);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordMatch = encoder.matches(password, user.getPassword());
            if (!isPasswordMatch) {
                throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
            }

            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsername(String authorizationHeader) {
        int firstSpaceIndex = authorizationHeader.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION);
        }
        return authorizationHeader.substring(0, firstSpaceIndex);
    }

    private String getPassword(String authorizationHeader) {
        int firstSpaceIndex = authorizationHeader.indexOf(" ");
        if (firstSpaceIndex == -1) {
            throw new UnauthorizedOperationException(INVALID_AUTHENTICATION);
        }
        return authorizationHeader.substring(firstSpaceIndex + 1);
    }

    public User verifyAuthentication(String username, String password) {
        try {
            User user = service.getByUsername(username);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean isPasswordMatch = encoder.matches(password, user.getPassword());
            if (!isPasswordMatch) {
                throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
            }
            return user;
        } catch (EntityNotFoundException e) {
            throw new AuthenticationFailureException(WRONG_USERNAME_OR_PASSWORD);
        }
    }

    public User tryGetUser(HttpSession session) {
        String currentUsername = (String) session.getAttribute("currentUser");

        if (currentUsername == null) {
            throw new AuthenticationFailureException("Invalid authentication.");
        }

        return service.getByUsername(currentUsername);
    }
}
