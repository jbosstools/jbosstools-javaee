/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.java.generation.JavaBeanGenerator;
import org.jboss.tools.common.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ButtonFieldEditor.ButtonPressedAction;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.common.ui.widget.editor.TextFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class NewAnnotationLiteralWizardPage extends NewClassWizardPage {
	protected QualifierSelectionProvider qualifiersProvider = new QualifierSelectionProvider();
	CompositeEditor qualifiers = null;

	protected StatusInfo qualifierStatus = new StatusInfo();

	public NewAnnotationLiteralWizardPage() {
		setTitle(CDIUIMessages.NEW_ANNOTATION_LITERAL_WIZARD_PAGE_NAME);
		setDescription(CDIUIMessages.NEW_ANNOTATION_LITERAL_WIZARD_DESCRIPTION);
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		if (!selection.isEmpty()) {
			Object o = selection.iterator().next();
			IType type = null;
			if (o instanceof IType) {
				type = (IType) o;
			} else if (o instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) o;
				try {
					IType[] ts = cu.getTypes();
					if (ts != null && ts.length > 0)
						type = ts[0];
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}

			}
			boolean isInterface = false;
			try {
				isInterface = type != null && type.isInterface();
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			if (isInterface) {
				String name = "";
				try {
					name = type.getFullyQualifiedParameterizedName();
				} catch (JavaModelException e) {
					name = type.getFullyQualifiedName();
				}
				IPackageFragmentRoot r = getPackageFragmentRoot();
				if(r != null) {
					ICDIProject cdi = NewCDIAnnotationWizardPage.getCDIProject(r.getJavaProject());
					IQualifier q = cdi != null ? cdi.getQualifier(name) : null;
					if(q != null) {
						selected = q;
						ArrayList<String> interfacesNames = new ArrayList<String>();
						interfacesNames.add(name);
						setSuperInterfaces(interfacesNames, true);
						superInterfacesChanged();
						setSuperClass(CDIConstants.ANNOTATION_LITERAL_TYPE_NAME + "<" + type.getElementName() + ">", false);
						setDefaultTypeName(name);
					}
				}
			}
		}

		doStatusUpdate();
	}

	void setDefaultTypeName(String interfaceName) {
		int d = interfaceName.lastIndexOf('.');
		int b = interfaceName.indexOf('<');
		if (b < 0)
			b = interfaceName.length();
		String elementName = interfaceName.substring(d + 1, b);
		String typeName = elementName + "Literal";
		typeName += interfaceName.substring(b);
		setTypeName(typeName, true);
		typeNameChanged();
	}

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
		createModifierControls(composite, nColumns);

		// createSuperClassControls(composite, nColumns);
//		createSuperInterfacesControls(composite, nColumns);

		// createMethodStubSelectionControls(composite, nColumns);

		createCustomFields(composite);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);

		onQualifiersChange();
		doStatusUpdate();
	}

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
		buf.replace(range.getOffset(), 0, sb.toString());
		createInstanceField(newType, imports, monitor, lineDelimiter);
	}

	protected void createCustomFields(Composite composite) {
		createQualifierField(composite);
	}

	protected void createQualifierField(Composite composite) {
		qualifiers = (CompositeEditor)createQualifierCompositeEditor("qualifiers", CDIUIMessages.FIELD_EDITOR_QUALIFIER_LABEL, "");
//		qualifiers.setProvider(qualifiersProvider);
//		qualifiersProvider.setEditorField(qualifiers);
		qualifiers.doFillIntoGrid(composite);
		setQualifiers(getPackageFragmentRoot());
		qualifiers.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				validateQualifierSelection(o);
			}});
	}

	void validateQualifierSelection(Object value) {
		boolean done = false;
		if(value != null) {
			String name = value.toString();
			IPackageFragmentRoot r = getPackageFragmentRoot();
			if(r != null) {
				ICDIProject cdi = NewCDIAnnotationWizardPage.getCDIProject(r.getJavaProject());
				selected = cdi != null ? cdi.getQualifier(name) : null;
				if(selected != null) {
					ArrayList<String> interfacesNames = new ArrayList<String>();
					interfacesNames.add(name);
					setSuperInterfaces(interfacesNames, true);
					superInterfacesChanged();
					setSuperClass(CDIConstants.ANNOTATION_LITERAL_TYPE_NAME + "<" + selected.getSourceType().getElementName() + ">", false);
					setDefaultTypeName(name);
					done = true;
				}
			}
		}
		if(!done) {
			selected = null;
			setSuperInterfaces(new ArrayList<String>(), true);
			superInterfacesChanged();
		}
		
	}

	public void setPackageFragmentRoot(IPackageFragmentRoot root, boolean canBeModified) {
		super.setPackageFragmentRoot(root, canBeModified);
		setQualifiers(root);
		if(qualifiers != null) {
			validateQualifierSelection(qualifiers.getValue());
		}
	}

	void setQualifiers(IPackageFragmentRoot root) {
		qualifiersProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = NewCDIAnnotationWizardPage.getCDIProject(jp);
			if(cdi != null) {
				qualifiersProvider.setProject(cdi);
			}
		}
	}
	
	protected IField createInstanceField(IType type, ImportsManager imports,
			IProgressMonitor monitor, String lineDelimiter)
			throws CoreException {

		imports.addImport(CDIConstants.ANNOTATION_LITERAL_TYPE_NAME);
		
		IType fieldType = fieldType = selected.getSourceType();

		imports.addImport(fieldType.getFullyQualifiedName());

		ICompilationUnit cu = type.getCompilationUnit();
		JavaModelUtil.reconcile(cu);
		CodeGenerationSettings settings = JavaPreferencesSettings
				.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();
		String access = "public static final ";

		String fieldHeader = access + " " + fieldType.getElementName() + " " + "INSTANCE" + 
			"= new " + type.getElementName() + "()" + ";" + lineDelimiter; //$NON-NLS-1$ //$NON-NLS-2$
		IJavaElement[] cs = type.getChildren();
		IJavaElement sibling = cs == null || cs.length == 0 ? null : cs[0];
		IField m = type.createField(fieldHeader, sibling, true, null);

		editField(cu, m, fieldType.getElementName(), fieldHeader, lineDelimiter);
		return m;
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
		String formattedContent = JavaBeanGenerator.codeFormat2(
				CodeFormatter.K_CLASS_BODY_DECLARATIONS, sb.toString(), 1,
				lineDelimiter, cu.getJavaProject());
		if (formattedContent != null && formattedContent.startsWith("\t")) { //$NON-NLS-1$
			formattedContent = formattedContent.substring(1);
		}
		buf.replace(range.getOffset(), range.getLength(), formattedContent);
	}

	protected IStatus superInterfacesChanged() {
		List list = getSuperInterfaces();
		if(list != null && !list.isEmpty()) {
			setDefaultTypeName(list.get(0).toString());
		}
		StatusInfo result = (StatusInfo) super.superInterfacesChanged();
		if (!result.isError()) {
			if (list == null || list.isEmpty()) {
				result.setError("Please select qualifier.");
			}
		}
		return result;
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

	protected void updateStatus(IStatus[] status) {
//		IStatus[] ns = new IStatus[status.length + 1];
//		System.arraycopy(status, 0, ns, 0, status.length);
//		ns[status.length] = qualifierStatus;
//		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}
	
	void onQualifiersChange() {
//		qualifierStatus = new StatusInfo();
//		if(((List)qualifiers.getValue()).isEmpty()) {
//			qualifierStatus.setWarning(CDIUIMessages.MESSAGE_QUALIFIER_NOT_SET);
//		}
	}

	public IFieldEditor createQualifierCompositeEditor(String name, String label, String defaultValue) {
		if(selected != null) {
			defaultValue = selected.getSourceType().getFullyQualifiedName();
		}
		CompositeEditor editor = new CompositeEditor(name, label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue),
				new ButtonFieldEditor(name,createSelectAction(), "")});
		return editor;
	}

	ButtonPressedAction createSelectAction() {
		return new ButtonPressedAction("Browse") {
			public void run() {
				selected = (ICDIAnnotation)runAddAction();
				if(selected != null) {
					qualifiers.setValue(selected.getSourceType().getFullyQualifiedName());
				}
			}
		};
	}

	ICDIAnnotation selected = null;

	protected Object runAddAction() {
		if(qualifiersProvider != null) {
			FilteredItemsSelectionDialog dialog = qualifiersProvider.createSelectionDialog();
			int result = dialog.open();
			if(result == FilteredItemsSelectionDialog.OK) {
				Object[] os = dialog.getResult();
				if(os != null) {
					for (Object o: os) {
						Object v = qualifiersProvider.getSelected(o);
						if(v != null) {
							return v;
						}
					}
				}
			}
		}
		return null;
	}

	public void setQualifier(String type) {
		qualifiers.setValue(type);
	}

}