package test;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import cdi.test.Scope2;

public class Test1 {
	@Inject @Q boolean test; 

	@Scope2 @Q @Produces boolean foo() { 
		return true; 
	} 

}
