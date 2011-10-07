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
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
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
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.NewBeanWizardPage.CheckBoxEditorWrapper;
import org.jboss.tools.common.java.generation.JavaBeanGenerator;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class NewDecoratorWizardPage extends NewClassWizardPage {
	protected InterceptorBindingSelectionProvider interceptorBindingsProvider = new InterceptorBindingSelectionProvider();
	IFieldEditor fieldName = null;
	String defaultTypeName = null;
	String defaultFieldName = null;

	protected StatusInfo fieldNameStatus = new StatusInfo();

	protected boolean mayBeRegisteredInBeansXML = true;
	protected CheckBoxEditorWrapper registerInBeansXML = null;

	public NewDecoratorWizardPage() {
		setTitle(CDIUIMessages.NEW_DECORATOR_WIZARD_PAGE_NAME);
		setDescription(CDIUIMessages.NEW_DECORATOR_WIZARD_DESCRIPTION);
		setImageDescriptor(CDIImages.getImageDescriptor(CDIImages.WELD_WIZARD_IMAGE_PATH));
	}

	public void setMayBeRegisteredInBeansXML(boolean b) {
		mayBeRegisteredInBeansXML = b;
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
		defaultTypeName = null;
		defaultFieldName = null;
		if (selection != null && !selection.isEmpty()) {
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
				ArrayList<String> interfacesNames = new ArrayList<String>();
				String name = "";
				try {
					name = type.getFullyQualifiedParameterizedName();
				} catch (JavaModelException e) {
					name = type.getFullyQualifiedName();
				}
				interfacesNames.add(name);
				setSuperInterfaces(interfacesNames, true);
				superInterfacesChanged();
				setDefaultTypeName(name);
			}
		}
		setModifiers(getModifiers() | Flags.AccAbstract, true);

		doStatusUpdate();
	}

	void setDefaultTypeName(String interfaceName) {
		int d = interfaceName.lastIndexOf('.');
		int b = interfaceName.indexOf('<');
		if (b < 0)
			b = interfaceName.length();
		String elementName = interfaceName.substring(d + 1, b);
		String typeName = elementName + "Decorator";
		typeName += interfaceName.substring(b);

		String currentTypeName = getTypeName();
		boolean isDefault = currentTypeName == null || currentTypeName.length() == 0 || currentTypeName.equals(defaultTypeName);
		if(isDefault)  {
			setTypeName(typeName, true);
			typeNameChanged();
		}
		defaultTypeName = typeName;
	
		String _defaultFieldName = elementName;
		if(_defaultFieldName.length() > 0) {
			_defaultFieldName = _defaultFieldName.substring(0, 1).toLowerCase() + _defaultFieldName.substring(1);
			if(fieldName != null) {
				String currentFieldName = fieldName.getValueAsString();
				isDefault = currentFieldName == null || currentFieldName.length() == 0 || currentFieldName.equals(defaultFieldName);
				if(isDefault)  {
					fieldName.setValue(_defaultFieldName);
				}
				defaultFieldName = _defaultFieldName;
			}
		}
		
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
		createSuperInterfacesControls(composite, nColumns);

		// createMethodStubSelectionControls(composite, nColumns);

		createCustomFields(composite);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);

		// onInterceptorBindingChange();
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
		addAnnotations(imports, sb, lineDelimiter);
		buf.replace(range.getOffset(), 0, sb.toString());
		createDelegateField(newType, imports, monitor, lineDelimiter);
		modifyMethodContent(newType, imports, monitor, lineDelimiter);
	}

	void addAnnotations(ImportsManager imports, StringBuffer sb,
			String lineDelimiter) {
		addDecoratorAnnotation(imports, sb, lineDelimiter);
	}

	protected void addDecoratorAnnotation(ImportsManager imports,
			StringBuffer sb, String lineDelimiter) {
		NewCDIAnnotationWizardPage.addAnnotation(
				CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME, imports, sb,
				lineDelimiter);
	}

	protected void createCustomFields(Composite composite) {
		createFieldNameField(composite);
		createRegisterInBeansXML(composite);
	}

	protected void createFieldNameField(Composite composite) {
		String label = "Delegate Field Name:";
		List is = getSuperInterfaces();
		String defaultFieldName = "";
		if (is != null && !is.isEmpty()) {
			defaultFieldName = is.get(0).toString();
			int i = defaultFieldName.lastIndexOf('.');
			if (i >= 0)
				defaultFieldName = defaultFieldName.substring(i + 1);
			if (defaultFieldName.length() > 0) {
				defaultFieldName = defaultFieldName.substring(0, 1)
						.toLowerCase() + defaultFieldName.substring(1);
			}
			i = defaultFieldName.indexOf('<');
			if (i >= 0) {
				defaultFieldName = defaultFieldName.substring(0, i);
			}
		}
		fieldName = IFieldEditorFactory.INSTANCE.createTextEditor("fieldName",
				label, defaultFieldName);
		((CompositeEditor) fieldName)
				.addFieldEditors(new IFieldEditor[] { new LabelFieldEditor(
						"fieldName", "") });
		fieldName.doFillIntoGrid(composite);
		fieldName.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onFieldNameChange();
				doStatusUpdate();
			}
		});
	}

	void onFieldNameChange() {
		String methodName = this.fieldName.getValueAsString();
		fieldNameStatus = new StatusInfo();
		if (methodName.length() == 0) {
			fieldNameStatus.setError(CDIUIMessages.MESSAGE_FIELD_NAME_EMPTY);
			return;
		}
		IStatus val = JavaConventionsUtil.validateMethodName(methodName, null);
		if (val != null && val.getSeverity() == IStatus.ERROR) {
			fieldNameStatus.setError(NLS.bind(
					CDIUIMessages.MESSAGE_FIELD_NAME_NOT_VALID,
					val.getMessage()));
		}

	}

	protected void createRegisterInBeansXML(Composite composite) {
		if(!mayBeRegisteredInBeansXML) return;
		String label = "Register in beans.xml";
		registerInBeansXML = NewBeanWizardPage.createCheckBoxField(composite, "register", label, true);
	}

	protected IField createDelegateField(IType type, ImportsManager imports,
			IProgressMonitor monitor, String lineDelimiter)
			throws CoreException {
		String fieldName = "" + this.fieldName.getValue();

		imports.addImport("javax.interceptor.InvocationContext");
		imports.addImport("javax.interceptor.AroundInvoke");
		imports.addImport(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
		imports.addImport(CDIConstants.ANY_QUALIFIER_TYPE_NAME);
		imports.addImport(CDIConstants.DELEGATE_STEREOTYPE_TYPE_NAME);

		ICompilationUnit cu = type.getCompilationUnit();
		JavaModelUtil.reconcile(cu);
		CodeGenerationSettings settings = JavaPreferencesSettings
				.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();
		String access = "private";
		String javatype = "Object";
		List is = getSuperInterfaces();
		if (is != null)
			for (int i = 0; i < is.size(); i++) {
				String in = is.get(i).toString();
				int d = in.lastIndexOf('.');
				if (d >= 0)
					in = in.substring(d + 1);
				javatype = in;
				break;
			}

		String fieldHeader = "@Inject @Delegate @Any " + access + " " + javatype + " " + fieldName + ";" + lineDelimiter; //$NON-NLS-1$ //$NON-NLS-2$
		IJavaElement[] cs = type.getChildren();
		IJavaElement sibling = cs == null || cs.length == 0 ? null : cs[0];
		IField m = type.createField(fieldHeader, sibling, true, null);

		editField(cu, m, javatype, fieldHeader, lineDelimiter);
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
				result.setError("Please select decorated type.");
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
		if(!CDIUIMessages.NEW_DECORATOR_WIZARD_DESCRIPTION.equals(getDescription())) {
			setDescription(CDIUIMessages.NEW_DECORATOR_WIZARD_DESCRIPTION);
		}
		IStatus[] ns = new IStatus[status.length + 1];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = fieldNameStatus;
		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}
	
	protected void modifyMethodContent(IType type, ImportsManager imports,
			IProgressMonitor monitor, String lineDelimiter) throws CoreException {
		IMethod[] ms = type.getMethods();
		for (int i = 0; i < ms.length; i++) {
			if(ms[i].isConstructor()) continue;
			ICompilationUnit cu = type.getCompilationUnit();
			synchronized (cu) {
				cu.reconcile(ICompilationUnit.NO_AST, true, null, null);
			}
			IBuffer buf = cu.getBuffer();
			ISourceRange range = ms[i].getSourceRange();

			int start = -1;
			int end = -1;
			StringBuffer sb = new StringBuffer();
			if("void".equals(ms[i].getReturnType()) || "V".equals(ms[i].getReturnType())) {
				end = buf.getContents().indexOf("}", range.getOffset());
				if(end < 0) continue;
				end = buf.getContents().lastIndexOf(lineDelimiter, end);
				if(end < 0 || end < range.getOffset()) continue;
//				end += lineDelimiter.length();
				start = end;
			} else {			
				start = buf.getContents().indexOf("return", range.getOffset());
				if(start < 0 || start > range.getOffset() + range.getLength()) continue;
				start += 7;
				end =  buf.getContents().indexOf(";", start);
				if(end < 0) continue;
				end++;
			}
			String fieldName = "" + this.fieldName.getValue();
			String methodName = ms[i].getElementName();
			String[] ps = ms[i].getParameterNames();
			sb.append(fieldName).append('.').append(methodName).append('(');
			for (int k = 0; k < ps.length; k++) {
				if(k > 0) sb.append(", ");
				sb.append(ps[k]);
			}
			sb.append(");");
			buf.replace(start, end - start, sb.toString());
		}
		
	}

	public boolean isToBeRegisteredInBeansXML() {
		if(registerInBeansXML != null) {
			return registerInBeansXML.composite.getValue() == Boolean.TRUE;
		}
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		if(!getControl().isVisible() && visible && fSuperInterfacesStatus.matches(IStatus.ERROR) && !fTypeNameStatus.matches(IStatus.ERROR)) {
			setDescription(fSuperInterfacesStatus.getMessage());
		}
		super.setVisible(visible);
	}

	protected String getSuperInterfacesLabel() {
		return CDIUIMessages.NEW_DECORATOR_WIZARD_INTERFACES_LABEL;
	}
}