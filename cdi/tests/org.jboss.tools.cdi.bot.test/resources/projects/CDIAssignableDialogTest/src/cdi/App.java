package cdi;

import javax.inject.Inject;

public class App {

	@Inject
	AbstractManager manager;
	
	@Inject
	IManager managerImpl;
	
}
