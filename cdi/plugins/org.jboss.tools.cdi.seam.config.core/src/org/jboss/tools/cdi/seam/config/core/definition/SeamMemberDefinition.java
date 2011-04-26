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
package org.jboss.tools.cdi.seam.config.core.definition;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.seam.config.core.scanner.SAXElement;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class SeamMemberDefinition {
	protected SAXElement element;
	SAXElement replaces = null;
	SAXElement modifies = null;
	Map<String, IJavaAnnotation> annotations = new HashMap<String, IJavaAnnotation>();
	
	public SeamMemberDefinition() {}

	public void setElement(SAXElement element) {
		this.element = element;
	}

	public void setReplaces(SAXElement replaces) {
		this.replaces = replaces;
	}

	public void setModifies(SAXElement modifies) {
		this.modifies = modifies;
	}

	public void addAnnotation(IJavaAnnotation a) {
		annotations.put(a.getTypeName(), a);
	}
}
