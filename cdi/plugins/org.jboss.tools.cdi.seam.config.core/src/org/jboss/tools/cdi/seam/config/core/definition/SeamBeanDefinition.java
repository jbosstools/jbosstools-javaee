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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.seam.config.core.xml.SAXElement;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeanDefinition extends SeamMemberDefinition {
	protected boolean inline = false;
	protected IType type = null;
	protected List<SeamFieldDefinition> fields = new ArrayList<SeamFieldDefinition>();
	protected List<SeamMethodDefinition> methods = new ArrayList<SeamMethodDefinition>();

	public SeamBeanDefinition() {}

	public SAXElement getElement() {
		return (SAXElement)getNode();
	}

	public void setInline(boolean b) {
		inline = b;
	}

	public boolean isInline() {
		return inline;
	}

	public void setType(IType type) {
		this.type = type;
	}

	public IType getType() {
		return type;
	}

	public void addField(SeamFieldDefinition field) {
		fields.add(field);
		field.setParent(this);
	}

	public void addMethod(SeamMethodDefinition method) {
		methods.add(method);
		method.setParent(this);
	}

	public SeamFieldDefinition getField(String name) {
		for (SeamFieldDefinition d: fields) {
			if(name.equals(d.getName())) return d;
		}
		return null;
	}

	public List<SeamMethodDefinition> getMethods() {
		return methods;
	}

	public SeamMethodDefinition getMethod(IMethod method) {
		for (SeamMethodDefinition m: methods) {
			IMethod c = m.getMethod();
			if(c != null && c.getElementName().equals(method.getElementName())
					&& c.equals(method)) {
				return m;
			}
		}
		return null;
	}

	public SeamMemberDefinition findExactly(int offset) {
		if(node.getLocation().includes(offset)) return this;
		for (SeamFieldDefinition f: fields) {
			if(f.getNode().getLocation().includes(offset)) return f;
		}
		for (SeamMethodDefinition m: methods) {
			if(m.getNode().getLocation().includes(offset)) return m;
		}
		return null;
	}

}
