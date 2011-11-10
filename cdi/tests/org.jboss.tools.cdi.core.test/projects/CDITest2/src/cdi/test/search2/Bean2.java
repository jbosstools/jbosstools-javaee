package cdi.test.search2;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import cdi.test.search1.BaseDecoratedInterface;


public class Bean2 {
	@Inject
	@Any
	private BaseDecoratedInterface field2;
	
	public void method2(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_2(BaseDecoratedInterface param2){
		
	}

}
