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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenType extends GenMember implements CDIConstants {
	private Set<String> imports = new HashSet<String>();
	private String packageName;
	private List<GenInterface> implementedTypes = new ArrayList<GenInterface>();
	
	List<GenMethod> methods = new ArrayList<GenMethod>();

	public GenType() {
		setVisibility(GenVisibility.PUBLIC);
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setTypeName(String typeName) {
		setName(typeName);
	}

	public String getTypeName() {
		return getName();
	}

	public GenType getType() {
		return this;
	}

	public GenType getDeclaringType() {
		return getParent() instanceof GenType ? getParent().getDeclaringType() : this;
	}

	public void setFullyQualifiedName(String qn) {
		int dot = qn.lastIndexOf('.');
		setTypeName(dot < 0 ? qn : qn.substring(dot + 1));
		setPackageName(dot < 0 ? "" : qn.substring(0, dot));
	}

	public String getFullyQualifiedName() {
		return getPackageName().length() == 0 ? getName() : getPackageName() + "." + getName();
	}

	public Collection<String> getImports() {
		return imports;
	}

	public void addImport(String type) {
		if(type.startsWith("java.lang.") && type.lastIndexOf(".") == 9) return;
		if(type.indexOf('.') < 0) return;
		imports.add(type);
	}

	public void addImplementedType(GenInterface implementedType) {
		if(!implementedTypes.contains(implementedType)) {
			implementedTypes.add(implementedType);
			getDeclaringType().addImport(implementedType.getFullyQualifiedName());
		}
	}

	public Collection<GenInterface> getImplementedTypes() {
		return implementedTypes;
	}

	public void addMethod(GenMethod method) {
		methods.add(method);
		new GenImportsCollector(getDeclaringType()).addImports(method);
	}

	public void flushMethods(BodyWriter sb) {
		for (GenMethod m: methods) {
			m.flush(sb);
			sb.newLine();
		}
	}

}
