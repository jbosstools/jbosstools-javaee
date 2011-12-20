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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class ChangeAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IAnnotation annotation;
	
	private String sourceString = "";
	private String changeString = CDIMarkerResolutionUtils.AT;
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
		String shortName = CDIMarkerResolutionUtils.getShortName(parameter);
		
		changeString += "("+shortName+")";
	}
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation, String[] typeNames){
		this(annotation);
		
		this.qualifiedNames = typeNames;
		String[] shortNames = CDIMarkerResolutionUtils.getShortNames(qualifiedNames);
		String totalList = CDIMarkerResolutionUtils.getTotalList(shortNames);
		if(useBraces)
			totalList = CDIMarkerResolutionUtils.OPEN_BRACE+totalList+CDIMarkerResolutionUtils.CLOSE_BRACE;
		
		changeString += "("+totalList+")";
	}
	
	public ChangeAnnotationMarkerResolution(IAnnotation annotation, String[] typeNames, boolean useBraces){
		this(annotation, typeNames);
		this.useBraces = useBraces;
	}

	@Override
	public String getLabel() {
		return NLS.bind(CDIUIMessages.CHANGE_ANNOTATION_MARKER_RESOLUTION_TITLE, sourceString, changeString);
	}

	@Override
	public void run(IMarker marker) {
		try{
			ICompilationUnit original = CDIMarkerResolutionUtils.getJavaMember(annotation).getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());

			CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			change.setEdit(edit);
			CDIMarkerResolutionUtils.addImport(CDIConstants.TARGET_ANNOTATION_TYPE_NAME, compilationUnit, edit);
			
			for(String qualifiedName : qualifiedNames){
				CDIMarkerResolutionUtils.addImport(qualifiedName, compilationUnit, true, edit);
			}
			
			IAnnotation workingCopyAnnotation = CDIMarkerResolutionUtils.findWorkingCopy(compilationUnit, annotation);
			
			//IBuffer buffer = compilationUnit.getBuffer();
			TextEdit re = new ReplaceEdit(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), changeString);
			edit.addChild(re);
			
			//buffer.replace(workingCopyAnnotation.getSourceRange().getOffset(), workingCopyAnnotation.getSourceRange().getLength(), changeString);
			
			if(edit.hasChildren()){
				change.perform(new NullProgressMonitor());
				original.reconcile(ICompilationUnit.NO_AST, false, null, new NullProgressMonitor());
			}
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}

	@Override
	public String getDescription() {
		return NLS.bind(CDIUIMessages.CHANGE_ANNOTATION_MARKER_RESOLUTION_TITLE, sourceString, changeString);
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_CHANGE;
	}
}
