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
package org.jboss.tools.seam.internal.core;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamComponentMethod extends SeamObject implements ISeamComponentMethod {
	Set<SeamComponentMethodType> types = new HashSet<SeamComponentMethodType>();
	
	IMember javaSource = null;
	
	public SeamComponentMethod() {}

	/**
	 * @return is types of the method
	 */
	public Set<SeamComponentMethodType> getTypes() {
		return types;
	}

	public boolean isOfType(SeamComponentMethodType type) {
		return types.contains(type);
	}

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
		if(resource == null) {
			if(javaSource == null) return super.getResource();
			resource = javaSource.getResource();
		}
		return resource;
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
		SeamComponentMethod m = (SeamComponentMethod)s;
		if(!typesAreEqual(types, m.types)) {
			changes = Change.addChange(changes, new Change(this, "types", types, m.types)); //$NON-NLS-1$
			this.types = m.types;
		}
		return changes;
	}

	boolean typesAreEqual(Set<SeamComponentMethodType> types1, Set<SeamComponentMethodType> types2) {
		if(types1 == null || types2 == null) return types2 == types1;
		if(types1.size() != types2.size()) return false;
		for (SeamComponentMethodType t : types2) {
			if(!types1.contains(t)) return false;
		}
		return true;
		
	}

	public SeamComponentMethod clone() throws CloneNotSupportedException {
		SeamComponentMethod c = (SeamComponentMethod)super.clone();
		c.types = new HashSet<SeamComponentMethodType>();
		c.types.addAll(types);
		return c;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_METHOD;
	}
	
	static String ATTR_COMP_TYPES = "comp-types";
	
	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		if(types != null) {
			StringBuffer sb = new StringBuffer();
			for (SeamComponentMethodType t : types) {
				sb.append(t.toString()).append(';');
			}
			element.setAttribute(ATTR_COMP_TYPES, sb.toString());
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		if(id instanceof IMember) {
			javaSource = (IMember)id;
		}
		
		if(element.hasAttribute(ATTR_COMP_TYPES)) {
			String v = element.getAttribute(ATTR_COMP_TYPES);
			if(v != null && v.length() > 0) {
				String[] cs = v.split(";");
				for (int i = 0; i < cs.length; i++) {
					types.add(SeamComponentMethodType.valueOf(cs[i]));
				}
			}
		}
	}

	@Override
	public IJavaElement getSourceElement() {
		return getSourceMember();
	}
}