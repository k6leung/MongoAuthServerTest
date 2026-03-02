package org.example.resourceserver.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.resourceserver.model.TestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Slf4j
@RestController
public class ArticlesController {

    @Autowired
    private RestClient restClient;

    @GetMapping("/books")
    public ResponseEntity<String> getBooks(Authentication authentication,
                                           @RequestHeader Map<String, Object> headers, @RequestParam Map<String, Object> reqeuestParams) { // authentication parameter is not necessary
        // The lines below are purely to demonstrate the existence of the jwt, you do not need them for your endpoints
        assert authentication instanceof JwtAuthenticationToken;
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String username = authentication.getName();
        String jwtString = jwtAuthenticationToken.getToken().getTokenValue();

        TestData simpleResourceResponse = restClient
                .get()
                .uri("/api/test")
                .attributes(clientRegistrationId("resource-server-client"))
                .retrieve()
                .body(TestData.class);

        return ResponseEntity.ok("Hi " + username +
                ", here are some books [book1, book2],  " +
                " also here is your jwt : " + jwtString +
                " simple resource response: " +  simpleResourceResponse);
        //return ResponseEntity.ok("ok");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/books/checkRoleAdmin")
    public String checkRoleAdmin(Authentication authentication) {
        return "has Role Admin: true";
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/books/checkRoleCustomer")
    public String checkRoleCustomer(Authentication authentication) {
        return "has Role Customer: true";
    }

    @PreAuthorize("hasAuthority('data:create')")
    @GetMapping("/books/checkAuthorityDataCreate")
    public String checkAuthorityDataCreate(Authentication authentication) {
        return "has authority data:create: true";
    }
}
