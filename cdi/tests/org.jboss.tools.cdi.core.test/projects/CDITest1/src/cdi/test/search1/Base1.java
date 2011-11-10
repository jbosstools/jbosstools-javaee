package cdi.test.search1;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class Base1 implements BaseDecoratedInterface{

	public Base1() {
	}
	
	@Inject
	Event<Base1> event1;
}
