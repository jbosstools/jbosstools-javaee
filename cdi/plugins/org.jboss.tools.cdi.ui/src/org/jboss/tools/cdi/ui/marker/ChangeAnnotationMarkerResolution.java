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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class ChangeAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IAnnotation annotation;
	
	private String sourceString = "";
	private String changeString = MarkerResolutionUtils.AT;
	private boolean useBraces = true;
	
	private String[] qualifiedNames = new String[0];
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation){
		this.annotation = annotation;
		
		changeString += annotation.getElementName();
		
		try {
			sourceString = annotation.getSource();
		} catch (JavaModelException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
	}
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation, String parameter){
		this(annotation);
		
		qualifiedNames = new String[]{parameter};
		String shortName = MarkerResolutionUtils.getShortName(parameter);
		
		changeString += "("+shortName+")";
	}
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation, String[] typeNames){
		this(annotation);
		
		this.qualifiedNames = typeNames;
		String[] shortNames = MarkerResolutionUtils.getShortNames(qualifiedNames);
		String totalList = MarkerResolutionUtils.getTotalList(shortNames);
		if(useBraces)
			totalList = MarkerResolutionUtils.OPEN_BRACE+totalList+MarkerResolutionUtils.CLOSE_BRACE;
		
		changeString += "("+totalList+")";
	}
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation, String[] typeNames, boolean useBraces){
		this(annotation, typeNames);
		this.useBraces = useBraces;
	}

	public String getLabel() {
		return NLS.bind(CDIUIMessages.CHANGE_ANNOTATION_MARKER_RESOLUTION_TITLE, sourceString, changeString);
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = MarkerResolutionUtils.getJavaMember(annotation).getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			MarkerResolutionUtils.addImport(CDIConstants.TARGET_ANNOTATION_TYPE_NAME, compilationUnit);
			
			for(String qualifiedName : qualifiedNames){
				MarkerResolutionUtils.addImport(qualifiedName, compilationUnit, true);
			}
			
			IAnnotation workingCopyAnnotation = MarkerResolutionUtils.findWorkingCopy(compilationUnit, annotation);
			
			IBuffer buffer = compilationUnit.getBuffer();
			
			buffer.replace(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), changeString);
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	public String getDescription() {
		return NLS.bind(CDIUIMessages.CHANGE_ANNOTATION_MARKER_RESOLUTION_TITLE, sourceString, changeString);
	}

	public Image getImage() {
		return null;
	}
}
