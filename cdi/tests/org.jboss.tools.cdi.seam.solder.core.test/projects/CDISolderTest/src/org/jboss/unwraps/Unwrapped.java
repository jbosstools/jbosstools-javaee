package org.jboss.unwraps;

import javax.inject.Inject;

import org.jboss.solder.unwraps.Unwraps;

public class Unwrapped {
	
	@Unwraps @Current 
	Permission getPermission() {
		return new Permission();
	}

	@Inject @Current
	Permission permission;

}
