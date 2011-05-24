package org.jboss.tools.cdi.ui.test.testmodel;

public class CDIElement {
	protected static String getShortName(String qualifiedName){
		int lastDot = qualifiedName.lastIndexOf(".");
		String name;
		if(lastDot < 0)
			name = qualifiedName;
		else
			name = qualifiedName.substring(lastDot+1);
		return name;
	}
}
