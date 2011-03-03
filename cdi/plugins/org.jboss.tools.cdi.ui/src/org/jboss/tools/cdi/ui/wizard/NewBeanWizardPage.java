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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
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
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUiImages;
import org.jboss.tools.common.ui.widget.editor.CheckBoxFieldEditor;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor;
import org.jboss.tools.common.ui.widget.editor.TextFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class NewBeanWizardPage extends NewClassWizardPage {

	protected CheckBoxEditorWrapper isNamed;
	protected BeanNameEditorWrapper beanName;

	protected ITaggedFieldEditor scope = null;
	protected Map<String, String> scopes = new TreeMap<String, String>();

	protected QualifierSelectionProvider qualifiersProvider = new QualifierSelectionProvider();
	protected ListFieldEditor qualifiers = null;
	
	protected StatusInfo fieldNameStatus = new StatusInfo();

	public NewBeanWizardPage() {
		setTitle(CDIUIMessages.NEW_BEAN_WIZARD_PAGE_NAME);
		setDescription(CDIUIMessages.NEW_BEAN_WIZARD_DESCRIPTION);
		setImageDescriptor(CDIUiImages.getImageDescriptor(CDIUiImages.WELD_WIZARD_IMAGE_PATH));
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
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
			ArrayList<String> interfacesNames = new ArrayList<String>();
			if (isInterface) {
				String name = "";
				try {
					name = type.getFullyQualifiedParameterizedName();
				} catch (JavaModelException e) {
					name = type.getFullyQualifiedName();
				}
				interfacesNames.add(name);
				setDefaultTypeName(name);
			}
			interfacesNames.add("java.io.Serializable");
			setSuperInterfaces(interfacesNames, true);
			superInterfacesChanged();
		}

		doStatusUpdate();
	}

	void setDefaultTypeName(String interfaceName) {
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

		createSuperClassControls(composite, nColumns);
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

	}

	void addAnnotations(ImportsManager imports, StringBuffer sb,
			String lineDelimiter) {
		addNamedAnnotation(imports, sb, lineDelimiter);
		addScopeAnnotation(imports, sb, lineDelimiter);
		addQualifiersAnnotations(imports, sb, lineDelimiter);
	}

	protected void addNamedAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(isNamed.checkBox != null && isNamed.checkBox.getValue() != null && "true".equals(isNamed.checkBox.getValueAsString())) {
			if(beanName.text.getValue() != null && beanName.text.getValueAsString().length() > 0) {
				addAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME, imports, sb, lineDelimiter, beanName.text.getValueAsString());
			} else {
				NewCDIAnnotationWizardPage.addAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME, imports, sb, lineDelimiter);
			}
		}
	}

	static void addAnnotation(String typeName, ImportsManager imports, StringBuffer sb, String lineDelimiter, String value) {
		int i = typeName.lastIndexOf('.');
		String name = typeName.substring(i + 1);
		imports.addImport(typeName);
		sb.append("@").append(name).append("(\"").append(value).append("\")").append(lineDelimiter);					
	}

	protected void addScopeAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(scope != null && scope.getValue() != null && scope.getValue().toString().length() > 0) {
			String scopeName = scope.getValue().toString();
			String qScopeName = scopes.get(scopeName);
			NewCDIAnnotationWizardPage.addAnnotation(qScopeName, imports, sb, lineDelimiter);
		}
	}

	protected void addQualifiersAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(qualifiers != null) {
			List list = (List)qualifiers.getValue();
			for (Object o: list) {
				if(o instanceof ICDIAnnotation) {
					ICDIAnnotation a = (ICDIAnnotation)o;
					String typeName = a.getSourceType().getFullyQualifiedName();
					NewCDIAnnotationWizardPage.addAnnotation(typeName, imports, sb, lineDelimiter);
				}
			}
		}
	}

	protected void createCustomFields(Composite composite) {
		createBeanNameField(composite);
		createScopeField(composite);
		createQualifiersField(composite);
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
		IStatus[] ns = new IStatus[status.length + 1];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = fieldNameStatus;
		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}

	public void setPackageFragmentRoot(IPackageFragmentRoot root, boolean canBeModified) {
		super.setPackageFragmentRoot(root, canBeModified);
		setScopes(root);
		setQualifiers(root);
	}

	void setScopes(IPackageFragmentRoot root) {
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = NewCDIAnnotationWizardPage.getCDIProject(jp);
			if(cdi != null) {
				Set<String> scopes = cdi.getScopeNames();
				String[] tags = scopes.toArray(new String[0]);
				setScopes(tags);
			} else {
				setScopes(new String[]{""});
			}
		} else {
			setScopes(new String[]{""});
		}
	}

	void setScopes(String[] tags) {
		scopes.clear();
		scopes.put("", "");
		for (String tag: tags) {
			if(tag.length() == 0) continue;
			int i = tag.lastIndexOf('.');
			String name = "@" + tag.substring(i + 1);
			scopes.put(name, tag);
		}
		if(scope != null) {
			scope.setTags(scopes.keySet().toArray(new String[0]));
			scope.setValue("");
		}
	}

	void setQualifiers(IPackageFragmentRoot root) {
		qualifiersProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = CDICorePlugin.getCDIProject(jp.getProject(), true);
			if(cdi != null) qualifiersProvider.setProject(cdi);
		}
	}
	
	protected void createQualifiersField(Composite composite) {
		qualifiers = new ListFieldEditor("qualifiers", CDIUIMessages.FIELD_EDITOR_QUALIFIER_LABEL, new ArrayList<Object>());
		qualifiers.setProvider(qualifiersProvider);
		qualifiersProvider.setEditorField(qualifiers);
		qualifiers.doFillIntoGrid(composite);
		setQualifiers(getPackageFragmentRoot());
		qualifiers.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
//				validateTargetAndStereotype();
			}});
		Object data = qualifiers.getLabelControl().getLayoutData();
		if(data instanceof GridData) {
			GridData d = (GridData)data;
			d.verticalAlignment = SWT.BEGINNING;
			qualifiers.getLabelControl().setData(d);
		}
	}

	public void addQualifier(IQualifier s) {
		List vs = (List)qualifiers.getValue();
		List nvs = new ArrayList();
		if(vs != null) nvs.addAll(vs);
		nvs.add(s);
		qualifiers.setValue(nvs);
	}

	public void setBeanName(String name) {
		isNamed.composite.setValue(Boolean.valueOf(true));
		beanName.composite.setValue(name);
	}

	protected static class CheckBoxEditorWrapper {
		protected IFieldEditor composite = null;
		protected CheckBoxFieldEditor checkBox = null;
	}

	protected static class BeanNameEditorWrapper {
		protected IFieldEditor composite = null;
		protected TextFieldEditor text = null;
	}

	protected void createBeanNameField(Composite composite) {
		isNamed = createCheckBoxField(composite, "isNamed", "Add @Named", false);
		beanName = createTextField(composite, "name", "Bean Name:", "");
		beanName.composite.setEnabled(false);
		isNamed.checkBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				boolean named = "true".equals(isNamed.checkBox.getValueAsString());
				beanName.composite.setEnabled(named);				
			}});
	}

	protected CheckBoxEditorWrapper createCheckBoxField(Composite composite, String name, String label, boolean defaultValue) {
		CheckBoxEditorWrapper wrapper = new CheckBoxEditorWrapper();
		wrapper.checkBox = new CheckBoxFieldEditor(name,"",Boolean.valueOf(defaultValue));
		CompositeEditor editor = new CompositeEditor(name,"", defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name, ""), wrapper.checkBox});
		wrapper.composite = editor;
		wrapper.composite.doFillIntoGrid(composite);
		((Button)wrapper.checkBox.getCheckBoxControl()).setText(label);
		return wrapper;
	}

	protected BeanNameEditorWrapper createTextField(Composite composite, String name, String label, String defaultValue) {
		BeanNameEditorWrapper wrapper = new BeanNameEditorWrapper();
		wrapper.text = new TextFieldEditor(name,"",defaultValue);
		CompositeEditor editor = new CompositeEditor(name,"", defaultValue);
		LabelFieldEditor l = new LabelFieldEditor(name, label);
		editor.addFieldEditors(new IFieldEditor[]{l, wrapper.text, new LabelFieldEditor(name, "")});
		wrapper.composite = editor;
		wrapper.composite.doFillIntoGrid(composite);
		return wrapper;
	}

	protected void createScopeField(Composite composite) {
		ArrayList<String> values = new ArrayList<String>();
		values.add("");
		scope = createComboField("Scope", CDIUIMessages.FIELD_EDITOR_SCOPE_LABEL, composite, values);
		setScopes(getPackageFragmentRoot());
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


}