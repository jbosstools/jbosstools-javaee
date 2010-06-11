/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.ui.CDIUIMessages;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewQualifierWizardPage extends NewCDIAnnotationWizardPage {	

	public NewQualifierWizardPage() {
		setTitle(CDIUIMessages.NEW_QUALIFIER_WIZARD_PAGE_NAME);
	}

	protected void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addQualifierAnnotation(imports, sb, lineDelimiter);
		addInheritedAnnotation(imports, sb, lineDelimiter);
		addTargetAnnotation(imports, sb, lineDelimiter, getTargets());
		addRetentionAnnotation(imports, sb, lineDelimiter);
		addDocumentedAnnotation(imports, sb, lineDelimiter);
	}

	protected void addQualifierAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addAnnotation(CDIConstants.QUALIFIER_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
	}

	@Override
	protected void createCustomFields(Composite parent) {
		createInheritedField(parent, false);
		
	}

}
