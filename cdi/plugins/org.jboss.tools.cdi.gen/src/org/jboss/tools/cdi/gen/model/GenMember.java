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
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class GenMember {
	private GenMember parent;
	private List<GenAnnotationReference> annotations = new ArrayList<GenAnnotationReference>();
	private List<GenAnnotationReference> qualifierAnnotations = new ArrayList<GenAnnotationReference>();
	String name;
	GenVisibility visibility = GenVisibility.LOCAL;

	public GenMember() {}

	public GenMember getParent() {
		return parent;
	}

	public void setParent(GenMember parent) {
		this.parent = parent;
	}

	/**
	 * May return null if this member is not a type and is not added to a type.
	 * @return
	 */
	public GenType getDeclaringType() {
		return parent != null ? parent.getDeclaringType() : null;
	}

	public void addAnnotation(GenAnnotationReference annotation) {
		annotations.add(annotation);
		if(getDeclaringType() != null) {
			new GenImportsCollector(getDeclaringType()).addImports(annotation);
		}
	}
	
	public Collection<GenAnnotationReference> getAnnotations() {
		return annotations;
	}

	public void addQualifierAnnotation(GenQualifier q, String value) {
		GenAnnotationReference a = new GenAnnotationReference();
		a.setAnnotation(q);		

		if(value != null) {
			a.getValues().put("value", "\"" + value + "\"");
		}
		
		addAnnotation(a);
		qualifierAnnotations.add(a);
	}

	public Collection<GenAnnotationReference> getQualifiers() {
		return qualifierAnnotations;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setVisibility(GenVisibility visibility) {
		this.visibility = visibility;
	}

	public GenVisibility getVisibility() {
		return visibility;
	}

	public void flushVisibility(BodyWriter sb) {
		if(visibility != GenVisibility.LOCAL) {
			sb.append(visibility.toString()).append(" ");
		}
	}

	public abstract GenType getType();

	public void flushAnnotations(BodyWriter sb) {
		flushAnnotations(sb, false);
	}

	public void flushAnnotations(BodyWriter sb, boolean separateBySpace) {
		for (GenAnnotationReference a: getAnnotations()) {
			sb.append("@").append(a.getTypeName());
			Map<String, Object> vs = a.getValues();
			if(!vs.isEmpty()) {
				sb.append("(");
				if(vs.size() == 1 && vs.containsKey("value")) {
					sb.append(vs.get("value"));
				} else {
					//TODO
				}
				sb.append(")");
			}
			if(separateBySpace) sb.append(" "); else sb.newLine();
		}
	}

}
