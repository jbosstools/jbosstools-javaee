package cdi.test.observers;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class TestBean implements BaseDecoratedInterface {
	@Inject
	Event<TestBean> event;

	public void observer(@Observes BaseDecoratedInterface event) {
		
	}

}
