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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.MarkerResolutionUtils;
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
			if(javaElement != null){
				CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
				
				MultiTextEdit edit = new MultiTextEdit();
				
				change.setEdit(edit);
				
				MarkerResolutionUtils.deleteAnnotation(qualifiedName, compilationUnit, javaElement, edit);
				
				if(edit.hasChildren()){
					change.perform(new NullProgressMonitor());
					original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
				}
			}
			compilationUnit.discardWorkingCopy();
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
			if(javaElement != null){
				javaElement = compilationUnit.findPrimaryType();
				String param = "";
				if(insertName){
					param= "(\""+generateComponentName(compilationUnit.findPrimaryType().getElementName())+"\")";
				}
				
				CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
				
				MultiTextEdit edit = new MultiTextEdit();
				
				change.setEdit(edit);
				
				MarkerResolutionUtils.addAnnotation(qualifiedName, compilationUnit, javaElement, param, edit);
				
				
				if(edit.hasChildren()){
					change.perform(new NullProgressMonitor());
					original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
				}
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	protected void addAnnotatedMethod(){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			final String lineDelim= compilationUnit.findRecommendedLineSeparator();
			
			IType type = compilationUnit.findPrimaryType();
			if(type != null){
				IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName); 
				if(importDeclaration == null || !importDeclaration.exists())
					compilationUnit.createImport(qualifiedName, null, new NullProgressMonitor());
				
				String annotation = MarkerResolutionUtils.getShortName(qualifiedName);
				String methodName = annotation.toLowerCase();
				
				IMethod oldMethod = type.getMethod(methodName, new String[]{});
				if(oldMethod == null || !oldMethod.exists()){
					StringBuffer buf= new StringBuffer();
					
					buf.append("@"+annotation); //$NON-NLS-1$
					buf.append(lineDelim);
					buf.append("public void "+methodName+"() {"); //$NON-NLS-1$ //$NON-NLS-2$
					buf.append(lineDelim);
					buf.append("}"); //$NON-NLS-1$
					type.createMethod(buf.toString(), null, false, new NullProgressMonitor());
				}else{
					IBuffer buffer = compilationUnit.getBuffer();
					buffer.replace(oldMethod.getSourceRange().getOffset(), 0, "@"+annotation+lineDelim);
				}

				compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
				compilationUnit.discardWorkingCopy();
			}
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}
	
	protected void renameAnnotation(String param, String importName, boolean generate){
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			IJavaElement javaElement = compilationUnit.getElementAt(start);
			if(javaElement != null){
				if(generate){
					param= "(\""+generateComponentName(compilationUnit.findPrimaryType().getElementName())+"\")";
				}
				
				CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
				
				MultiTextEdit edit = new MultiTextEdit();
				
				change.setEdit(edit);
				
				MarkerResolutionUtils.updateAnnotation(qualifiedName, compilationUnit, javaElement, param, edit);
				
				
				if(edit.hasChildren()){
					change.perform(new NullProgressMonitor());
					original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
				}
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
	}

	public String getDescription() {
		return label;
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
