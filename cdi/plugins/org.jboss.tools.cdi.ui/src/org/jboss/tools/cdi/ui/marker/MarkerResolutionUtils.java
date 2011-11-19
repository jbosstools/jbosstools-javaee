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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanField;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.util.BeanUtil;

/**
 * @author Daniel Azarov
 */
public class MarkerResolutionUtils {
	public static final String DOT = ".";  //$NON-NLS-1$
	public static final String COMMA = ",";  //$NON-NLS-1$
	public static final String SEMICOLON = ";";  //$NON-NLS-1$
	public static final String SPACE = " ";  //$NON-NLS-1$
	public static final String AT = "@";  //$NON-NLS-1$
	public static final String IMPLEMENTS = "implements";  //$NON-NLS-1$
	public static final String IMPORT = "import";  //$NON-NLS-1$
	public static final String EXTENDS = "extends";  //$NON-NLS-1$
	public static final String OPEN_BRACE = "{"; //$NON-NLS-1$
	public static final String CLOSE_BRACE = "}"; //$NON-NLS-1$

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
	
	/**
	 * 
	 * @param qualifiedName
	 * @param compilationUnit
	 * @return true if there is import in compilation unit with the same short name
	 * @throws JavaModelException
	 */
	public static boolean addImport(String qualifiedName, ICompilationUnit compilationUnit) throws JavaModelException{
		return addImport(qualifiedName, compilationUnit, false, null);
	}
	
	public static boolean addImport(String qualifiedName, ICompilationUnit compilationUnit, MultiTextEdit rootEdit) throws JavaModelException{
		return addImport(qualifiedName, compilationUnit, false, rootEdit);
	}
	
	/**
	 * 
	 * @param qualifiedName
	 * @param compilationUnit
	 * @param staticFlag
	 * @return true if there is import in compilation unit with the same short name
	 * @throws JavaModelException
	 */
	public static boolean addImport(String qualifiedName, ICompilationUnit compilationUnit, boolean staticFlag) throws JavaModelException{
		return addImport(qualifiedName, compilationUnit, staticFlag, null);
	}
	
	public static boolean addImport(String qualifiedName, ICompilationUnit compilationUnit, boolean staticFlag, MultiTextEdit rootEdit) throws JavaModelException{
		if(primitives.contains(qualifiedName))
			return false;
		
		if(qualifiedName != null){
			String shortName = getShortName(qualifiedName);
			
			IPackageDeclaration[] packages = compilationUnit.getPackageDeclarations();
			
			// local classes do not need to be imported
			if(qualifiedName.indexOf(DOT) >= 0){
				String typePackage = qualifiedName.substring(0,qualifiedName.lastIndexOf(DOT));
				
				for(IPackageDeclaration packageDeclaration : packages){
					if(packageDeclaration.getElementName().equals(typePackage))
						return false;
				}
				
				for(IPackageDeclaration packageDeclaration : packages){
					IType type = compilationUnit.getJavaProject().findType(packageDeclaration.getElementName()+DOT+shortName);
					if(type != null && type.exists())
						return true;
				}
			}
		
			IImportDeclaration[] importDeclarations = compilationUnit.getImports(); 
			
			for(IImportDeclaration importDeclaration : importDeclarations){
				String importName = importDeclaration.getElementName();
				String elementShort = getShortName(importName);
				if(importDeclaration.isOnDemand()){
					int importLastDot = importName.lastIndexOf(DOT);
					if(importLastDot == -1) return false; // invalid import declaration
					int elementLastDot = qualifiedName.lastIndexOf(DOT);
					if(elementLastDot == -1) return false; // invalid import declaration
					
					if(qualifiedName.substring(0, elementLastDot).equals(importName.substring(0, importLastDot)))
						return false;
				}
				
				if(importName.equals(qualifiedName))
					return false;
				if(elementShort.equals(shortName))
					return true;
				
			}
			if(rootEdit == null){
				if(staticFlag){
					compilationUnit.createImport(qualifiedName, null, Flags.AccStatic, new NullProgressMonitor());
				}else{
					compilationUnit.createImport(qualifiedName, null, new NullProgressMonitor());
				}
			}else{
				String text = compilationUnit.findRecommendedLineSeparator()+IMPORT+SPACE+qualifiedName+SEMICOLON;
				if(!isDuplicate(rootEdit, text)){
					TextEdit edit = new InsertEdit(compilationUnit.getImportContainer().getSourceRange().getOffset()+compilationUnit.getImportContainer().getSourceRange().getLength(), text);
					rootEdit.addChild(edit);
				}
			}
		}
		return false;
	}
	
	private static boolean isDuplicate(MultiTextEdit rootEdit, String text){
		for(TextEdit edit : rootEdit.getChildren()){
			if(edit instanceof InsertEdit && ((InsertEdit) edit).getText().equals(text))
				return true;
		}
		return false;
	}
	
	public static void addAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element) throws JavaModelException{
		addAnnotation(qualifiedName, compilationUnit, element, "");
	}
	public static void updateAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element, String params) throws JavaModelException{
		updateAnnotation(qualifiedName, compilationUnit, element, params, null);
	}
	
	public static void updateAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element, String params, MultiTextEdit rootEdit) throws JavaModelException{
		IJavaElement workingCopyElement = findWorkingCopy(compilationUnit, element);
		if(workingCopyElement == null){
			return;
		}
		
		if(!(workingCopyElement instanceof IMember))
			return;
		
		IMember workingCopyMember = (IMember) workingCopyElement;
		
		IAnnotation annotation = findAnnotation(workingCopyMember, qualifiedName);
		if(annotation == null || !annotation.exists())
			return;
		
		boolean duplicateShortName = addImport(qualifiedName, compilationUnit, null);
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		if(duplicateShortName)
			shortName = qualifiedName;
		
		String newValue = AT+shortName+params;
		
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

	public static void addAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element, String params) throws JavaModelException{
		addAnnotation(qualifiedName, compilationUnit, element, params, null);
	}
	
	public static void addAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element, String params, MultiTextEdit rootEdit) throws JavaModelException{
		IJavaElement workingCopyElement = findWorkingCopy(compilationUnit, element);
		if(workingCopyElement == null){
			return;
		}
		
		if(!(workingCopyElement instanceof IMember))
			return;
		
		IMember workingCopyMember = (IMember) workingCopyElement;
		
		IAnnotation annotation = findAnnotation(workingCopyMember, qualifiedName);
		if(annotation != null && annotation.exists())
			return;
		
		boolean duplicateShortName = addImport(qualifiedName, compilationUnit, rootEdit);
		
		IBuffer buffer = compilationUnit.getBuffer();
		String shortName = getShortName(qualifiedName);
		
		if(duplicateShortName)
			shortName = qualifiedName;
		
		String str = AT+shortName+params;
		
		if(workingCopyMember instanceof IType){
			str += compilationUnit.findRecommendedLineSeparator();
		}else{
			str += SPACE;
		}
		
		if(rootEdit != null){
			TextEdit edit = new InsertEdit(workingCopyMember.getSourceRange().getOffset(), str);
			rootEdit.addChild(edit);
		}else{
			buffer.replace(workingCopyMember.getSourceRange().getOffset(), 0, str);
			
			synchronized(compilationUnit) {
				compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
			}
		}
		
		
		
	}

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
	
	public static String getShortName(String qualifiedName){
		int lastDot = qualifiedName.lastIndexOf(DOT);
		String name;
		if(lastDot < 0)
			name = qualifiedName;
		else
			name = qualifiedName.substring(lastDot+1);
		return name;
	}
	
	public static String getPackageName(String qualifiedName){
		int lastDot = qualifiedName.lastIndexOf(DOT);
		String name;
		if(lastDot < 0)
			name = "";
		else
			name = qualifiedName.substring(0, lastDot);
		return name;
	}
	
	public static String[] getShortNames(String[] qualifiedNames){
		String[] shortNames = new String[qualifiedNames.length];
		for(int i = 0; i < qualifiedNames.length; i++){
			shortNames[i] = getShortName(qualifiedNames[i]);
		}
		return shortNames;
	}
	
	public static String getTotalList(String[] names){
		String list = "";
		for(int i = 0; i < names.length; i++){
			if(i != 0)
				list += ", ";
			list += names[i];
		}
		return list;
	}
	
	public static IAnnotation findAnnotation(IJavaElement element, String qualifiedName){
		if(element instanceof IAnnotatable){
			String name = getShortName(qualifiedName);
			IAnnotation annotation = ((IAnnotatable)element).getAnnotation(qualifiedName);
			if (!annotation.exists()) {
				annotation = ((IAnnotatable)element).getAnnotation(name);
			} else {
				return annotation;
			}
			if(annotation.exists()) {
				IType type=null;
				if(element instanceof IType){
					type = (IType)element;
				}else if(element instanceof IMember){
					type = ((IMember)element).getDeclaringType();
				}else if(element instanceof ITypeParameter){
					type = ((ITypeParameter)element).getDeclaringMember().getDeclaringType();
				}else if(element instanceof ILocalVariable){
					type = ((ILocalVariable)element).getDeclaringMember().getDeclaringType();
				}
				if (type != null && annotation != null && qualifiedName.equals(EclipseJavaUtil.resolveType(type, name))) {
					return annotation;
				}
			}
		}
		return null;
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
			CDIUIPlugin.getDefault().logError(ex);
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
			CDIUIPlugin.getDefault().logError(ex);
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
			CDIUIPlugin.getDefault().logError(ex);
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
			CDIUIPlugin.getDefault().logError(ex);
		}
		
	}
	
	private static boolean isQualifierNeeded(List<ValuedQualifier> vQualifiers, IQualifier qualifier){
		for(ValuedQualifier vq : vQualifiers){
			if(vq.getQualifier().equals(qualifier))
				return true;
		}
		return false;
	}
	
	public static void addInterfaceToClass(ICompilationUnit compilationUnit, IType type, String qualifiedName) throws JavaModelException{
		String shortName = getShortName(qualifiedName);
		
		IType[] types = compilationUnit.getTypes();
		IType workingType = null;
		for(IType t : types){
			if(t.getElementName().equals(type.getElementName())){
				workingType = t;
				break;
			}
		}
		
		if(workingType != null){
			addImport(qualifiedName, compilationUnit, null);
			
			IBuffer buffer = compilationUnit.getBuffer();
			
			String text = buffer.getText(workingType.getSourceRange().getOffset(), workingType.getSourceRange().getLength());
			
			int namePosition = text.indexOf(workingType.getElementName());
			if(namePosition >= 0){
				int implementsPosition = text.indexOf(IMPLEMENTS,namePosition);
				if(implementsPosition > 0){
					buffer.replace(workingType.getSourceRange().getOffset()+implementsPosition+IMPLEMENTS.length(),0,SPACE+shortName+COMMA);
				}else{
					int extedsPosition = text.indexOf(EXTENDS,namePosition);
					if(extedsPosition > 0){
						int bracePosition = text.indexOf(OPEN_BRACE, extedsPosition);
						String str = IMPLEMENTS+SPACE+shortName+SPACE;
						if(!text.substring(bracePosition-1,bracePosition).equals(SPACE))
							str = SPACE+str;
						buffer.replace(workingType.getSourceRange().getOffset()+bracePosition,0,str);
					}else{
						buffer.replace(workingType.getSourceRange().getOffset()+namePosition+workingType.getElementName().length(),0,SPACE+IMPLEMENTS+SPACE+shortName);
					}
				}
			}
		}

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
	
	@SuppressWarnings("unchecked")
	public static <T extends IJavaElement> T findWorkingCopy(ICompilationUnit compilationUnit, T element) throws JavaModelException{
		if(element instanceof IAnnotation){
			IJavaElement parent = findWorkingCopy(compilationUnit, element.getParent());
			if(parent instanceof IAnnotatable){
				for(IAnnotation a : ((IAnnotatable)parent).getAnnotations()){
					if(a.getElementName().equals(element.getElementName()))
						return (T)a;
				}
			}
		}else if(element instanceof ILocalVariable && ((ILocalVariable) element).isParameter()){
			IJavaElement parent = findWorkingCopy(compilationUnit, element.getParent());
			if(parent instanceof IMethod){
				for(ILocalVariable parameter : ((IMethod)parent).getParameters()){
					if(parameter.getElementName().equals(element.getElementName()) && parameter.getTypeSignature().equals(((ILocalVariable)element).getTypeSignature()))
						return (T)parameter;
				}
			}
		}else{
			IJavaElement[] elements = compilationUnit.findElements(element);
			if(elements != null){
				for(IJavaElement e : elements){
					if(e.getClass().equals(element.getClass()))
						return (T)e;
				}
			}
		}
		return null;
	}
	
	public static void deleteAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element) throws JavaModelException{
		deleteAnnotation(qualifiedName, compilationUnit, element, null);
	}
	
	public static void deleteAnnotation(String qualifiedName, ICompilationUnit compilationUnit, IJavaElement element, MultiTextEdit rootEdit) throws JavaModelException{
		IJavaElement workingCopyElement = findWorkingCopy(compilationUnit, element);
		if(workingCopyElement == null){
			return;
		}
		
		IAnnotation annotation = findAnnotation(workingCopyElement, qualifiedName);
		if(annotation != null){
			IBuffer buffer = compilationUnit.getBuffer();
			
			int position = annotation.getSourceRange().getOffset() + annotation.getSourceRange().getLength();
			int numberOfSpaces = 0;
			if(position < buffer.getLength()-1){
				char c = buffer.getChar(position);
				while((c == ' ' || c == '\t' || c == '\n' || c == '\r') && position < buffer.getLength()-1){
					numberOfSpaces++;
					position++;
					c = buffer.getChar(position);
				}
			}
			
			// delete annotation
			if(rootEdit != null){
				TextEdit edit = new DeleteEdit(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength());
				rootEdit.addChild(edit);
			}else{
				buffer.replace(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength()+numberOfSpaces, "");
			}
			
			// check and delete import
			IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName);
			IImportContainer importContainer = compilationUnit.getImportContainer();
			if(importDeclaration.exists() && importContainer.exists()){
				int importSize = importContainer.getSourceRange().getOffset()+importContainer.getSourceRange().getLength();
				
					if(rootEdit != null){
						int annotationStart = annotation.getSourceRange().getOffset();
						int annotationEnd = annotationStart+annotation.getSourceRange().getLength();
						String textBefore = buffer.getText(importSize, annotationStart-importSize);
						String textAfter = buffer.getText(annotationEnd, buffer.getLength()-annotationEnd);
						if(checkImport(textBefore, qualifiedName) && checkImport(textAfter, qualifiedName)){
							TextEdit edit = new DeleteEdit(importDeclaration.getSourceRange().getOffset(), importDeclaration.getSourceRange().getLength());
							rootEdit.addChild(edit);
						}
					}else{
						String text = buffer.getText(importSize, buffer.getLength()-importSize);
						if(checkImport(text, qualifiedName)){
							importDeclaration.delete(false, new NullProgressMonitor());
						}
					}
			}
			
			if(rootEdit == null){
				synchronized(compilationUnit) {
					compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
				}
			}
		}
	}
	
	private static boolean checkImport(String text, String qualifiedName){
		String name = getShortName(qualifiedName);
		
		Pattern p = Pattern.compile(".*\\W"+name+"\\W.*",Pattern.DOTALL); //$NON-NLS-1$ //$NON-NLS-2$
		Matcher m = p.matcher(text);
		return !m.matches();
	}
	
	public static IMember getJavaMember(IJavaElement element){
		while(element != null){
			if(element instanceof IMember)
				return (IMember)element;
			element = element.getParent();
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
		
		return findQualifierValue(declaration);
	}
	
	public static String findQualifierValue(IQualifierDeclaration declaration){
		Object value = declaration.getMemberValue(null);
		
		return value == null ? "" : value.toString();
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
