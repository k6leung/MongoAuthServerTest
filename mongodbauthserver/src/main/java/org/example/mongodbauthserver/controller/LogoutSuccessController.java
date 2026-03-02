package org.example.mongodbauthserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogoutSuccessController {

    @GetMapping("/logoutSuccess")
    public String logoutSuccess() {
        return "logout complete";
    }
}
