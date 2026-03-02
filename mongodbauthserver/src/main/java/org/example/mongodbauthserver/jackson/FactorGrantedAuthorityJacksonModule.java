package org.example.mongodbauthserver.jackson;

import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.jackson.SecurityJacksonModule;
import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

// todo: to be removed
public class FactorGrantedAuthorityJacksonModule extends SecurityJacksonModule {

    public FactorGrantedAuthorityJacksonModule() {
        super(FactorGrantedAuthorityJacksonModule.class.getName(), new Version(1, 0, 0, (String)null, (String)null, (String)null));
    }

    @Override
    public void configurePolymorphicTypeValidator(BasicPolymorphicTypeValidator.Builder builder) {
        builder.allowIfSubType(FactorGrantedAuthority.class);
    }

    public void setupModule(JacksonModule.SetupContext context) {
        context.setMixIn(FactorGrantedAuthority.class, FactorGrantedAuthorityMixin.class);
    }
}
