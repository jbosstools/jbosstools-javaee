package cdi.test.search5;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import cdi.test.search1.Base1;
import cdi.test.search4.Base4;
import javax.inject.Named;

@Named("abcd")
public class Base5 extends Base4 { 
	@Inject
	Event<Base1> event5;
}
