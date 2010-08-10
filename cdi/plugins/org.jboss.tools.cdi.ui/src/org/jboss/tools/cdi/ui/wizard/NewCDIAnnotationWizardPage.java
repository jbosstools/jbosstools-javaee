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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreMessages;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUiImages;
import org.jboss.tools.common.ui.widget.editor.CheckBoxFieldEditor;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class NewCDIAnnotationWizardPage extends NewAnnotationWizardPage {
	protected CheckBoxEditorWrapper inherited = null;
	
	protected IFieldEditor target = null;
	
	public NewCDIAnnotationWizardPage() {
		setImageDescriptor(CDIUiImages.getImageDescriptor(CDIUiImages.WELD_WIZARD_IMAGE_PATH));
	}

	protected void createTypeMembers(IType newType, final ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		ISourceRange range = newType.getSourceRange();
		IBuffer buf = newType.getCompilationUnit().getBuffer();		
		String lineDelimiter = StubUtility.getLineDelimiterUsed(newType.getJavaProject());
		StringBuffer sb = new StringBuffer();
		addAnnotations(imports, sb, lineDelimiter);
		buf.replace(range.getOffset(), 0, sb.toString());
	}

	protected abstract void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter);

	protected void addTargetAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter, String[] targets) {
		imports.addImport("java.lang.annotation.Target");
		StringBuffer list = new StringBuffer();
		for (int i = 0; i < targets.length; i++) {
			imports.addImport("static java.lang.annotation.ElementType." + targets[i]);
			if(i > 0) list.append(", ");
			list.append(targets[i]);
		}
		sb.append("@Target( {" + list.toString() + "} )").append(lineDelimiter);
	}
	
	static void addAnnotation(String typeName, ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		int i = typeName.lastIndexOf('.');
		String name = typeName.substring(i + 1);
		imports.addImport(typeName);
		sb.append("@").append(name).append(lineDelimiter);					
	}



	protected void addInheritedAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(inherited != null && inherited.composite.getValue() == Boolean.TRUE) {
			addAnnotation(CDIConstants.INHERITED_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
		}
	}

	protected void addRetentionAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		imports.addImport(CDIConstants.RETENTION_ANNOTATION_TYPE_NAME);
		imports.addImport("static java.lang.annotation.RetentionPolicy.RUNTIME");
		sb.append("@Retention(RUNTIME)").append(lineDelimiter);
	}

	protected void addDocumentedAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addAnnotation("java.lang.annotation.Documented", imports, sb, lineDelimiter);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NONE);

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
//		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
//		createModifierControls(composite, nColumns);

		createCustomFields(composite);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_ANNOTATION_WIZARD_PAGE);
	}

	protected abstract void createCustomFields(Composite composite);

	protected static class CheckBoxEditorWrapper {
		protected IFieldEditor composite = null;
		protected CheckBoxFieldEditor checkBox = null;
	}

	protected CheckBoxEditorWrapper createCheckBoxField(Composite composite, String name, String label, boolean defaultValue) {
		CheckBoxEditorWrapper wrapper = new CheckBoxEditorWrapper();
		wrapper.checkBox = new CheckBoxFieldEditor(name,label,Boolean.valueOf(defaultValue));
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,""), wrapper.checkBox});
		wrapper.composite = editor;
		wrapper.composite.doFillIntoGrid(composite);		
		((Button)wrapper.checkBox.getCheckBoxControl()).setText(label);		
		return wrapper;
	}

	protected void createInheritedField(Composite composite, boolean defaultValue) {
		String label = "Add @Inherited";
		inherited = createCheckBoxField(composite, "isInherited", label, defaultValue);
	}

	protected void createTargetField(Composite composite, List<String> values) {
		target = createComboField("Target", CDIUIMessages.FIELD_EDITOR_TARGET_LABEL, composite, values);
	}

	protected ITaggedFieldEditor createComboField(String name, String label, Composite composite, List<String> values) {
		ITaggedFieldEditor result = IFieldEditorFactory.INSTANCE.createComboEditor(name, label, values, values.get(0));
		((CompositeEditor)result).addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name, "")});
		result.doFillIntoGrid(composite);
		Combo combo = (Combo)result.getEditorControls()[1];
		Object layoutData = combo.getLayoutData();
		if(layoutData instanceof GridData) {
			((GridData)layoutData).horizontalAlignment = GridData.FILL;
		}		
		return result;
	}

	protected String[] getTargets() {
		if(target == null) {
			return new String[]{"TYPE", "METHOD", "PARAMETER", "FIELD"};
		}
		String value = (String)target.getValue();
		String[] vs = value.split(",");
		for (int i = 0; i < vs.length; i++) vs[i] = vs[i].trim();
		return vs;
	}

	public void setInherited(boolean b) {
		if(inherited != null) {
			inherited.composite.setValue(b);
		}
	}

	public void setTarget(String s) {
		if(target != null) {
			((CompositeEditor)target).setValue(s);
		}
	}

	static ICDIProject getCDIProject(IJavaProject jp) {
		CDICoreNature n = getCDINatureWithProgress(jp.getProject());
		return n == null ? null : n.getDelegate();
	}

	public static CDICoreNature getCDINatureWithProgress(final IProject project){
		final CDICoreNature cdiNature = CDICorePlugin.getCDI(project, false);
		if(cdiNature != null && !cdiNature.isStorageResolved()){
			try{
				PlatformUI.getWorkbench().getProgressService().run(false, false, new IRunnableWithProgress(){
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException, InterruptedException {
						monitor.beginTask(CDICoreMessages.CDI_UTIL_BUILD_CDI_MODEL, 10);
						monitor.worked(3);
						cdiNature.resolve();
						monitor.worked(7);
					}
					
				});
			}catch(InterruptedException ie){
				CDICorePlugin.getDefault().logError(ie);
			}catch(InvocationTargetException ite){
				CDICorePlugin.getDefault().logError(ite);
			}
		}
		
		return cdiNature;
	}


}
