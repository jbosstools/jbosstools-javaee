package deltaspike.partialbean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import javax.enterprise.context.Dependent;

@Dependent
@BindingB
public class HandlerB implements InvocationHandler {
 
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return null;
	}

}
