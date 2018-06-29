package com.shinesolutions.aem.passwordreset;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ActivatorConfiguration {

    @AttributeDefinition(name = "pwdreset.authorizables", type = AttributeType.STRING)
    String[] pwdreset_authorizables() default {"admin"};
}
