/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core;

import org.jboss.tools.cdi.core.CDIConstants;

public interface CDISeamConfigConstants extends CDIConstants {

	public static String SEAM_BEANS_XML = "seam-beans.xml";

	public static String URI_PREFIX = "urn:java:";

	//Problem id: node name was not resolved to Java type
	public static String UNRESOLVED_TYPE = "Unresolved type";

	//Problem id: node was expected to be resolved to annotation type
	public static String ANNOTATION_EXPECTED = "Annotation expected";

}
