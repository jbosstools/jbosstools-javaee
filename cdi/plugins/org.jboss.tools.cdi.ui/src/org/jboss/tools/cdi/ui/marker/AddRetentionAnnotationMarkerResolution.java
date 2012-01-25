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
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MultiTextEdit;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.internal.core.refactoring.CDIMarkerResolutionUtils;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.refactoring.BaseMarkerResolution;

public class AddRetentionAnnotationMarkerResolution extends BaseMarkerResolution {
	private IType type;
	
	public AddRetentionAnnotationMarkerResolution(IType type){
		this.type = type;
		label = NLS.bind(CDIUIMessages.ADD_RETENTION_MARKER_RESOLUTION_TITLE, type.getElementName());
		init();
	}

	@Override
	protected ICompilationUnit getCompilationUnit(){
		return type.getCompilationUnit();
	}
	
	@Override
	protected CompilationUnitChange getChange(ICompilationUnit compilationUnit){
		CompilationUnitChange change = new CompilationUnitChange("", compilationUnit);
		
		MultiTextEdit edit = new MultiTextEdit();
		
		change.setEdit(edit);
		try{
			CDIMarkerResolutionUtils.addImport(CDIConstants.RETENTION_POLICY_RUNTIME_TYPE_NAME, compilationUnit, true, edit);
			
			CDIMarkerResolutionUtils.addAnnotation(CDIConstants.RETENTION_ANNOTATION_TYPE_NAME, compilationUnit, type, "(RUNTIME)", edit);
		} catch (JavaModelException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
		return change;
	}
	
	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_ADD;
	}
}
