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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.BaseMarkerResolution;

/**
 * @author Daniel Azarov
 */
public class MakeBeanScopedDependentMarkerResolution extends BaseMarkerResolution {
	private IBean bean;
	private IFile file;
	private IAnnotation annotation;
	
	public MakeBeanScopedDependentMarkerResolution(IBean bean, IFile file){
		this.label = MessageFormat.format(CDIUIMessages.MAKE_BEAN_SCOPED_DEPENDENT_MARKER_RESOLUTION_TITLE, new Object[]{bean.getElementName()});
		this.bean = bean;
		this.file = file;
		annotation = getScopeAnnotation();
		init();
	}
	
	@Override
	protected ICompilationUnit getCompilationUnit(){
		return EclipseUtil.getCompilationUnit(file);
	}
	
	@Override
	protected CompilationUnitChange getChange(ICompilationUnit compilationUnit){
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		if(annotation != null){
			MultiTextEdit edit = new MultiTextEdit();
		
			change.setEdit(edit);
		
			try{
				CDIMarkerResolutionUtils.addImport(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME, compilationUnit, edit);
		
				IAnnotation workingCopyAnnotation = getWorkingCopyAnnotation(annotation, compilationUnit);
		
				if(workingCopyAnnotation != null){
					String shortName = CDIMarkerResolutionUtils.getShortName(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
			
					TextEdit re = new ReplaceEdit(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), CDIMarkerResolutionUtils.AT+shortName);
					edit.addChild(re);
			
					IBuffer buffer = compilationUnit.getBuffer();
			
					// delete import
					String qualifiedName = getFullyQualifiedName();
					if(qualifiedName != null){
						CDIMarkerResolutionUtils.deleteImportForAnnotation(qualifiedName, workingCopyAnnotation, compilationUnit, buffer, edit);
					}
				}
			} catch (JavaModelException e) {
				CDIUIPlugin.getDefault().logError(e);
			}
		}
		
		return change;
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
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

}
