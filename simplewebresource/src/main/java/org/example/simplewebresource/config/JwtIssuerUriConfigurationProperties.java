package org.example.simplewebresource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt.issuer")
public class JwtIssuerUriConfigurationProperties {

    private List<String> uri = new ArrayList<>();
}
