package cdi.seam;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class Application {

	@Inject @Q1
	Event<Bean> event;
	
}
