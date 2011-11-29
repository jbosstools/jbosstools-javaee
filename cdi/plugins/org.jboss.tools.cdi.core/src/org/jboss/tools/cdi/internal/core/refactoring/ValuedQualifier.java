/*******************************************************************************
  * Copyright (c) 2011 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.cdi.internal.core.refactoring;

import org.jboss.tools.cdi.core.IQualifier;

public class ValuedQualifier{
	private IQualifier qualifier;
	private String value="";
	
	public ValuedQualifier(IQualifier qualifier){
		this.qualifier = qualifier;
	}
	
	public ValuedQualifier(IQualifier qualifier, String value){
		this(qualifier);
		this.value = value;
	}
	
	public IQualifier getQualifier(){
		return qualifier;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}

	public boolean equals(Object obj) {
		if(obj instanceof ValuedQualifier)
			return getQualifier().getSourceType().getFullyQualifiedName().equals(((ValuedQualifier)obj).getQualifier().getSourceType().getFullyQualifiedName());
		return false;
	}
}
