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

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenMethod extends GenMember {
	GenType returnType;
	boolean isAbstract = false;
	
	public GenMethod() {
		setVisibility(GenVisibility.PUBLIC);
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean b) {
		isAbstract = b;
	}

	public void setReturnType(GenType returnType) {
		this.returnType = returnType;
	}

	public GenType getReturnType() {
		return returnType;
	}

	public void flush(BodyWriter sb) {
		flushAnnotations(sb);
		flushVisibility(sb);
		sb.append(returnType.getTypeName()).append(" ").append(getName());
		sb.append("(");
		//TODO parameters
		sb.append(")");
		if(isAbstract()) {
			sb.append(";").newLine();
		} else {
			sb.append(" {").newLine().increaseIndent();
			if(!"void".equals(returnType.getTypeName())) {
				sb.append("return null;").newLine();
			}		
			sb.decreaseIndent().append("}");
		}
	}

}
