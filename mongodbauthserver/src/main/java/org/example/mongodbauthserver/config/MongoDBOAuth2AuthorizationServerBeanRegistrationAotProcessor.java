package org.example.mongodbauthserver.config;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.BindingReflectionHintsRegistrar;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationCode;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson.CoreJacksonModule;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenExchangeActor;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2TokenExchangeCompositeAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.jackson.OAuth2AuthorizationServerJacksonModule;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.jackson.WebJacksonModule;
import org.springframework.security.web.jackson.WebServletJacksonModule;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class MongoDBOAuth2AuthorizationServerBeanRegistrationAotProcessor  implements BeanRegistrationAotProcessor  {

    private static final boolean jackson3Present;

    static {
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        jackson3Present = ClassUtils.isPresent("tools.jackson.databind.json.JsonMapper", classLoader);
    }

    private boolean jacksonContributed;

    @Override
    public @Nullable BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
        if(!this.jacksonContributed) {
            JacksonConfigurationBeanRegistrationAotContribution jacksonContribution =
                    new JacksonConfigurationBeanRegistrationAotContribution();
            this.jacksonContributed = true;
            return jacksonContribution;
        }

        return null;
    }

    private static class JacksonConfigurationBeanRegistrationAotContribution
        implements BeanRegistrationAotContribution {

        private final BindingReflectionHintsRegistrar reflectionHintsRegistrar = new BindingReflectionHintsRegistrar();

        @Override
        public void applyTo(GenerationContext generationContext, BeanRegistrationCode beanRegistrationCode) {
            registerHints(generationContext.getRuntimeHints());
        }

        private void registerHints(RuntimeHints hints) {
            // Collections -> UnmodifiableSet, UnmodifiableList, UnmodifiableMap,
            // UnmodifiableRandomAccessList, etc.
            hints.reflection().registerType(Collections.class, MemberCategory.DECLARED_CLASSES);

            // HashSet
            hints.reflection()
                    .registerType(HashSet.class, MemberCategory.DECLARED_FIELDS,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

            hints.reflection()
                    .registerTypes(Arrays.asList(TypeReference.of(AbstractAuthenticationToken.class),
                                    TypeReference.of(DefaultSavedRequest.Builder.class),
                                    TypeReference.of(WebAuthenticationDetails.class),
                                    TypeReference.of(UsernamePasswordAuthenticationToken.class), TypeReference.of(User.class),
                                    TypeReference.of(DefaultOidcUser.class), TypeReference.of(DefaultOAuth2User.class),
                                    TypeReference.of(OidcUserAuthority.class), TypeReference.of(OAuth2UserAuthority.class),
                                    TypeReference.of(SimpleGrantedAuthority.class), TypeReference.of(OidcIdToken.class),
                                    TypeReference.of(AbstractOAuth2Token.class), TypeReference.of(OidcUserInfo.class),
                                    TypeReference.of(OAuth2TokenExchangeActor.class),
                                    TypeReference.of(OAuth2AuthorizationRequest.class),
                                    TypeReference.of(OAuth2TokenExchangeCompositeAuthenticationToken.class),
                                    TypeReference.of(AuthorizationGrantType.class),
                                    TypeReference.of(OAuth2AuthorizationResponseType.class),
                                    TypeReference.of(OAuth2TokenFormat.class), TypeReference.of(FactorGrantedAuthority.class)),
                            (builder) -> builder.withMembers(MemberCategory.DECLARED_FIELDS,
                                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS));

            // Jackson Modules
            if (jackson3Present) {
                hints.reflection()
                        .registerTypes(
                                Arrays.asList(TypeReference.of(CoreJacksonModule.class),
                                        TypeReference.of(WebJacksonModule.class),
                                        TypeReference.of(WebServletJacksonModule.class),
                                        TypeReference.of(OAuth2AuthorizationServerJacksonModule.class)),
                                (builder) -> builder.withMembers(MemberCategory.DECLARED_FIELDS,
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS));
            }

            // Jackson Mixins
            if (jackson3Present) {
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                        loadClass("org.springframework.security.web.jackson.DefaultSavedRequestMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                        loadClass("org.springframework.security.web.jackson.WebAuthenticationDetailsMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                        loadClass("org.springframework.security.jackson.UsernamePasswordAuthenticationTokenMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                        loadClass("org.springframework.security.jackson.UserMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                        loadClass("org.springframework.security.jackson.SimpleGrantedAuthorityMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                        "org.springframework.security.oauth2.server.authorization.jackson.OAuth2TokenExchangeActorMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                        "org.springframework.security.oauth2.server.authorization.jackson.OAuth2AuthorizationRequestMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                        "org.springframework.security.oauth2.server.authorization.jackson.OAuth2TokenExchangeCompositeAuthenticationTokenMixin"));
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                        "org.springframework.security.oauth2.server.authorization.jackson.OAuth2TokenFormatMixin"));
                // todo: to be removed
                this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                        "org.example.mongodbauthserver.jackson.FactorGrantedAuthorityMixin"));
            }

            // Check if OAuth2 Client is on classpath
            if (ClassUtils.isPresent("org.springframework.security.oauth2.client.registration.ClientRegistration",
                    ClassUtils.getDefaultClassLoader())) {

                hints.reflection()
                        .registerType(TypeReference
                                        .of("org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken"),
                                (builder) -> builder.withMembers(MemberCategory.DECLARED_FIELDS,
                                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                        MemberCategory.INVOKE_DECLARED_METHODS));

                // Jackson Module
                if (jackson3Present) {
                    hints.reflection()
                            .registerType(
                                    TypeReference
                                            .of("org.springframework.security.oauth2.client.jackson.OAuth2ClientJacksonModule"),
                                    (builder) -> builder.withMembers(MemberCategory.DECLARED_FIELDS,
                                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                            MemberCategory.INVOKE_DECLARED_METHODS));
                }

                // Jackson Mixins
                if (jackson3Present) {
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(), loadClass(
                            "org.springframework.security.oauth2.client.jackson.OAuth2AuthenticationTokenMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.DefaultOidcUserMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.DefaultOAuth2UserMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.OidcUserAuthorityMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.OAuth2UserAuthorityMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.OidcIdTokenMixin"));
                    this.reflectionHintsRegistrar.registerReflectionHints(hints.reflection(),
                            loadClass("org.springframework.security.oauth2.client.jackson.OidcUserInfoMixin"));
                }
            }
        }

        private static Class<?> loadClass(String className) {
            try {
                return Class.forName(className);
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
