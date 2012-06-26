package cdi.test;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Category;

@Named
@RequestScoped
public class Bean {

	@Inject @Category("trains") TrainSpotterLog log;
	
	public String getHelloWorld() {
		log.dieselTrainsSpotted(1);
		return "HelloWorld";
	}
}
