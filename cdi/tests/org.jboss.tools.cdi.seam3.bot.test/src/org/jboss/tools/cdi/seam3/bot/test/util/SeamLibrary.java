/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.util;

/**
 * 
 * @author jjankovi
 *
 */
public enum SeamLibrary {

	SOLDER, INTERNATIONAL, UNKNOWN;
	
	public String getName() {
		switch (this) {
		case SOLDER:
			return "seam-solder.jar";			
		case INTERNATIONAL:
			return "seam-international.jar";
		case UNKNOWN:
		default:
			throw new AssertionError("Unknown libraries");
		}
	}
	
}
