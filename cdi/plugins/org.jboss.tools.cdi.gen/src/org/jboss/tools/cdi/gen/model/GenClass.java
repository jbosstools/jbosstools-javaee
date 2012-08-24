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
import java.util.List;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenClass extends GenType {
	GenClass extendedType;
	List<GenField> fields = new ArrayList<GenField>();
	
	public GenClass() {}
	
	public void setExtendedType(GenClass extendedType) {
		this.extendedType = extendedType;
		getDeclaringType().addImport(extendedType.getFullyQualifiedName());
	}

	public GenClass getExtendedType() {
		return extendedType;
	}

	public void addField(GenField f) {
		fields.add(f);
		if(getDeclaringType() != null) {
			new GenImportsCollector(getDeclaringType()).addImports(f);
		}
	}

	public void flush(BodyWriter sb) {
		sb.append("package ").append(getPackageName()).append(";").newLine().newLine();
	
		//imports
		for (String i: getImports()) {
			sb.append("import ").append(i).append(";").newLine();
		}
		sb.append("\n");
		//annotations
		flushAnnotations(sb);
		//header
		flushVisibility(sb);
		sb.append("class ").append(getTypeName());
		if(extendedType != null) {
			sb.append(" extends ").append(extendedType.getTypeName());
		}
		int imported = 0;
		for (GenInterface in: getImplementedTypes()) {
			if(imported == 0) {
				sb.append(" implements ");
			} else {
				sb.append(", ");
			}
			imported++;
			sb.append(in.getTypeName());
		}
		sb.append(" {").newLine().increaseIndent();
		
		for (GenField f: fields) {
			f.flush(sb);
			sb.newLine();
		}
	
		flushMethods(sb);

		//TODO body
		
		sb.decreaseIndent().append("}");
	}

}
