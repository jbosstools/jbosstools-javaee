/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.batch.ui.internal.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.core.IBatchProject;
import org.jboss.tools.batch.ui.JobImages;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.util.BeanUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBatchArtifactWizardPage extends NewClassWizardPage implements PropertyChangeListener {
	protected String defaultTypeName = null;

	IFieldEditor artifacts;
	IFieldEditor derivedFrom;

	IFieldEditor nameOptions;
	IFieldEditor name;
	
	IFieldEditor properties;

	protected StatusInfo artifactNameStatus = new StatusInfo();
	protected StatusInfo propertiesStatus = new StatusInfo();

	public NewBatchArtifactWizardPage() {
		setTitle(WizardMessages.NEW_BATCH_ARTIFACT_WIZARD_PAGE_NAME);
		setDescription(WizardMessages.NEW_BATCH_ARTIFACT_WIZARD_DESCRIPTION);
		setImageDescriptor(JobImages.getImageDescriptor(JobImages.NEW_ARTIFACT_IMAGE));
	}

	/**
	 * Presets properties to artifact.
	 * @param propertyNames
	 */
	public void setProperties(List<String> propertyNames) {
		properties.setValue(propertyNames);
	}

	/**
	 * Presets the artifact to be created.
	 * @param type
	 */
	public void setArtifact(BatchArtifactType type, boolean canBeModified) {
		String label = BatchFieldEditorFactory.getArtifactLabel(type);
		if(label != null) {
			setArtifactByLabel(label);
			setArtifactCanBeModified(canBeModified);
		}
	}

	/**
	 * Presets name for the artifact to be created.
	 * @param type
	 */
	public void setArtifactName(String artifactName) {
		name.setValue(artifactName);
	}

	void setArtifactByLabel(String label) {
		artifacts.setValue(label);
	}

	/**
	 * Presets artifact extending from abstract class if available for current artifact type.
	 */
	public void setDeriveFromAbstractClass() {
		String artifactName = artifacts.getValueAsString();
		BatchArtifactType type = BatchFieldEditorFactory.ARTIFACTS.get(artifactName);
		if(type.getClassName() != null) {
			derivedFrom.setValue(BatchFieldEditorFactory.DERIVE_FROM_CLASS);	
		}
	}

	/**
	 * Presets artifact implementing from interface.
	 */
	public void setDeriveFromInterface() {
		derivedFrom.setValue(BatchFieldEditorFactory.DERIVE_FROM_INTERFACE);	
	}

	/**
	 * 
	 * @param b
	 */
	public void setArtifactCanBeModified(boolean canBeModified) {
		artifacts.setEnabled(canBeModified);
	}

	@Override
	public void init(IStructuredSelection selection) {
		super.init(selection);
		defaultTypeName = null;
		setSuperClass(BatchArtifactType.ABSTRACT_BATCHLET_TYPE, false);

		artifacts = BatchFieldEditorFactory.createArtifactEditor(getDefaultArtifact());
		derivedFrom = BatchFieldEditorFactory.createDerivedFromEditor();

		nameOptions = BatchFieldEditorFactory.createNameOptionsEditor();
		name = BatchFieldEditorFactory.createArtifactNameEditor();

		properties = BatchFieldEditorFactory.createPropertiesEditor();
		
		doStatusUpdate();
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
		// createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
//		createModifierControls(composite, nColumns);

		artifacts.doFillIntoGrid(composite);		
		derivedFrom.doFillIntoGrid(composite);

		createSeparator(composite, nColumns);

		nameOptions.doFillIntoGrid(composite);
		name.doFillIntoGrid(composite);
		Control c = (Control)name.getEditorControls()[1];
		GridData d = (GridData)c.getLayoutData();
		d.horizontalSpan--;
		c.setLayoutData(d);
		
		artifacts.addPropertyChangeListener(this);
		derivedFrom.addPropertyChangeListener(this);
		nameOptions.addPropertyChangeListener(this);
		name.addPropertyChangeListener(this);
		
		createSeparator(composite, nColumns);
		
		properties.doFillIntoGrid(composite);
		properties.addPropertyChangeListener(this);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);

		onArtifactKindChange();
		doStatusUpdate();
	}

	protected BatchArtifactType getDefaultArtifact() {
		return BatchArtifactType.BATCHLET;
	}

	private void doStatusUpdate() {
		// status of all used components
		IStatus[] status = new IStatus[] {
				fContainerStatus,
				isEnclosingTypeSelected() ? fEnclosingTypeStatus
						: fPackageStatus, fTypeNameStatus, fModifierStatus,
				fSuperClassStatus, fSuperInterfacesStatus };

		// the mode severe status will be displayed and the OK button
		// enabled/disabled.
		updateStatus(status);
	}

	@Override
	protected void updateStatus(IStatus[] status) {
		IStatus[] ns = new IStatus[status.length + 2];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = artifactNameStatus;
		ns[status.length + 1] = propertiesStatus;
		status = ns;
		super.updateStatus(status);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if(BatchFieldEditorFactory.ARTIFACT_EDITOR.equals(name)) {
			onArtifactKindChange();
		} else if(BatchFieldEditorFactory.DERIVE_FROM_EDITOR.equals(name)) {
			onDerivedFromChange();
		} else if(BatchFieldEditorFactory.LOADER_OPTIONS_EDITOR.equals(name)) {
			onNameOptionChange();
		} else if(BatchFieldEditorFactory.NAME_EDITOR.equals(name)) {
			onNameChange();
		} else if(BatchFieldEditorFactory.PROPERTIES_EDITOR.equals(name)) {
			onFieldNameChange();
		}
		doStatusUpdate();
	}

	void onArtifactKindChange() {
		String artifactName = artifacts.getValueAsString();
		BatchArtifactType type = BatchFieldEditorFactory.ARTIFACTS.get(artifactName);
		if(type != null) {
			boolean hasNoClass = type.getClassName() == null;
			if(hasNoClass) {
				if(BatchFieldEditorFactory.DERIVE_FROM_CLASS.equals(derivedFrom.getValueAsString())) {
					derivedFrom.setValue(BatchFieldEditorFactory.DERIVE_FROM_INTERFACE);
				}
			}
			if(getControl() != null) {
				Composite p = (Composite)derivedFrom.getEditorControls()[1];
				p.getChildren()[1].setEnabled(!hasNoClass);
			}
			onDerivedFromChange();
		}
	}

	void onDerivedFromChange() {
		String artifactName = artifacts.getValueAsString();
		BatchArtifactType type = BatchFieldEditorFactory.ARTIFACTS.get(artifactName);
		if(type != null) {
			if(BatchFieldEditorFactory.DERIVE_FROM_CLASS.equals(derivedFrom.getValueAsString())) {
				setSuperClass(type.getClassName(), false);
				setSuperInterfaces(new ArrayList<String>(), false);
			} else {
				setSuperClass("", false);
				setSuperInterfaces(Arrays.asList(type.getInterfaceName()), false);
			}
		}
	}

	void onNameOptionChange() {
		name.setEnabled(!isArtifactNameQualified());
		if(isArtifactNameQualified()) {
			name.setValue(getQualifiedName());
		} else {
			name.setValue(BeanUtil.getDefaultBeanName(getTypeName()));
		}
	}

	void onNameChange() {
		artifactNameStatus = new StatusInfo();
		if(getJavaProject() == null) {
			return;
		}
		IBatchProject bp = BatchCorePlugin.getBatchProject(getJavaProject().getProject(), true);
		if(bp != null) {
			String v = name.getValueAsString();
			if(v.length() == 0) {
				artifactNameStatus.setError(WizardMessages.errorArtifactNameIsEmpty);
			} else if(!bp.getArtifacts(v).isEmpty()) {
				artifactNameStatus.setError(WizardMessages.errorArtifactNameIsNotUnique);
			}
		}
	}

	void onFieldNameChange() {
		propertiesStatus = new StatusInfo();
		Set<String> fields= new HashSet<String>();
		for (String n: ((List<String>)properties.getValue())) {
			if(n == null || n.length() == 0) continue;
			IStatus val = JavaConventionsUtil.validateFieldName(n, null);
			if (val != null && val.getSeverity() == IStatus.ERROR) {
				propertiesStatus.setError(NLS.bind(WizardMessages.errorFieldNameIsNotValid, val.getMessage()));
				return;
			}
			if(fields.contains(n)) {
				propertiesStatus.setError(NLS.bind(WizardMessages.errorFieldNameIsNotUnique, n));
				return;
			}
			fields.add(n);
		}
	}

	boolean isArtifactNameQualified() {
		String nameOption = nameOptions.getValueAsString();
		return BatchFieldEditorFactory.LOADER_OPTION_QUALIFIED.equals(nameOption);
	}

	String getQualifiedName() {
		return getPackageText() + "." + getTypeName();
	}

	boolean isArtifactNameDefault = true;

	@Override
	protected IStatus typeNameChanged() {
		IStatus result = super.typeNameChanged();
		if(name != null) {
			if(isArtifactNameDefault) {
				if(!isArtifactNameQualified()) {
					name.setValue(BeanUtil.getDefaultBeanName(getTypeName()));
				} else {
					name.setValue(getPackageText() + "." + getTypeName());
				}
			}
		}
		//validate custom fields here
		return result;
	}

	@Override
	protected void createTypeMembers(IType newType,
			final ImportsManager imports, IProgressMonitor monitor)
			throws CoreException {
		createInheritedMethods(newType, true, true, imports,
				new SubProgressMonitor(monitor, 1));

		ISourceRange range = newType.getSourceRange();
		IBuffer buf = newType.getCompilationUnit().getBuffer();
		String lineDelimiter = StubUtility.getLineDelimiterUsed(newType
				.getJavaProject());
		StringBuffer sb = new StringBuffer();
		addAnnotations(imports, sb, lineDelimiter);
		buf.replace(range.getOffset(), 0, sb.toString());
		createFields(newType, imports, monitor, lineDelimiter);
	}

	void addAnnotations(ImportsManager imports, StringBuffer sb,
			String lineDelimiter) {
		if(BatchFieldEditorFactory.LOADER_OPTION_ANNOTATION.equals(nameOptions.getValueAsString())) {
			addNamedAnnotation(imports, sb, lineDelimiter);
		}
	}

	protected void addNamedAnnotation(ImportsManager imports,
			StringBuffer sb, String lineDelimiter) {
		String annotationValue = name.getValueAsString();
		if(BeanUtil.getDefaultBeanName(getTypeName()).equals(annotationValue)) {
			annotationValue = "";
		}
		addAnnotation(BatchConstants.NAMED_QUALIFIER_TYPE, annotationValue, imports, sb, lineDelimiter);
	}

	static void addAnnotation(String typeName, String value, ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		int i = typeName.lastIndexOf('.');
		String name = typeName.substring(i + 1);
		imports.addImport(typeName);
		sb.append("@").append(name);
		if(value != null && value.length() > 0) {
			sb.append("(\"").append(value).append("\")");
		}
		sb.append(lineDelimiter);					
	}

	protected void createFields(IType type, ImportsManager imports, IProgressMonitor monitor, String lineDelimiter) throws CoreException {
		imports.addImport(BatchConstants.INJECT_ANNOTATION_TYPE);
		imports.addImport(BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE);
		
		ICompilationUnit cu = type.getCompilationUnit();
		JavaModelUtil.reconcile(cu);
		CodeGenerationSettings settings = JavaPreferencesSettings
				.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();
		String access = "protected";
		String javatype = "String";

		IJavaElement[] cs = type.getChildren();
		IJavaElement sibling = cs == null || cs.length == 0 ? null : cs[0];

		for (String fieldName: ((List<String>)properties.getValue())) {
			String fieldHeader = "@Inject @BatchProperty " + access + " " + javatype + " " + fieldName + ";" + lineDelimiter; //$NON-NLS-1$ //$NON-NLS-2$
			IField m = type.createField(fieldHeader, sibling, true, null);
			editField(cu, m, javatype, fieldHeader, lineDelimiter);
		}
	}

	void editField(ICompilationUnit cu, IField m, String javatype, String fieldHeader,
			String lineDelimiter) throws CoreException {
		synchronized (cu) {
			cu.reconcile(ICompilationUnit.NO_AST, true, null, null);
		}
		ISourceRange range = m.getSourceRange();
		IBuffer buf = cu.getBuffer();
		StringBuffer sb = new StringBuffer(lineDelimiter);
		if (isAddComments()) {
			String fieldComment = CodeGeneration.getFieldComment(cu,
					javatype, m.getElementName(), lineDelimiter);
			sb.append(fieldComment).append(lineDelimiter);
		}
		sb.append(fieldHeader);
		String formattedContent = codeFormat2(
				CodeFormatter.K_CLASS_BODY_DECLARATIONS, sb.toString(), 1,
				lineDelimiter, cu.getJavaProject());
		if (formattedContent != null && formattedContent.startsWith("\t")) { //$NON-NLS-1$
			formattedContent = formattedContent.substring(1);
		}
		buf.replace(range.getOffset(), range.getLength(), formattedContent);
	}

	//JavaBeanGenerator
	static String codeFormat2(int kind, String sourceString, int indentationLevel, String lineSeparator, IJavaProject project) {
		TextEdit edit = ToolFactory.createCodeFormatter(project.getOptions(true)).format(kind, sourceString, 0, sourceString.length(), indentationLevel, lineSeparator);
		Document doc = new Document(sourceString);
		try {
			edit.apply(doc, 0);
			return doc.get();
		} catch (BadLocationException e) {
			return sourceString;
		}
	}

}
