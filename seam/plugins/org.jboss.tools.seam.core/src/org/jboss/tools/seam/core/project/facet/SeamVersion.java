/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core.project.facet;

import org.eclipse.osgi.util.NLS;

/**
 * @author eskimo
 *
 */
public enum SeamVersion{
	SEAM_1_2("1.2"), SEAM_2_0("2.0"); //$NON-NLS-1$ //$NON-NLS-2$
	
	String version = ""; //$NON-NLS-1$
	
	public static final String V_1_2 = "1.2"; //$NON-NLS-1$
	
	public static final String V_2_0 = "2.0"; //$NON-NLS-1$
	
	SeamVersion(String version) {
		this.version = version;
	}
	
	public String toString() {
		return version;
	}
	
	static public SeamVersion parseFromString(String version) {
		if(V_1_2.equals(version)) {
			return SEAM_1_2;
		} else if(V_2_0.equals(version)) {
			return SEAM_2_0;
		}
		throw new IllegalArgumentException(NLS.bind("Seam version ''{0}'' is not supported",version)); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
