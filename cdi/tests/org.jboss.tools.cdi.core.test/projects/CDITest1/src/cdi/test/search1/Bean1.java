package cdi.test.search1;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

public class Bean1 {
	@Inject
	@Any
	private BaseDecoratedInterface field1;
	
	public void method1(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_1(BaseDecoratedInterface param1){
		
	}
}
