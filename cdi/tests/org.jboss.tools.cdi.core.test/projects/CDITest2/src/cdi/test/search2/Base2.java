package cdi.test.search2;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import cdi.test.search1.Base1;

public class Base2 extends Base1 {
	@Inject
	Event<Base1> event2;
}
