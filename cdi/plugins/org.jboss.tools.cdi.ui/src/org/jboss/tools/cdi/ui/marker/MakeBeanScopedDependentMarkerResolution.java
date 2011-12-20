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
package org.jboss.tools.cdi.ui.marker;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;

/**
 * @author Daniel Azarov
 */
public class MakeBeanScopedDependentMarkerResolution implements IMarkerResolution2{
	private String label;
	private IBean bean;
	private IFile file;
	
	public MakeBeanScopedDependentMarkerResolution(IBean bean, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_BEAN_SCOPED_DEPENDENT_MARKER_RESOLUTION_TITLE, new Object[]{bean.getElementName()});
		this.bean = bean;
		this.file = file;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		IAnnotation originalAnnotation = getScopeAnnotation();
		if(originalAnnotation == null)
			return;
		try{
			ICompilationUnit original = EclipseUtil.getCompilationUnit(file);
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());

			CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			change.setEdit(edit);
			
			CDIMarkerResolutionUtils.addImport(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME, compilationUnit, edit);
			
			IAnnotation workingCopyAnnotation = getWorkingCopyAnnotation(originalAnnotation, compilationUnit);
			
			if(workingCopyAnnotation != null){
				String shortName = CDIMarkerResolutionUtils.getShortName(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
				
				TextEdit re = new ReplaceEdit(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), CDIMarkerResolutionUtils.AT+shortName);
				edit.addChild(re);
				
				IBuffer buffer = compilationUnit.getBuffer();
				
				//buffer.replace(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), CDIMarkerResolutionUtils.AT+shortName);
				
				//synchronized(compilationUnit) {
				//	compilationUnit.reconcile(ICompilationUnit.NO_AST, true, null, null);
				//}
				
				// delete import
				String qualifiedName = getFullyQualifiedName();
				if(qualifiedName != null){
					IImportDeclaration importDeclaration = compilationUnit.getImport(qualifiedName);
					IImportContainer importContainer = compilationUnit.getImportContainer();
					if(importDeclaration != null && importContainer != null){
						int importSize = importContainer.getSourceRange().getOffset()+importContainer.getSourceRange().getLength();
						String text = buffer.getText(importSize, buffer.getLength()-importSize);
						if(checkImport(text, originalAnnotation.getElementName())){
							TextEdit de = new DeleteEdit(importDeclaration.getSourceRange().getOffset(), importDeclaration.getSourceRange().getLength());
							edit.addChild(de);
							//importDeclaration.delete(false, new NullProgressMonitor());
						}
					}
				}
			}
			
			if(edit.hasChildren()){
				change.perform(new NullProgressMonitor());
				original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}

	}
	
	private boolean checkImport(String text, String shortName){
		Pattern p = Pattern.compile(".*\\W"+shortName+"\\W.*",Pattern.DOTALL); //$NON-NLS-1$ //$NON-NLS-2$
		Matcher m = p.matcher(text);
		return !m.matches();
	}

	
	private IAnnotation getWorkingCopyAnnotation(IAnnotation annotation, ICompilationUnit compilationUnit){
		IType type = compilationUnit.getType(bean.getBeanClass().getElementName());
		if(type != null){
			return type.getAnnotation(annotation.getElementName());
		}
		return null;
	}
	
	private IAnnotation getScopeAnnotation(){
		Set<IScopeDeclaration> scopDeclarations = bean.getScopeDeclarations();
		Iterator<IScopeDeclaration> iter = scopDeclarations.iterator();
		while(iter.hasNext()){
			IScopeDeclaration declaration = iter.next();
			if(declaration.getJavaAnnotation() != null) {
				return declaration.getJavaAnnotation();
			}
		}
		return null;
	}
	
	private String getFullyQualifiedName(){
		Set<IScopeDeclaration> scopDeclarations = bean.getScopeDeclarations();
		Iterator<IScopeDeclaration> iter = scopDeclarations.iterator();
		while(iter.hasNext()){
			IScopeDeclaration declaration = iter.next();
			return declaration.getScope().getSourceType().getFullyQualifiedName();
		}
		return null;

	}

	@Override
	public String getDescription() {
		return label;
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

}
