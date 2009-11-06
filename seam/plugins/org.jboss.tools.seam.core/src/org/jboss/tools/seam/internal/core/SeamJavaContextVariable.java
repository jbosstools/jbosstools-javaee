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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.model.project.ext.store.XMLStoreHelper;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.w3c.dom.Element;

public abstract class SeamJavaContextVariable extends AbstractContextVariable implements IJavaSourceReference {
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
			XMLStoreHelper.saveField(element, (IField)javaSource, TAG_JAVA_SOURCE, context);
		} else if(javaSource instanceof IMethod) {
			XMLStoreHelper.saveMethod(element, (IMethod)javaSource, TAG_JAVA_SOURCE, context);
		} else if(javaSource instanceof IType) {
			Element ce = XMLUtilities.createElement(element, TAG_JAVA_SOURCE);
			ce.setAttribute(SeamXMLConstants.ATTR_CLASS, SeamXMLConstants.CLS_TYPE);
			XMLStoreHelper.saveType(ce, (IType)javaSource, context);
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		Element c = XMLUtilities.getUniqueChild(element, TAG_JAVA_SOURCE);
		if(c != null) {
			String cls = c.getAttribute(SeamXMLConstants.ATTR_CLASS);
			if(SeamXMLConstants.CLS_FIELD.equals(cls)) {
				javaSource = XMLStoreHelper.loadField(c, context);
			} else if(SeamXMLConstants.CLS_METHOD.equals(cls)) {
				javaSource = XMLStoreHelper.loadMethod(c, context);
			} else if(SeamXMLConstants.CLS_TYPE.equals(cls)) {
				javaSource = XMLStoreHelper.loadType(c, context);
			}
		}

	}

	public void open() {
		if(javaSource == null) return;
		if(!javaSource.exists()) {
			ServiceDialog d = PreferenceModelUtilities.getPreferenceModel().getService();
			d.showDialog("Warning", "Member " + javaSource.getElementName() + " does not exist.", new String[]{SpecialWizardSupport.OK}, null, ServiceDialog.WARNING);
			return;
		}
		try {
			JavaUI.openInEditor(javaSource);
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}
	
}
