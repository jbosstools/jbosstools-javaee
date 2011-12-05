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
