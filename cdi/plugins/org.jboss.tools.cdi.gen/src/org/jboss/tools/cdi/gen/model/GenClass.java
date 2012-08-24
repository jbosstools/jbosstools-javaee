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

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenClass extends GenType {
	GenClass extendedType;
	List<GenInterface> implementedTypes = new ArrayList<GenInterface>();
	Set<GenAnnotationReference> qualifierAnnotations = new HashSet<GenAnnotationReference>();
	List<GenField> fields = new ArrayList<GenField>();
	
	public GenClass() {}
	
	public void setExtendedType(GenClass extendedType) {
		this.extendedType = extendedType;
		addImport(extendedType.getFullyQualifiedName());
	}

	public void addImplementedType(GenInterface implementedType) {
		if(!implementedTypes.contains(implementedType)) {
			implementedTypes.add(implementedType);
			addImport(implementedType.getFullyQualifiedName());
		}
	}

	public GenClass getExtendedType() {
		return extendedType;
	}

	public List<GenInterface> getImplementedTypes() {
		return implementedTypes;
	}

	public void addQualifierAnnotation(GenQualifier q, String value) {
		addImport(q.getFullyQualifiedName());
		
		GenAnnotationReference a = new GenAnnotationReference();
		a.setAnnotation(q);
		

		if(value != null) {
			a.getValues().put("value", "\"" + value + "\"");
		}
		
		addAnnotation(a);
		qualifierAnnotations.add(a);
	}

	public Set<GenAnnotationReference> getQualifiers() {
		return qualifierAnnotations;
	}

	public void addField(GenField f) {
		fields.add(f);
		addImport(f.getType().getFullyQualifiedName());
		for (GenAnnotationReference a: f.getAnnotations()) {
			addImport(a.getFullyQualifiedName());
		}
	}

	public void flush(BodyWriter sb) {
		sb.append("package ").append(getPackageName()).append(";").newLine().newLine();
	
		//imports
		for (String i: imports) {
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
		for (GenInterface in: implementedTypes) {
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
