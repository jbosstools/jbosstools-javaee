package deltaspike.security;

import org.apache.deltaspike.security.api.authorization.annotation.Secures;

public class CustomAuthorizer {
	
	@Secures 
	@CustomSecurityBinding(4)
	public boolean check() {		
		return true;
	}

	@Secures  
	@CustomSecurityBinding(4)
	public boolean check1() {		
		return true;
	}

	@Secures  
	@CustomSecurityBinding(2)
	public boolean check2() {		
		return true;
	}

	@Secures 
	@CustomSecurityBinding2
	public void check3() {		
	}

	@Secures 
	public boolean check4() {
		return true;
	}

}
