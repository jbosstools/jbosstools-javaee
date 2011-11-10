package cdi.test.search3;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import cdi.test.search1.BaseDecoratedInterface;


public class Bean3 {
	@Inject
	@Any
	private BaseDecoratedInterface field3;
	
	public void method3(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_3(BaseDecoratedInterface param3){
		
	}
}
