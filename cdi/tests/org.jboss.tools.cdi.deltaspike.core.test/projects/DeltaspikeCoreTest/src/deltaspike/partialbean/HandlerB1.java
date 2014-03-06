package deltaspike.partialbean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

@BindingB
@ApplicationScoped
public class HandlerB1 implements InvocationHandler {
 
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return null;
	}

}
