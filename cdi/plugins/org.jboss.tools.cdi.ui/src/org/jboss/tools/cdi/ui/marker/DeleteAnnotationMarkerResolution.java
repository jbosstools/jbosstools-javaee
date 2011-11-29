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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.MarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class DeleteAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IJavaElement element;
	private String qualifiedName;
	private String label;
	
	public DeleteAnnotationMarkerResolution(IJavaElement element, String qualifiedName){
		this.element = element;
		this.qualifiedName = qualifiedName;
		String shortName = MarkerResolutionUtils.getShortName(qualifiedName);
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
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = MarkerResolutionUtils.getJavaMember(element).getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			MarkerResolutionUtils.deleteAnnotation(qualifiedName, compilationUnit, element);
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return CDIImages.QUICKFIX_REMOVE;
	}
}
