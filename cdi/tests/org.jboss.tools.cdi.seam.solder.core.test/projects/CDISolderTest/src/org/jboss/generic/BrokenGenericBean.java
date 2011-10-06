package org.jboss.generic;

import javax.inject.Inject;

import org.jboss.solder.bean.generic.Generic;
import org.jboss.solder.bean.generic.GenericConfiguration;

@GenericConfiguration(Override.class)
public class BrokenGenericBean {
	int x;
	
}
