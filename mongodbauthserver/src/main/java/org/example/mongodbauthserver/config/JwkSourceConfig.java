package org.example.mongodbauthserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class JwkSourceConfig {

    @Value("${keystore.alias}")
    private String keystoreAlias;
    @Value("${keystore.password}")
    private String keystorePassword;
    @Value("${keystore.path}")
    private String keystorePath;

    @Autowired
    private ResourceLoader resourceLoader;

    private KeyPair readKeyPairFromEnv() {
        Path currentRelativePath = Paths.get("");
        String currentWorkingDirPath = currentRelativePath.toAbsolutePath().toString();

        log.info("Working Directory: {}", currentWorkingDirPath);
        Resource keystoreFileResource = resourceLoader.getResource("file:" + this.keystorePath);
        KeyStoreKeyFactory ksFactory =
                new KeyStoreKeyFactory(keystoreFileResource, keystorePassword.toCharArray());

        return ksFactory.getKeyPair(keystoreAlias);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = readKeyPairFromEnv();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keystoreAlias)
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
}
