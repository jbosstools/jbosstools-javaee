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
	Set<String> imports = new HashSet<String>();
	String packageName;
	
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

	public void setFullyQualifiedName(String qn) {
		int dot = qn.lastIndexOf('.');
		setTypeName(dot < 0 ? qn : qn.substring(dot + 1));
		setPackageName(dot < 0 ? "" : qn.substring(0, dot));
	}

	public String getFullyQualifiedName() {
		return getPackageName() + "." + getName();
	}

	public void addImport(String type) {
		imports.add(type);
	}

	public void addMethod(GenMethod method) {
		methods.add(method);
	}

	public void flushMethods(BodyWriter sb) {
		for (GenMethod m: methods) {
			m.flush(sb);
			sb.newLine();
		}
	}

}
