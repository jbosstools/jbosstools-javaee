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
 * Seam Versions enumeration
 * 
 * @author eskimo
 */

public enum SeamVersion {
	/**
	 * Seam versions 1.2.X
	 */
	SEAM_1_2("1.2"),
	
	/**
	 * Seam versions 2.0.X
	 */
	SEAM_2_0("2.0"), //$NON-NLS-1$ //$NON-NLS-2$

	/**
	 * Seam versions 2.1.X
	 */
	SEAM_2_1("2.1"),  //$NON-NLS-1$
	
	/**
	 * Seam versions 2.2.X
	 */
	SEAM_2_2("2.2");  //$NON-NLS-1$
	
	String version = ""; //$NON-NLS-1$

	SeamVersion(String version) {
		this.version = version;
	}

	/**
	 * Return string representation of version
	 * 
	 * @return
	 * 	version number as string
	 */
	public String toString() {
		return version;
	}

	public static SeamVersion[] ALL_VERSIONS = new SeamVersion[]{SEAM_1_2, SEAM_2_0, SEAM_2_1, SEAM_2_2};

	/**
	 * Get enumeration by string
	 * TODO support for compatible version? should 1.2.1 return SEAM_1_2?
	 * 
	 * @param version
	 * 	string representation of version
	 * @return
	 * 	version enumeration corresponding to version string
	 */
	public static SeamVersion parseFromString(String version) {
		if (SEAM_1_2.toString().equals(version)) {
			return SEAM_1_2;
		} else if (SEAM_2_0.toString().equals(version)) {
			return SEAM_2_0;
		} else if (SEAM_2_1.toString().equals(version)) {
			return SEAM_2_1;
		} else if (SEAM_2_2.toString().equals(version)) {
			return SEAM_2_2;
		}
		throw new IllegalArgumentException(NLS.bind(
				"Seam version ''{0}'' is not supported", version)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Get enumeration by string
	 * If can't find any version then return null.
	 * 
	 * @param version
	 * 	string representation of version
	 * @return
	 * 	version enumeration corresponding to version string
	 */
	public static SeamVersion findByString(String version) {
		if (SEAM_1_2.toString().equals(version)) {
			return SEAM_1_2;
		} else if (SEAM_2_0.toString().equals(version)) {
			return SEAM_2_0;
		} else if (SEAM_2_1.toString().equals(version)) {
			return SEAM_2_1;
		} else if (SEAM_2_2.toString().equals(version)) {
			return SEAM_2_2;
		}
		return null;
	}
}
