package deltaspike.security2;

import java.util.Set;

import org.apache.deltaspike.security.api.authorization.AccessDecisionVoter;
import org.apache.deltaspike.security.api.authorization.AccessDecisionVoterContext;
import org.apache.deltaspike.security.api.authorization.SecurityViolation;
import org.apache.deltaspike.security.api.authorization.annotation.Secured;
import org.apache.deltaspike.security.api.authorization.annotation.Secures;

import deltaspike.security.Admin;
import deltaspike.security.CustomSecurityBinding;
import deltaspike.security.CustomSecurityBinding2;

public class SecuredBean2 {

	@CustomSecurityBinding2
    @Admin
    public SecuredBean2 doSomething() {
    	return null;
    }


	@CustomSecurityBinding(2) 
    public SecuredBean2 doSomething1() {
    	return null;
    }

	@CustomSecurityBinding(4)
    public SecuredBean2 doSomething2() {
    	return null;
    }

	@CustomSecurityBinding(1)
    public SecuredBean2 doSomething3() {
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
