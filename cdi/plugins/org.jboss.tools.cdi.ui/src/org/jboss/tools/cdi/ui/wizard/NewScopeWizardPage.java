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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.NewCDIAnnotationWizardPage.CheckBoxEditorWrapper;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewScopeWizardPage extends NewCDIAnnotationWizardPage {	
	protected CheckBoxEditorWrapper normal = null;
	protected CheckBoxEditorWrapper passivating = null;

	public NewScopeWizardPage() {
		setTitle(CDIUIMessages.NEW_SCOPE_WIZARD_PAGE_NAME);
	}

	protected void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addScopeAnnotation(imports, sb, lineDelimiter);
		addInheritedAnnotation(imports, sb, lineDelimiter);
		addTargetAnnotation(imports, sb, lineDelimiter, getTargets());
		addRetentionAnnotation(imports, sb, lineDelimiter);
		addDocumentedAnnotation(imports, sb, lineDelimiter);
	}

	protected String[] getTargets() {
		return new String[]{"TYPE", "METHOD", "FIELD"};
	}

	protected void addScopeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(normal != null) {
			if(normal.composite.getValue() == Boolean.FALSE) {
				addAnnotation(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
			} else if(passivating.composite.getValue() == Boolean.FALSE) {
				addAnnotation(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
			} else {
				String typeName = CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME;
				int i = typeName.lastIndexOf('.');
				String name = typeName.substring(i + 1);
				imports.addImport(typeName);
				sb.append("@").append(name).append("(passivating=true)").append(lineDelimiter);				
			}
		}
	}

	@Override
	protected void createCustomFields(Composite parent) {
		createInheritedField(parent, true);
		createNormalField(parent);
		createPassivatingField(parent);
	}

	protected void createNormalField(Composite composite) {
		String label = "is normal scope";
		normal = createCheckBoxField(composite, "isNormal", label, true);
		normal.checkBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				passivating.checkBox.setEnabled(o != Boolean.FALSE);
			}});
	}

	protected void createPassivatingField(Composite composite) {
		String label = "is passivating";
		passivating = createCheckBoxField(composite, "isPassivating", label, false);
	}

}
