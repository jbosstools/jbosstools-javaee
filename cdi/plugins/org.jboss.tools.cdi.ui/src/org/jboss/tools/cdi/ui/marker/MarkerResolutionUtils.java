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
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * @author Daniel Azarov
 */
public class MarkerResolutionUtils {
	public static final String DOT = ".";  //$NON-NLS-1$
	public static final String SPACE = " ";  //$NON-NLS-1$
	public static final String AT = "@";  //$NON-NLS-1$

	static final HashSet<String> primitives = new HashSet<String>();
	static{
		primitives.add("void");  //$NON-NLS-1$
		primitives.add("int");  //$NON-NLS-1$
		primitives.add("java.lang.Integer");  //$NON-NLS-1$
		primitives.add("char");  //$NON-NLS-1$
		primitives.add("java.lang.Character");  //$NON-NLS-1$
		primitives.add("boolean");  //$NON-NLS-1$
		primitives.add("java.lang.Boolean");  //$NON-NLS-1$
		primitives.add("short");  //$NON-NLS-1$
		primitives.add("java.lang.Short");  //$NON-NLS-1$
		primitives.add("long");  //$NON-NLS-1$
		primitives.add("java.lang.Long");  //$NON-NLS-1$
		primitives.add("float");  //$NON-NLS-1$
		primitives.add("java.lang.Float");  //$NON-NLS-1$
		primitives.add("double");  //$NON-NLS-1$
		primitives.add("java.lang.Double");  //$NON-NLS-1$
		primitives.add("byte");  //$NON-NLS-1$
		primitives.add("java.lang.Byte");  //$NON-NLS-1$
		primitives.add("java.lang.String");  //$NON-NLS-1$
	}
	
	public static void addImport(String qualifiedName, ICompilationUnit compilationUnit) throws JavaModelException{
		if(primitives.contains(qualifiedName))
			return;
		
		IPackageDeclaration[] packages = compilationUnit.getPackageDeclarations();
		
		if(qualifiedName.indexOf(DOT) >= 0){
			String typePackage = qualifiedName.substring(0,qualifiedName.lastIndexOf(DOT));
			
			for(IPackageDeclaration packageDeclaration : packages){
				if(packageDeclaration.getElementName().equals(typePackage))
					return;
			}
		}
		
		if(qualifiedName != null){
			IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName); 
			if(importDeclaration == null || !importDeclaration.exists())
				compilationUnit.createImport(qualifiedName, null, new NullProgressMonitor());
		}
		
	}
	
	public static void addAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IType element) throws JavaModelException{
		IAnnotation annotation = getAnnotation(element, qualifiedName);
		if(annotation != null && annotation.exists())
			return;
		
		addImport(qualifiedName, compilationUnit);
		
		String lineDelim = compilationUnit.findRecommendedLineSeparator();
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		buffer.replace(element.getSourceRange().getOffset(), 0, AT+shortName+lineDelim);
		
		synchronized(compilationUnit) {
			compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
		}
	}

	public static void addQualifier(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element) throws JavaModelException{
		IAnnotation annotation = getAnnotation(element, qualifiedName);
		if(annotation != null && annotation.exists())
			return;

		addImport(qualifiedName, compilationUnit);
		
		String lineDelim = SPACE;
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		annotation = getAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		
		buffer.replace(annotation.getSourceRange().getOffset()+annotation.getSourceRange().getLength(), 0, lineDelim+AT+shortName);
		
		synchronized(compilationUnit) {
			compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
		}
	}
	
	public static String getShortName(String qualifiedName){
		int lastDot = qualifiedName.lastIndexOf(DOT);
		String name;
		if(lastDot < 0)
			name = qualifiedName;
		else
			name = qualifiedName.substring(lastDot+1);
		return name;
	}
	
	public static IAnnotation getAnnotation(IJavaElement element, String qualifiedName){
		if(element instanceof IAnnotatable){
			String name = getShortName(qualifiedName);
			IAnnotation annotation = ((IAnnotatable)element).getAnnotation(qualifiedName);
			if (annotation == null || !annotation.exists()) {
				annotation = ((IAnnotatable)element).getAnnotation(name);
			}
			IMember member=null;
			if(element instanceof IMember){
				member = (IMember)element;
			}else if(element instanceof ITypeParameter){
				member = ((ITypeParameter)element).getDeclaringMember();
			}
			if (member != null && annotation != null && qualifiedName.equals(EclipseJavaUtil.resolveType(member.getDeclaringType(), name))) {
				return annotation;
			}
		}
		return null;
	}
	
	private static boolean contains(IQualifierDeclaration declaration, Set<IQualifier> qualifiers){
		for(IQualifier qualifier : qualifiers){
			if(declaration.getQualifier().getSourceType().getFullyQualifiedName().equals(qualifier.getSourceType().getFullyQualifiedName()))
				return true;
		}
		return false;
	}
	
	public static void deleteQualifierAnnotation(ICompilationUnit compilationUnit, IJavaElement element, IQualifier qualifier) throws JavaModelException{
		if(element instanceof IAnnotatable){
			String fullName = qualifier.getSourceType().getFullyQualifiedName();
			String shortName = getShortName(fullName);
			IAnnotation annotation = ((IAnnotatable)element).getAnnotation(fullName);
			if(annotation == null || !annotation.exists()){
				annotation = ((IAnnotatable)element).getAnnotation(shortName);
			}
			if(annotation != null && annotation.exists()){
				IBuffer buffer = compilationUnit.getBuffer();
				
				buffer.replace(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), "");
				
				synchronized(compilationUnit) {
					compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
				}
			}
		}
	}

	private static List<IQualifier> findQualifiersToDelete(IInjectionPoint injectionPoint, Set<IQualifier> qualifiers){
		ArrayList<IQualifier> list = new ArrayList<IQualifier>();
		Set<IQualifierDeclaration> declarations = injectionPoint.getQualifierDeclarations();
		for(IQualifierDeclaration declaration : declarations){
			if(!contains(declaration, qualifiers))
				list.add(declaration.getQualifier());
		}
		return list;
	}
	
	public static void addQualifiersToInjectedPoint(IInjectionPoint injectionPoint, IBean bean){
		try{
			ICompilationUnit original = injectionPoint.getClassBean().getBeanClass().getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			IJavaElement element = getInjectedJavaElement(compilationUnit, injectionPoint);
			Set<IQualifier> qualifiers = bean.getQualifiers();
			
			// delete unneeded qualifiers
			
			List<IQualifier> toDelete = findQualifiersToDelete(injectionPoint, qualifiers);
			
			for(IQualifier qualifier : toDelete){
					deleteQualifierAnnotation(compilationUnit, element, qualifier);
			}
			
			for(IQualifier qualifier : qualifiers){
				String qualifierName = qualifier.getSourceType().getFullyQualifiedName();
				if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
					MarkerResolutionUtils.addQualifier(qualifierName, compilationUnit, element);
				}
			}

			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	public static void addQualifiersToBean(List<IQualifier> deployed, IBean bean){
		IFile file = (IFile)bean.getBeanClass().getResource();
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IType type = compilationUnit.findPrimaryType();
			if(type != null){
				for(IQualifier qualifier : deployed){
					String qualifierName = qualifier.getSourceType().getFullyQualifiedName();
					if(!qualifierName.equals(CDIConstants.ANY_QUALIFIER_TYPE_NAME) && !qualifierName.equals(CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME)){
						MarkerResolutionUtils.addAnnotation(qualifier.getSourceType().getFullyQualifiedName(), compilationUnit, type);
					}
					
				}
			}
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	public static IJavaElement getInjectedJavaElement(ICompilationUnit compolationUnit, IInjectionPoint injectionPoint){
		if(injectionPoint instanceof IInjectionPointField){
			IField field = ((IInjectionPointField)injectionPoint).getField();
			IType type = field.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IField f = t.getField(field.getElementName());
			
			return f;
		}else if(injectionPoint instanceof IInjectionPointMethod){
			IMethod method = ((IInjectionPointMethod)injectionPoint).getMethod();
			IType type = method.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
			
			return m;
		}else if(injectionPoint instanceof IInjectionPointParameter){
			String paramName = ((IInjectionPointParameter)injectionPoint).getName();
			IMethod method =  ((IInjectionPointParameter)injectionPoint).getBeanMethod().getMethod();
			IType type = method.getDeclaringType();
			IType t = compolationUnit.getType(type.getElementName());
			IMethod m = t.getMethod(method.getElementName(), method.getParameterTypes());
			ITypeParameter p = m.getTypeParameter(paramName);
			
			return p;
		}
		return null;
	}


}
