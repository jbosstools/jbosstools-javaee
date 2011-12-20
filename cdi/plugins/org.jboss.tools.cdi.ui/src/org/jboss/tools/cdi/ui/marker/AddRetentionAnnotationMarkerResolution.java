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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class AddRetentionAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IType type;
	private String label;
	
	public AddRetentionAnnotationMarkerResolution(IType type){
		this.type = type;
		label = NLS.bind(CDIUIMessages.ADD_RETENTION_MARKER_RESOLUTION_TITLE, type.getElementName());
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		try{
			ICompilationUnit original = type.getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
			
			MultiTextEdit edit = new MultiTextEdit();
			
			change.setEdit(edit);
			
			CDIMarkerResolutionUtils.addImport(CDIConstants.RETENTION_POLICY_RUNTIME_TYPE_NAME, compilationUnit, true, edit);
			
			CDIMarkerResolutionUtils.addAnnotation(CDIConstants.RETENTION_ANNOTATION_TYPE_NAME, compilationUnit, type, "(RUNTIME)", edit);
			
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
		return label;
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_ADD;
	}
}
