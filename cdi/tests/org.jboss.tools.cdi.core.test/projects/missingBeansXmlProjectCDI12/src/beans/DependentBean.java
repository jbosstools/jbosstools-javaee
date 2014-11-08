package beans;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Dependent
public class DependentBean<T> {
	
	@Produces T[] t = null;


	@Produces T[] getT() {
		return null;
	}

	@Produces
	String getString(T[] t) {
		return null;
	}

	@Inject
	T[] t2 = null;
}
