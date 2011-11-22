package org.jboss.tools.cdi.bot.test.annotations;

public enum JSFTemplate {

	BLANK, KICKSTART;
	
	public String getName() {
		switch (this) {
		case BLANK:
			return "JSFBlankWithoutLibs";
		case KICKSTART:
			return "JSFKickStartWithoutLibs";
		default:
			throw new AssertionError("Unknown type");
		}
	}
	
}
