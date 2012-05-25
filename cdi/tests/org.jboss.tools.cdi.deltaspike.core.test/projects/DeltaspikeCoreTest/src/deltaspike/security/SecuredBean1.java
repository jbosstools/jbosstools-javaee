package deltaspike.security;

import java.util.Set;

import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;
import org.apache.deltaspike.security.api.authorization.annotation.Secured;

public class SecuredBean1 {

	@CustomSecurityBinding2
    @Admin
    public SecuredBean1 doSomething() {
    	return null;
    }


	@CustomSecurityBinding(2)
    public SecuredBean1 doSomething1() {
    	return null;
    }

	@CustomSecurityBinding(4)
    public SecuredBean1 doSomething2() {
    	return null;
    }

	@CustomSecurityBinding(1)
    public SecuredBean1 doSomething3() {
    	return null;
    }

	@Secured(A.class)
    public void a() {    	
    }
}

class A implements AccessDecisionVoter {

	@Override
	public Set<SecurityViolation> checkPermission(
			AccessDecisionVoterContext accessDecisionVoterContext) {
		return null;
	}
	
}
