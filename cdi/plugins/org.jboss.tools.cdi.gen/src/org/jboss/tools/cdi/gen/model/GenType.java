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

import java.util.HashSet;
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

	public GenType() {
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

	public String getFullyQualifiedName() {
		return getPackageName() + "." + getName();
	}

	public void addImport(String type) {
		imports.add(type);
	}

}
