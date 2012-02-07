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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

public class ValuedQualifier{
	public static final int STATE_NONE = -1;
	public static final int STATE_NEW_QUALIFIER = 0;
	public static final int STATE_NEW_QUALIFIER_CHANGED_VALUE = 1;
	public static final int STATE_BEAN_QUALIFIER = 2;
	public static final int STATE_BEAN_QUALIFIER_CHANGED_VALUE = 3;
	
	private int state = STATE_NONE;
	private IQualifier qualifier;
	private List<Pair> pairs = new ArrayList<Pair>();
	
	public ValuedQualifier(IQualifier qualifier, IQualifierDeclaration declaration){
		this.qualifier = qualifier;
		if(declaration != null){
			// copy pairs from qualifier declaration
			for(IMemberValuePair mvp : declaration.getMemberValuePairs()){
				Pair pair = new Pair();
				pair.name = mvp.getMemberName();
				pair.value = mvp.getValue();
				if(mvp.getValueKind() == IMemberValuePair.K_STRING){
					pair.type = "String";
				}
				pairs.add(pair);	
			}
		}else{
			IType type = qualifier.getSourceType();
			try {
				if(type.isAnnotation()){
					for(IMethod method : type.getMethods()){
						IMemberValuePair mvp = method.getDefaultValue();
						Pair pair = new Pair();
						pair.type = Signature.getSignatureSimpleName(method.getReturnType());
						pair.name = method.getElementName();
						if(mvp != null && mvp.getValue() != null){
							pair.required = false;
							pair.value = mvp.getValue();
						}else{
							pair.required = true;
							if(pair.type.equals("boolean")){
								pair.value = "false";
							}else if(pair.type.equals("int") ||	pair.type.equals("short") || pair.type.equals("long")){
								pair.value = "0";
							}else if(pair.type.equals("float")){
								pair.value = "0";
							}else if(pair.type.equals("double")){
								pair.value = "0.0";
							}else if(pair.type.equals("char")){
								pair.value = ' ';
							}else if(pair.type.equals("byte")){
								pair.value = "0";						
							}else if(pair.type.equals("String")){
								pair.value = "default";
							}else{
								pair.value = "String.class";
							}
						}
						pairs.add(pair);
					}
				}
			} catch (JavaModelException e) {
			}
		}
	}
	
	public ValuedQualifier(IQualifier qualifier){
		this(qualifier, null);
	}
	
	public boolean isEditable(){
		for(Pair pair : pairs){
			if(pair.name.equals("value") && pair.type.equals("String")){
				return true;
			}
		}
		return false;
	}
	
	public int getState(){
		return state;
	}
	
	public List<Pair> getValuePairs(){
		return pairs;
	}
	
	public IQualifier getQualifier(){
		return qualifier;
	}
	
	public String getValue(){
		return generateValue();
	}
	
	public String getStringValue(){
		for(Pair pair : pairs){
			if(pair.name.equals("value") && pair.type.equals("String")){
				return (String)pair.value;
			}
		}
		return "";
	}
	
	private String generateValue(){
		String text = "";
		boolean first = true;
		for(Pair pair : pairs){
			if(!first){
				text += ", ";
			}
			if(!"value".equals(pair.name) || pairs.size() > 1){
				text += pair.name+"=";
			}
			if("char".equals(pair.type)){
				text += "\'"+pair.value+"\'";
			}else if("String".equals(pair.type)){
				text += "\""+pair.value+"\"";
			}else{
				text += pair.value;
			}
			first = false;
		}
		return text;
	}
	
	public void setValue(String name, String value){
		for(Pair pair : pairs){
			if(pair.name.equals(name)){
				pair.value = value;
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ValuedQualifier)
			return getQualifier().getSourceType().getFullyQualifiedName().equals(((ValuedQualifier)obj).getQualifier().getSourceType().getFullyQualifiedName());
		return false;
	}
	
	static class Pair{
		public boolean required;
		public String type="";
		public String name="";
		public Object value;
	}
	
}
