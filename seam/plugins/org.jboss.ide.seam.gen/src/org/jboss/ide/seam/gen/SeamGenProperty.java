package org.jboss.ide.seam.gen;

import java.util.Properties;

public class SeamGenProperty {

	private final String description;

	public SeamGenProperty(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public String getDefaultValue(Properties others) {
		return null;
	}
	
	protected String upper(String name)
	{
		if(name==null || name.length()==0) return "";
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	protected String lower(String name) {
		if ( name == null || name.length() == 0 )
			return "";
		return name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
	}
}
