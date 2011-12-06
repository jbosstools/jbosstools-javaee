package cdi.test.search2;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.inject.Named;

import cdi.test.search1.BaseDecoratedInterface;
import cdi.test.MyQualifier;
import test.Q;

@Named("customer")
public class Bean2 {
	@Inject @Q @MyQualifier
	@Any
	private BaseDecoratedInterface field2;
	
	public void method2(@Observes BaseDecoratedInterface event) {
		
	}
	
	@Inject public void method_2(BaseDecoratedInterface param2){
		
	}
	
	public String name = "";

}
