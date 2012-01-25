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

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.refactoring.BaseMarkerResolution;

public class DeleteAnnotationMarkerResolution extends BaseMarkerResolution {
	private IJavaElement element;
	private String qualifiedName;
	
	public DeleteAnnotationMarkerResolution(IJavaElement element, String qualifiedName){
		this.element = element;
		this.qualifiedName = qualifiedName;
		String shortName = CDIMarkerResolutionUtils.getShortName(qualifiedName);
		String type = "";
		if(element instanceof IType){
			try {
				if(((IType) element).isAnnotation())
					type = CDIUIMessages.CDI_QUICK_FIXES_ANNOTATION;
				else if(((IType) element).isInterface())
					type = CDIUIMessages.CDI_QUICK_FIXES_INTERFACE;
				else if(((IType) element).isClass())
					type = CDIUIMessages.CDI_QUICK_FIXES_CLASS;
				else
					type = CDIUIMessages.CDI_QUICK_FIXES_TYPE;
			} catch (JavaModelException ex) {
				CDIUIPlugin.getDefault().logError(ex);
			}
		}else if(element instanceof IMethod){
			type = CDIUIMessages.CDI_QUICK_FIXES_METHOD;
		}else if(element instanceof IField){
			type = CDIUIMessages.CDI_QUICK_FIXES_FIELD;
		}else if(element instanceof ILocalVariable && ((ILocalVariable) element).isParameter()){
			type = NLS.bind(CDIUIMessages.CDI_QUICK_FIXES_PARAMETER, element.getParent().getElementName());
		}
			
		label = NLS.bind(CDIUIMessages.DELETE_ANNOTATION_MARKER_RESOLUTION_TITLE, new String[]{shortName, element.getElementName(), type});
		init();
	}
	
	@Override
	protected ICompilationUnit getCompilationUnit(){
		return CDIMarkerResolutionUtils.getJavaMember(element).getCompilationUnit();
	}

	@Override
	protected CompilationUnitChange getChange(ICompilationUnit compilationUnit){
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		
		MultiTextEdit edit = new MultiTextEdit();
		
		change.setEdit(edit);
		try{
			CDIMarkerResolutionUtils.deleteAnnotation(qualifiedName, compilationUnit, element, edit);
		} catch (JavaModelException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
		
		return change;
	}
	
	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_REMOVE;
	}
}
