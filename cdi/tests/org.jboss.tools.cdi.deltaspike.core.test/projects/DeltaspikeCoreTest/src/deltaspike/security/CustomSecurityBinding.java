package deltaspike.security;

import org.apache.deltaspike.security.api.authorization.annotation.SecurityBindingType;

@SecurityBindingType
public @interface CustomSecurityBinding {
	int value() default 0;
}
