package cdi;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@IBinding
@Interceptor
public class I1 extends AbstractManager {

	public I1() {
		// TODO Auto-generated constructor stub
	}

	@AroundInvoke
	public Object manage(InvocationContext ic) throws Exception {
		return null;
	}
	
}
