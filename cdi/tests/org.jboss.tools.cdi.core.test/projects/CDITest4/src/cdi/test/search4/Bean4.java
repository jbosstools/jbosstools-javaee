package cdi.test.search4;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import cdi.test.search1.BaseDecoratedInterface;

public class Bean4 {
	@Inject
	@Any
	private BaseDecoratedInterface field4;
	
	public void method4(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_4(BaseDecoratedInterface param4){
		
	}

}
