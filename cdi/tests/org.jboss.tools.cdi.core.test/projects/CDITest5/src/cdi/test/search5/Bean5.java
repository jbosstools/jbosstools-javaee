package cdi.test.search5;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import cdi.test.search1.BaseDecoratedInterface;
import javax.inject.Named;

public class Bean5 {
	@Inject
	@Any
	private BaseDecoratedInterface field5;
	
	public void method5(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_5(BaseDecoratedInterface param5){
		
	}
}
