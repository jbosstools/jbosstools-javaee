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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewInterceptorBindingWizardPage extends NewCDIAnnotationWizardPage {
	protected InterceptorBindingSelectionProvider interceptorBindingsProvider = new InterceptorBindingSelectionProvider();
	protected ListFieldEditor interceptorBindings = null;
	
	protected StatusInfo targetStatus = new StatusInfo();

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
		target.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				validateTargetAndInterceptorBinding();
			}});
	}

	protected void createInterceptorBindingField(Composite composite) {
		interceptorBindings = new ListFieldEditor("interceptorBindings", CDIUIMessages.FIELD_EDITOR_INTERCEPTOR_BINDINGS_LABEL, new ArrayList<Object>());
		interceptorBindings.setProvider(interceptorBindingsProvider);
		interceptorBindingsProvider.setEditorField(interceptorBindings);
		interceptorBindings.doFillIntoGrid(composite);
		setInterceptorBindings(getPackageFragmentRoot());
		interceptorBindings.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				validateTargetAndInterceptorBinding();
			}});
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
	
	void validateTargetAndInterceptorBinding() {
		try {
			getTargetAndInterceptorBindingError();
		} catch (JavaModelException e) {
			CDIUIPlugin.getDefault().logError(e);
		}
		doStatusUpdate();
	}
	
	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status= new IStatus[] {
			fContainerStatus,
			isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus,
			fTypeNameStatus,
			fModifierStatus,
			fSuperClassStatus,
			fSuperInterfacesStatus
		};

		// the mode severe status will be displayed and the OK button enabled/disabled.
		updateStatus(status);
	}

	protected void updateStatus(IStatus[] status) {
		IStatus[] ns = new IStatus[status.length + 1];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = targetStatus;
		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}	

	void getTargetAndInterceptorBindingError() throws JavaModelException {
		targetStatus = new StatusInfo();
		if(interceptorBindings != null && target != null) {
			String value = (String)target.getValue();
			boolean hasMethodOrField = value != null && (value.indexOf("METHOD") >= 0 || value.indexOf("FIELD") >= 0);
			List list = (List)interceptorBindings.getValue();
			for (Object o: list) {
				if(o instanceof IInterceptorBinding) {
					IInterceptorBinding a = (IInterceptorBinding)o;
					IAnnotationDeclaration target = a.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
					if(target != null) {
						Set<String> targets = CDIUtil.getTargetAnnotationValues(target);
						if(targets != null && targets.size() == 1 && targets.contains("TYPE") && hasMethodOrField) {
							String message = NLS.bind(CDIUIMessages.MESSAGE_INTERCEPTOR_BINDING_IS_NOT_COMPATIBLE, a.getSourceType().getElementName());
//							String message = a.getSourceType().getElementName() + " annotated with @Target({TYPE}) is not compatible with target";
							targetStatus.setWarning(message);
						}
						//targets always contain TYPE
					}
				}
			}
		}
	}

	public void addInterceptorBinding(IInterceptorBinding s) {
		List vs = (List)interceptorBindings.getValue();
		List nvs = new ArrayList();
		if(vs != null) nvs.addAll(vs);
		nvs.add(s);
		interceptorBindings.setValue(nvs);
	}

}
