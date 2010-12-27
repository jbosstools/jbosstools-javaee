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

import java.util.HashSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
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
	
	public static void addAnnotation(String qualifiedName, ICompilationUnit compilationUnit, ISourceReference element) throws JavaModelException{
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
		addImport(qualifiedName, compilationUnit);
		
		String lineDelim = SPACE;
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		IAnnotation annotation = getAnnotation(element, CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		
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

}
