package org.jboss.tools.cdi.bot.test.annotations;

public enum JSFEnvironment {
	
	JSF_11, JSF_12, JSF_12_FACETS, JSF_20;
	
	public String getName() {
		switch (this) {
		case JSF_11:
			return "JSF 1.1.02 - Reference Implementation";			
		case JSF_12:
			return "JSF 1.2";			
		case JSF_12_FACETS:
			return "JSF 1.2 with Facets";
		case JSF_20:
			return "JSF 2.0";
		default:
			throw new AssertionError("Unknown type");
		}
	}
}
