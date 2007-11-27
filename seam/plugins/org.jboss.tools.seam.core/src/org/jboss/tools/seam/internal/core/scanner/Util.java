 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.scanner;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

/**
 * @author Viacheslav Kabanovich
 */
public class Util implements SeamAnnotations {
	
	static Set<String> EJB_ANNOTATION_TYPES = new HashSet<String>();
	
	static {
		EJB_ANNOTATION_TYPES.add(STATEFUL_ANNOTATION_TYPE);
		EJB_ANNOTATION_TYPES.add(ENTITY_ANNOTATION_TYPE);
		EJB_ANNOTATION_TYPES.add(REMOVE_ANNOTATION_TYPE);
		EJB_ANNOTATION_TYPES.add(STATELESS_ANNOTATION_TYPE);
		EJB_ANNOTATION_TYPES.add(MESSAGE_DRIVEN_ANNOTATION_TYPE);
	}

	/**
	 * Returns true if parameter is qualified name of annotation type
	 * used to build seam model.
	 * @param qualifiedTypeName
	 * @return
	 */
	public static boolean isSeamAnnotationType(String qualifiedTypeName) {
		return qualifiedTypeName != null
				&& (qualifiedTypeName.startsWith(SEAM_ANNOTATION_TYPE_PREFIX) 
						|| EJB_ANNOTATION_TYPES.contains(qualifiedTypeName));
	}

}
