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
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenMember {
	protected Set<GenAnnotationReference> annotations = new HashSet<GenAnnotationReference>();
	String name;
	GenVisibility visibility = GenVisibility.LOCAL;

	public GenMember() {}

	public void addAnnotation(GenAnnotationReference annotation) {
		annotations.add(annotation);
	}
	
	public Set<GenAnnotationReference> getAnnotations() {
		return annotations;
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

	public void flushAnnotations(BodyWriter sb) {
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
			sb.newLine();
		}
	}

}
