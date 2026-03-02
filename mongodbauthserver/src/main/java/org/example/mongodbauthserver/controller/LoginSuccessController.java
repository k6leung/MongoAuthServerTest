package org.example.mongodbauthserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginSuccessController {

    @GetMapping("/loginSuccess")
    public String loginSuccess(Authentication authentication, HttpServletRequest request) {
        HttpSession session = request.getSession();
        return authentication.getPrincipal().toString();
    }
}