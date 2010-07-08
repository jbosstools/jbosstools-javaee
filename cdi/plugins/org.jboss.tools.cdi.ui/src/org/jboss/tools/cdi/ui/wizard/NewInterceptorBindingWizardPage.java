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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewInterceptorBindingWizardPage extends NewCDIAnnotationWizardPage {
	protected InterceptorBindingSelectionProvider interceptorBindingsProvider = new InterceptorBindingSelectionProvider();
	protected ListFieldEditor interceptorBindings = null;
	

	public NewInterceptorBindingWizardPage() {
		setTitle(CDIUIMessages.NEW_INTERCEPTOR_BINDING_WIZARD_PAGE_NAME);
	}

	protected void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addInterceptorBindingAnnotation(imports, sb, lineDelimiter);
		addInheritedAnnotation(imports, sb, lineDelimiter);
		addInterceptorBindingAnnotations(imports, sb, lineDelimiter);
		addTargetAnnotation(imports, sb, lineDelimiter, getTargets());
		addRetentionAnnotation(imports, sb, lineDelimiter);
		addDocumentedAnnotation(imports, sb, lineDelimiter);
	}

	protected void addInterceptorBindingAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addAnnotation(CDIConstants.INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
	}

	@SuppressWarnings("unchecked")
	protected void addInterceptorBindingAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(interceptorBindings != null) {
			List list = (List)interceptorBindings.getValue();
			for (Object o: list) {
				if(o instanceof ICDIAnnotation) {
					ICDIAnnotation a = (ICDIAnnotation)o;
					String typeName = a.getSourceType().getFullyQualifiedName();
					addAnnotation(typeName, imports, sb, lineDelimiter);
				}
			}
		}
	}

	@Override
	protected void createCustomFields(Composite composite) {
		createInheritedField(composite, true);
		createTargetField(composite);
		createInterceptorBindingField(composite);
	}

	protected void createTargetField(Composite composite) {
		List<String> targetOptions = new ArrayList<String>();
		targetOptions.add("TYPE,METHOD");
		targetOptions.add("TYPE");
		createTargetField(composite, targetOptions);
	}

	protected void createInterceptorBindingField(Composite composite) {
		interceptorBindings = new ListFieldEditor("interceptorBindings", CDIUIMessages.FIELD_EDITOR_INTERCEPTOR_BINDINGS_LABEL, new ArrayList<Object>());
		interceptorBindings.setProvider(interceptorBindingsProvider);
		interceptorBindingsProvider.setEditorField(interceptorBindings);
		interceptorBindings.doFillIntoGrid(composite);
		setInterceptorBindings(getPackageFragmentRoot());
	}

	public void setPackageFragmentRoot(IPackageFragmentRoot root, boolean canBeModified) {
		super.setPackageFragmentRoot(root, canBeModified);
		setInterceptorBindings(root);
	}

	void setInterceptorBindings(IPackageFragmentRoot root) {
		interceptorBindingsProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = getCDIProject(jp);
			if(cdi != null) {
				interceptorBindingsProvider.setProject(cdi);
			}
		}
	}
	
}
