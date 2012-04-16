package demo;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

public class ABCProducer {
	@Produces
	@Named("abc")
	String s;
	
	public ABCProducer() {
	}

}
