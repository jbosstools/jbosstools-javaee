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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.CDIUiImages;

public class AddAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IJavaElement element;
	private String qualifiedName;
	private String label;
	
	public AddAnnotationMarkerResolution(IJavaElement element, String qualifiedName){
		this.element = element;
		this.qualifiedName = qualifiedName;
		String shortName = MarkerResolutionUtils.getShortName(qualifiedName);
		String type = "";
		if(element instanceof IType){
			type = "class";
		}else if(element instanceof IMethod){
			type = "method";
		}else if(element instanceof IField){
			type = "field";
		}else if(element instanceof ILocalVariable && ((ILocalVariable) element).isParameter()){
			type = "parameter of '"+element.getParent().getElementName()+"' method";
		}
			
		label = NLS.bind(CDIUIMessages.ADD_ANNOTATION_MARKER_RESOLUTION_TITLE, new String[]{shortName, element.getElementName(), type});
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = MarkerResolutionUtils.getJavaMember(element).getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			MarkerResolutionUtils.addAnnotation(qualifiedName, compilationUnit, element);
			
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
		return CDIUiImages.QUICKFIX_ADD;
	}
}
