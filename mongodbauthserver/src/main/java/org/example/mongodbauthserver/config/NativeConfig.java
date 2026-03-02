package org.example.mongodbauthserver.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.time.Instant;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(value = {RedisSessionHint.class, SpringSecurityHint.class})
@RegisterReflectionForBinding(value = {Instant.class})
//@RegisterReflection(classes = {FactorGrantedAuthority.class}, memberCategories = {MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.ACCESS_DECLARED_FIELDS})
public class NativeConfig {

}

