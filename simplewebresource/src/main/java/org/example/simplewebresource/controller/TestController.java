package org.example.simplewebresource.controller;

import org.example.simplewebresource.model.TestData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;


import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private Map<String, Map<String, Object>> mergeMaps(Map<String, Object> headers, Map<String, Object> body) {
        HashMap<String, Map<String, Object>> result = new HashMap<>();
        result.put("headers", headers);
        result.put("body", body);

        return result;
    }

    @GetMapping("/api/simple")
    public Map<String, Map<String, Object>> simple(@RequestHeader Map<String, Object> headers, @RequestParam Map<String, Object> reqeuestParams) {
        return mergeMaps(headers, reqeuestParams);
    }

    @GetMapping("/api/test")
    public TestData test(Authentication authentication) {
        return new TestData("test");
    }
}
