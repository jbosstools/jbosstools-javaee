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
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class ChangeTargetAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private String label=null;
	private IType type;
	private IAnnotation annotation;
	private String[] qualifiedNames;
	private String[] shortNames;
	private String totalList;

	
	public ChangeTargetAnnotationMarkerResolution(IType type, IAnnotation annotation, String[] typeNames){
		this.type = type;
		this.annotation = annotation;
		this.qualifiedNames = typeNames;
		shortNames = MarkerResolutionUtils.getShortNames(qualifiedNames);
		totalList = "{"+MarkerResolutionUtils.getTotalList(shortNames)+"}";
		
		try {
			label = NLS.bind(CDIUIMessages.CHANGE_TARGET_MARKER_RESOLUTION_TITLE, annotation.getSource(), totalList);
		} catch (JavaModelException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = type.getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			for(String qualifiedName : qualifiedNames){
				MarkerResolutionUtils.addImport(qualifiedName, compilationUnit, true);
			}
			
			IAnnotation workingCopyAnnotation = MarkerResolutionUtils.findWorkingCopyAnnotation(compilationUnit, type, annotation);
			
			IBuffer buffer = compilationUnit.getBuffer();
			String shortName = MarkerResolutionUtils.getShortName(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
			
			buffer.replace(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), MarkerResolutionUtils.AT+shortName+"("+totalList+")");
			
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
		return null;
	}
}
