/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaSourceReference;
import org.jboss.tools.seam.core.event.Change;
import org.w3c.dom.Element;

public abstract class SeamJavaContextVariable extends AbstractContextVariable implements ISeamJavaSourceReference {
	protected IMember javaSource = null;
	
	public SeamJavaContextVariable() {}

	public IMember getSourceMember() {
		return javaSource;
	}
	
	public void setSourceMember(IMember javaSource) {
		this.javaSource = javaSource;
	}

	public int getLength() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getLength();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

	public IResource getResource() {
		return javaSource == null || javaSource.getTypeRoot().getResource() == null ? super.getResource() : javaSource.getTypeRoot().getResource();
	}

	public int getStartPosition() {
		if(javaSource == null) return 0;
		try {
			if(javaSource.getSourceRange() == null) return 0;
			return javaSource.getSourceRange().getOffset();
		} catch (JavaModelException e) {
			//ignore
			return 0;
		}
	}

	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		
		if(s instanceof SeamJavaContextVariable) {
			SeamJavaContextVariable sf = (SeamJavaContextVariable)s;
			javaSource = sf.javaSource;
			resource = sf.resource;
		}
		
		return changes;
	}

	public SeamJavaContextVariable clone() throws CloneNotSupportedException {
		SeamJavaContextVariable c = (SeamJavaContextVariable)super.clone();
		return c;
	}
	
	static String TAG_JAVA_SOURCE = "java-source";

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		if(javaSource instanceof IField) {
			SeamXMLHelper.saveField(element, (IField)javaSource, TAG_JAVA_SOURCE, context);
		} else if(javaSource instanceof IMethod) {
			SeamXMLHelper.saveMethod(element, (IMethod)javaSource, TAG_JAVA_SOURCE, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		Element c = XMLUtilities.getUniqueChild(element, TAG_JAVA_SOURCE);
		if(c != null) {
			String cls = c.getAttribute(SeamXMLConstants.ATTR_CLASS);
			if(SeamXMLConstants.CLS_FIELD.equals(cls)) {
				javaSource = SeamXMLHelper.loadField(c, context);
			} else if(SeamXMLConstants.CLS_METHOD.equals(cls)) {
				javaSource = SeamXMLHelper.loadMethod(c, context);
			}
		}

	}

}
