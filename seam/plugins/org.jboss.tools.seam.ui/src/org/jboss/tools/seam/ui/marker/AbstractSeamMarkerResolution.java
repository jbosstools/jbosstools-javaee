/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.marker;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;

/**
 * @author Daniel Azarov
 */
public abstract class AbstractSeamMarkerResolution implements
		IMarkerResolution2 {
	protected String label;
	protected String qualifiedName;

	protected IFile file;
	protected int start, end;
	
	public AbstractSeamMarkerResolution(String label, String qualifiedName, IFile file, int start, int end){
		this.label = label;
		this.qualifiedName = qualifiedName;
		this.file = file;
		this.start = start;
		this.end = end;
	}
	
	protected void deleteAnnotation(){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			IType type = compilationUnit.findPrimaryType();
			if(javaElement != null && type != null){
				if(javaElement instanceof IAnnotatable){
					IAnnotation annotation = findAnnotation(type, (IAnnotatable)javaElement);
					if(annotation != null){
						IBuffer buffer = compilationUnit.getBuffer();
						
						// delete annotation
						buffer.replace(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), "");
						
						// check and delete import
						IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName);
						IImportContainer importContainer = compilationUnit.getImportContainer();
						if(importDeclaration != null && importContainer != null){
							int importSize = importContainer.getSourceRange().getOffset()+importContainer.getSourceRange().getLength();
							String text = buffer.getText(importSize, buffer.getLength()-importSize);
							if(checkImport(text, qualifiedName))
								importDeclaration.delete(false, new NullProgressMonitor());
						}
						compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
					}
				}
			}
			
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	private IType getType(IJavaElement element){
		IJavaElement elem = element;
		while(elem != null){
			if(elem instanceof IType)
				return (IType)elem;
			elem = elem.getParent();
		}
		return null;
	}
	
	private boolean checkImport(String text, String qualifiedName){
		String name = getShortName(qualifiedName);
		
		Pattern p = Pattern.compile(".*\\W"+name+"\\W.*",Pattern.DOTALL); //$NON-NLS-1$ //$NON-NLS-2$
		Matcher m = p.matcher(text);
		return !m.matches();
	}
	
	protected String getShortName(String qualifiedName){
		int lastDot = qualifiedName.lastIndexOf('.');
		String name;
		if(lastDot < 0)
			name = qualifiedName;
		else
			name = qualifiedName.substring(lastDot+1);
		return name;
	}
	
	protected boolean validateComponentName(String value){
		ISeamProject seamProject = getSeamProject();
		Map<String, IStatus> errors = SeamValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(value, seamProject);
		if(errors.isEmpty())
			return true;
		
		return false;
	}
	
	protected ISeamProject getSeamProject(){
		return SeamCorePlugin.getSeamProject(file.getProject(), true);
	}
	
	protected String generateComponentName(String className){
		String componentName = className.toLowerCase();
		if(validateComponentName(componentName))
			return componentName;
		int index = 2;
		String name = componentName;
		while(!validateComponentName(componentName) && index < 100){
			name = componentName+index;
			index++;
		}
		return name;
	}
	
	protected void addAnnotation(String annotationString, boolean insertName){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			IType type = getType(javaElement);
			if(type != null){
				IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName); 
				if(importDeclaration == null || !importDeclaration.exists())
					compilationUnit.createImport(qualifiedName, null, new NullProgressMonitor());
				
				IBuffer buffer = compilationUnit.getBuffer();
				
				String name="";
				if(insertName){
					name="(\""+generateComponentName(compilationUnit.findPrimaryType().getElementName())+"\")";
				}
				
				buffer.replace(type.getSourceRange().getOffset(), 0, annotationString+name+'\n');
				compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			}
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	protected void renameAnnotation(String annotationString){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			IType type = getType(javaElement);
			if(type != null){
				IAnnotation annotation = findAnnotation(type, type);
				if(annotation != null){
					IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName); 
					if(importDeclaration == null || !importDeclaration.exists())
						compilationUnit.createImport(qualifiedName, null, new NullProgressMonitor());
				
					IBuffer buffer = compilationUnit.getBuffer();
					
					String name= "(\""+generateComponentName(compilationUnit.findPrimaryType().getElementName())+"\")";
					
					buffer.replace(annotation.getSourceRange().getOffset(), annotation.getSourceRange().getLength(), annotationString+name);
					compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
				}
			}
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	
	private IAnnotation findAnnotation(IType sourceType, IAnnotatable member) {
		try {
			IAnnotation[] annotations = member.getAnnotations();
			String simpleAnnotationTypeName = qualifiedName;
			int lastDot = qualifiedName.lastIndexOf('.');
			if(lastDot>-1) {
				simpleAnnotationTypeName = simpleAnnotationTypeName.substring(lastDot + 1);
			}
			for (IAnnotation annotation : annotations) {
				if(qualifiedName.equals(annotation.getElementName())) {
					return annotation;
				}
				if(simpleAnnotationTypeName.equals(annotation.getElementName())) {
					String fullAnnotationclassName = EclipseJavaUtil.resolveType(sourceType, simpleAnnotationTypeName);
					if(fullAnnotationclassName!=null) {
						IType annotationType = sourceType.getJavaProject().findType(fullAnnotationclassName);
						if(annotationType!=null && annotationType.getFullyQualifiedName().equals(qualifiedName)) {
							return annotation;
						}
					}
				}
			}
		} catch (JavaModelException e) {
			SeamGuiPlugin.getDefault().logError(e);
		}
		return null;
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
	}

	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}
	
	/**
	 * Returns qualified name for test purpose
	 * @return
	 */
	public String getQualifiedName(){
		return qualifiedName;
	}

}
