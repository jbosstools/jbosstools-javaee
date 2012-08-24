/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.gen.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenAnnotationReference {
	private GenAnnotation annotation;
	private Map<String, Object> values = new HashMap<String, Object>();
	
	public GenAnnotationReference() {		
	}

	public void setAnnotation(GenAnnotation annotation) {
		this.annotation = annotation;
	}

	public String getPackageName() {
		return annotation.getPackageName();
	}

	public String getTypeName() {
		return annotation.getTypeName();
	}

	public Map<String, Object> getValues() {
		return values;
	}

	public void setValue(String name, Object value) {
		values.put(name, value);
	}

	public String getFullyQualifiedName() {
		return annotation.getFullyQualifiedName();
	}
}
