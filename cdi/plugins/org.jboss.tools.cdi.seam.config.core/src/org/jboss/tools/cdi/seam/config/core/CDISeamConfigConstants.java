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

	public String SEAM_BEANS_XML = "seam-beans.xml";

	public String URI_PREFIX = "urn:java:";

	//Problem id: node name was not resolved to Java type
	public String ERROR_UNRESOLVED_TYPE = "Unresolved type";

	public String ERROR_UNRESOLVED_MEMBER = "Unresolved member";

	//Problem id: node was expected to be resolved to annotation type
	public String ERROR_ANNOTATION_EXPECTED = "Annotation expected";

	public String PACKAGE_EE = "ee";

	public String KEYWORD_ARRAY = "array";

	public String KEYWORD_ENTRY = "entry";
	public String KEYWORD_E = "e";

	public String KEYWORD_KEY = "key";
	public String KEYWORD_K = "k";

	public String KEYWORD_VALUE = "value";
	public String KEYWORD_V = "v";

	public String KEYWORD_REPLACES = "replaces";

	public String KEYWORD_MODIFIES = "modifies";
	
	public String KEYWORD_PARAMETERS = "parameters";

	public String ATTR_DIMENSIONS = "dimensions";

	public String INLINE_BEAN_QUALIFIER = "org.jboss.seam.config.xml.fieldset.InlineBeanQualifier";
}
