package cdi.test.observers;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class CDIBeanTest {
	public void method(@Observes BaseDecoratedInterface event) {
		
	}

	@Inject BaseDecoratedInterface point;
}
