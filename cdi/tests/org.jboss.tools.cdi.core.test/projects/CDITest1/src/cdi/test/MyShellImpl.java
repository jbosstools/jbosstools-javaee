package cdi.test;

import java.util.List;
import javax.inject.Inject;
import org.jboss.weld.environment.se.bindings.Parameters;

public class MyShellImpl {
	
	@Inject
	@Parameters
	private List<String> p4;
	
	
}
