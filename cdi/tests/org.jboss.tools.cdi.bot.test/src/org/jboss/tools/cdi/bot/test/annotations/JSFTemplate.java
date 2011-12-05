/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.

 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.annotations;

public enum JSFTemplate {

	BLANK_LIBS, BLANK_NO_LIBS, 
	KICKSTART_LIBS, KICKSTART_RI_LIBS, KICKSTART_NO_LIBS;
	
	public String getName() {
		switch (this) {
		case BLANK_LIBS:
			return "JSFBlankWithLibs";
		case BLANK_NO_LIBS:
			return "JSFBlankWithoutLibs";
		case KICKSTART_LIBS:
			return "JSFKickStartWithLibs";
		case KICKSTART_RI_LIBS:
			return "JSFKickStartWithRILibs";
		case KICKSTART_NO_LIBS:
			return "JSFKickStartWithoutLibs";
		default:
			throw new AssertionError("Unknown type");
		}
	}
	
}
