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

import java.util.ArrayList;
import java.util.Collection;

/**
 * 
 * @author jjankovi
 *
 */
public enum SeamLibrary {

	SOLDER_3, SOLDER_3_1, INTERNATIONAL, UNKNOWN;
	
	private Collection<String> libraries;
	
	public Collection<String> getLibrariesNames() {
		switch (this) {
		case SOLDER_3:
			libraries = new ArrayList<String>();
			libraries.add("seam-solder.jar");
			return libraries;			
		case INTERNATIONAL:
			libraries = new ArrayList<String>();
			libraries.add("seam-international.jar");
			return libraries;
		case SOLDER_3_1:	
			libraries = new ArrayList<String>();
			libraries.add("solder-api.jar");
			libraries.add("solder-impl.jar");
			libraries.add("solder-logging.jar");
			libraries.add("solder-tooling.jar");
			return libraries;
		case UNKNOWN:
		default:
			throw new AssertionError("Unknown libraries");
		}
	}
	
}
