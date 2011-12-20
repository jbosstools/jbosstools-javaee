/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanField;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
import org.jboss.tools.common.util.BeanUtil;

/**
 * @author Daniel Azarov
 */
public class CDIMarkerResolutionUtils extends MarkerResolutionUtils{

	public static void addQualifier(String qualifiedName, String value, ICompilationUnit compilationUnit, IJavaElement element, MultiTextEdit rootEdit) throws JavaModelException{
		if(!(element instanceof ISourceReference))
			return;
		IAnnotation annotation = findAnnotation(element, qualifiedName);
		if(annotation != null && annotation.exists())
			return;

		boolean duplicateShortName = addImport(qualifiedName, compilationUnit, rootEdit);
		
		String lineDelim = SPACE;
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		if(!value.isEmpty())
			value = "(\""+value+"\")";
		
		if(duplicateShortName)
			shortName = qualifiedName;
		
		annotation = findAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		
		if(rootEdit != null){
			if(annotation != null && annotation.exists()){
				TextEdit edit = new InsertEdit(annotation.getSourceRange().getOffset()+annotation.getSourceRange().getLength(), lineDelim+AT+shortName+value);
				rootEdit.addChild(edit);
			}else{
				TextEdit edit = new InsertEdit(((ISourceReference)element).getSourceRange().getOffset(), AT+shortName+value+lineDelim);
				rootEdit.addChild(edit);
			}
		}else{
			if(annotation != null && annotation.exists()){
				buffer.replace(annotation.getSourceRange().getOffset()+annotation.getSourceRange().getLength(), 0, lineDelim+AT+shortName+value);
			}else{
				buffer.replace(((ISourceReference)element).getSourceRange().getOffset(), 0, AT+shortName+value+lineDelim);
			}
			
			synchronized(compilationUnit) {
				compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
			}
		}
	}

	public static void updateQualifier(String qualifiedName, String value, ICompilationUnit compilationUnit, IJavaElement element, MultiTextEdit rootEdit) throws JavaModelException{
		if(!(element instanceof ISourceReference))
			return;
		IAnnotation annotation = findAnnotation(element, qualifiedName);
		if(annotation == null || !annotation.exists())
			return;
		
		boolean duplicateShortName = addImport(qualifiedName, compilationUnit, rootEdit);
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		if(!value.isEmpty())
			value = "(\""+value+"\")";
		
		if(duplicateShortName)
			shortName = qualifiedName;
		
		String newValue = AT+shortName+value;
		
		if(!annotation.getSource().equals(newValue)){
			if(rootEdit != null){
				TextEdit edit = new ReplaceEdit(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), newValue);
				rootEdit.addChild(edit);
			}else{
				buffer.replace(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), newValue);
				
				synchronized(compilationUnit) {
					compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
				}
			}
		}
	}
	
	private static boolean contains(IQualifierDeclaration declaration, List<ValuedQualifier> declarations){
		for(ValuedQualifier d : declarations){
			if(declaration.getQualifier().getSourceType().getFullyQualifiedName().equals(d.getQualifier().getSourceType().getFullyQualifiedName()))
				return true;
		}
		return false;
	}
	
	private static List<IQualifier> findQualifiersToDelete(IInjectionPoint injectionPoint, List<ValuedQualifier> qualifiers){
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Set<IQualifierDeclaration> declarations = injectionPoint.getQualifierDeclarations();
		for(IQualifierDeclaration declaration : declarations){
			if(!contains(declaration, qualifiers))
				list.add(declaration.getQualifier());
		}
		return list;
	}
	
	private static void addQualifiersToParameter(ICompilationUnit compilationUnit, IInjectionPoint injectionPoint, List<ValuedQualifier>  declarations, MultiTextEdit rootEdit){
		HashMap<IQualifier, Boolean> duplicants = new HashMap<IQualifier, Boolean>();
		if(!(injectionPoint instanceof IInjectionPointParameter))
			return;
		try{
			for(ValuedQualifier declaration : declarations){
				String qualifierName = declaration.getQualifier().getSourceType().getFullyQualifiedName();
				boolean duplicant = false;
				if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) &&
					!qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
						duplicant = addImport(qualifierName, compilationUnit, rootEdit);
				}
				duplicants.put(declaration.getQualifier(), new Boolean(duplicant));
			}
			
			String paramName = ((IInjectionPointParameter)injectionPoint).getName();
			IMethod method =  ((IInjectionPointParameter)injectionPoint).getBeanMethod().getMethod();
			IType type = method.getDeclaringType();
			IType t = compilationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
		
			IBuffer buffer = compilationUnit.getBuffer();
			
			ILocalVariable[] parameters = m.getParameters();
			for(int index = 0; index < parameters.length; index++){
				if(parameters[index].getElementName().equals(paramName)){
					StringBuffer b = new StringBuffer();
					if(index > 0)
						b.append(SPACE);
					for(ValuedQualifier declaration : declarations){
						String qualifierName = declaration.getQualifier().getSourceType().getFullyQualifiedName();
						String value = declaration.getValue();
						
						if(!value.isEmpty())
							value = "(\""+value+"\")";
						
						if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
							boolean duplicant = duplicants.get(declaration.getQualifier()).booleanValue();
							String annotation = getShortName(qualifierName);
							if(duplicant)
								annotation = qualifierName;
								b.append(AT+annotation+value+SPACE);
						}
					}
					b.append(Signature.getSignatureSimpleName(parameters[index].getTypeSignature())+SPACE);
					b.append(parameters[index].getElementName());
					
					String newValue = b.toString();
					
					if(!parameters[index].getSource().equals(newValue)){
						if(rootEdit != null){
							TextEdit edit = new ReplaceEdit(parameters[index].getSourceRange().getOffset(), parameters[index].getSourceRange().getLength(), b.toString());
							rootEdit.addChild(edit);
						}else{
							buffer.replace(parameters[index].getSourceRange().getOffset(), parameters[index].getSourceRange().getLength(), b.toString());
							
							synchronized(compilationUnit) {
								compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
							}
						}
					}
				}
			}
			
		}catch(JavaModelException ex){
			CDICorePlugin.getDefault().logError(ex);
		}
	}
	
	public static ISourceRange getParameterRegion(IInjectionPointParameter injectionParameter){
		try{
			String paramName = injectionParameter.getName();
			IMethod method =  injectionParameter.getBeanMethod().getMethod();
			
			for(ILocalVariable parameter : method.getParameters()){
				if(parameter.getElementName().equals(paramName)){
					return parameter.getSourceRange();
				}
			}
		}catch(JavaModelException ex){
			CDICorePlugin.getDefault().logError(ex);
		}
		return null;
	}
	
	public static void addQualifiersToInjectionPoint(List<ValuedQualifier> deployed, IInjectionPoint injectionPoint, ICompilationUnit compilationUnit, MultiTextEdit edit){
		try{
			if(injectionPoint instanceof IInjectionPointParameter){
				addQualifiersToParameter(compilationUnit, injectionPoint, deployed, edit);
			}else{
				IJavaElement element = getInjectedJavaElement(compilationUnit, injectionPoint);
				if(element == null || !element.exists())
					return;
				
				// delete unneeded qualifiers
				List<IQualifier> toDelete = findQualifiersToDelete(injectionPoint, deployed);
				
				for(IQualifier qualifier : toDelete){
						deleteAnnotation(qualifier.getSourceType().getFullyQualifiedName(), compilationUnit, element, edit);
				}
				
				for(ValuedQualifier declaration : deployed){
					String qualifierName = declaration.getQualifier().getSourceType().getFullyQualifiedName();
					String value = declaration.getValue();
					if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
						addQualifier(qualifierName, value, compilationUnit, element, edit);
						updateQualifier(qualifierName, value, compilationUnit, element, edit);
					}
				}
			}
		}catch(CoreException ex){
			CDICorePlugin.getDefault().logError(ex);
		}
		
	}
	
	public static void addQualifiersToBean(List<ValuedQualifier> deployed, IBean bean, ICompilationUnit compilationUnit, MultiTextEdit edit){
		IJavaElement beanElement = null;
		if(bean instanceof IBeanField){
			beanElement = ((IBeanField) bean).getField();
		}else if(bean instanceof IBeanMethod){
			beanElement = ((IBeanMethod) bean).getMethod();
		}else{
			beanElement = bean.getBeanClass();
		}
		
		try{
			for(IQualifierDeclaration declaration : bean.getQualifierDeclarations()){
				IQualifier qualifier = declaration.getQualifier();
				String qualifierName = qualifier.getSourceType().getFullyQualifiedName();
				if(!isQualifierNeeded(deployed, qualifier)){
					deleteAnnotation(qualifierName, compilationUnit, beanElement, edit);
				}
			}
			
			for(ValuedQualifier vq : deployed){
				String qualifierName = vq.getQualifier().getSourceType().getFullyQualifiedName();
				String value = vq.getValue();
				String elName = getELName(bean);
				
				if(!value.isEmpty() && (!value.equals(elName) || !qualifierName.equals(CDIConstants.NAMED_QUALIFIER_TYPE_NAME))){
					value = "(\""+value+"\")";
				}else{
					value = "";
				}
				
				if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
					addAnnotation(qualifierName, compilationUnit, beanElement, value, edit);
					updateAnnotation(qualifierName, compilationUnit, beanElement, value, edit);
				}
				
			}
		}catch(CoreException ex){
			CDICorePlugin.getDefault().logError(ex);
		}
		
	}
	
	private static boolean isQualifierNeeded(List<ValuedQualifier> vQualifiers, IQualifier qualifier){
		for(ValuedQualifier vq : vQualifiers){
			if(vq.getQualifier().equals(qualifier))
				return true;
		}
		return false;
	}
	
	private static IJavaElement getInjectedJavaElement(ICompilationUnit compilationUnit, IInjectionPoint injectionPoint){
		if(injectionPoint instanceof IInjectionPointField){
			IField field = ((IInjectionPointField)injectionPoint).getField();
			IType type = field.getDeclaringType();
			IType t = compilationUnit.getType(type.getElementName());
			IField f = t.getField(field.getElementName());
			
			return f;
		}else if(injectionPoint instanceof IInjectionPointParameter){
			IMethod method = ((IInjectionPointParameter)injectionPoint).getBeanMethod().getMethod();
			IType type = method.getDeclaringType();
			IType t = compilationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
			// Why method? Why not Java element for parameter?
			return m;
		}
		return null;
	}
	
	public static boolean checkBeanQualifiers(IBean selectedBean, IBean bean, Set<IQualifier> qualifiers){
		HashSet<ValuedQualifier> valuedQualifiers = new HashSet<ValuedQualifier>();
		for(IQualifier qualifier : qualifiers){
			valuedQualifiers.add(new ValuedQualifier(qualifier));
		}
		return checkValuedQualifiers(selectedBean, bean, valuedQualifiers);
	}
	
	public static boolean checkValuedQualifiers(IBean selectedBean, IBean bean, Set<ValuedQualifier> qualifiers){
		for(ValuedQualifier qualifier : qualifiers){
			if(!isBeanContainQualifier(bean, qualifier)){
				return false;
			}
		}
		if(bean.getQualifiers().size() == qualifiers.size())
			return true;
		return false;
	}
	
	private static boolean isBeanContainQualifier(IBean bean, ValuedQualifier valuedQualifier){
		 
		Set<IQualifier> qualifiers = bean.getQualifiers();
		for(IQualifier q : qualifiers){
			if(q.getSourceType().getFullyQualifiedName().equals(valuedQualifier.getQualifier().getSourceType().getFullyQualifiedName()))
				return true;
		}
		return false;
	}
	
	public static String findQualifierValue(IBean bean, IQualifier qualifier){
		IQualifierDeclaration declaration = findQualifierDeclaration(bean, qualifier);
		if(declaration == null)
			return "";
		
		return findQualifierValue(bean, declaration);
	}
	
	public static String findQualifierValue(IBean bean, IQualifierDeclaration declaration){
		Object value = declaration.getMemberValue(null);
		
		String result =  value == null ? "" : value.toString();
		
		if("".equals(result) && declaration.getQualifier().getSourceType().getFullyQualifiedName().equals(CDIConstants.NAMED_QUALIFIER_TYPE_NAME))
			result = getELName(bean);
		
		return result;
	}
	
	public static IQualifierDeclaration findQualifierDeclaration(IBean bean, IQualifier qualifier){
		Set<IQualifierDeclaration> declarations = bean.getQualifierDeclarations();
		
		if(declarations == null)
			return null;
		
		for(IQualifierDeclaration declaration : declarations){
			if(declaration.getQualifier().getSourceType().getFullyQualifiedName().equals(qualifier.getSourceType().getFullyQualifiedName()))
				return declaration;
		}
		return null;
	}
	
	public static String getELName(IBean bean){
		String name;
		if(bean instanceof IBeanField){
			name = ((IBeanField) bean).getField().getElementName();
		}else if(bean instanceof IBeanMethod){
			name = ((IBeanMethod) bean).getMethod().getElementName();
			if(BeanUtil.isGetter(((IBeanMethod) bean).getMethod())) {
				return BeanUtil.getPropertyName(name);
			}
		}else{
			name = bean.getBeanClass().getElementName();
			if(name.length() > 0) {
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
			}
		}
		
		return name;
	}
}
