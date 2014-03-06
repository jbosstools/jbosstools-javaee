package deltaspike.partialbean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@BindingB
public class HandlerB implements InvocationHandler {
 
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return null;
	}

}
