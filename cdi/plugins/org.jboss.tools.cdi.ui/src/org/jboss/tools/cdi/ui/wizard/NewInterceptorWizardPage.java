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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedConstructorsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedMethodsOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.util.JavaConventionsUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jdt.ui.CodeGeneration;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage.ImportsManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.common.java.generation.JavaBeanGenerator;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ListFieldEditor;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewInterceptorWizardPage extends NewClassWizardPage {
	protected InterceptorBindingSelectionProvider interceptorBindingsProvider = new InterceptorBindingSelectionProvider();
	protected ListFieldEditor interceptorBindings = null;
	IFieldEditor methodName = null;

	protected StatusInfo methodNameStatus = new StatusInfo();
	protected StatusInfo interceptorBindingsStatus = new StatusInfo();

	public NewInterceptorWizardPage() {
		setTitle(CDIUIMessages.NEW_INTERCEPTOR_WIZARD_PAGE_NAME);
		setDescription("Create a new Interceptor Java class");
	}

	public void init(IStructuredSelection selection) {
		super.init(selection);
	}

	public void addInterceptorBinding(ICDIAnnotation a) {
		Object o = interceptorBindings.getValue();
		List list = o instanceof List ? (List)o : new ArrayList();
		if(list.contains(a)) return;
		list.add(a);
		interceptorBindings.setValue(new ArrayList(list));
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns= 4;

		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;
		composite.setLayout(layout);

		// pick & choose the wanted UI components

		createContainerControls(composite, nColumns);
		createPackageControls(composite, nColumns);
//		createEnclosingTypeControls(composite, nColumns);

		createSeparator(composite, nColumns);

		createTypeNameControls(composite, nColumns);
//		createModifierControls(composite, nColumns);

		createSuperClassControls(composite, nColumns);
//		createSuperInterfacesControls(composite, nColumns);

//		createMethodStubSelectionControls(composite, nColumns);

		createCustomFields(composite);

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_CLASS_WIZARD_PAGE);

		onInterceptorBindingChange();
		doStatusUpdate();
	}

	protected void createTypeMembers(IType newType, final ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		createInheritedMethods(newType, true, true, imports, new SubProgressMonitor(monitor, 1));

		ISourceRange range = newType.getSourceRange();
		IBuffer buf = newType.getCompilationUnit().getBuffer();		
		String lineDelimiter = StubUtility.getLineDelimiterUsed(newType.getJavaProject());
		StringBuffer sb = new StringBuffer();
		addAnnotations(imports, sb, lineDelimiter);
		buf.replace(range.getOffset(), 0, sb.toString());
		//TODO add method
		createAroundInvokeMethod(newType, imports, monitor, lineDelimiter);
	}

	void addAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		addInterceptorBindingAnnotations(imports, sb, lineDelimiter);
		addInterceptorAnnotation(imports, sb, lineDelimiter);
	}

	protected void addInterceptorAnnotation(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		NewCDIAnnotationWizardPage.addAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME, imports, sb, lineDelimiter);
	}
	
	protected void addInterceptorBindingAnnotations(ImportsManager imports, StringBuffer sb, String lineDelimiter) {
		if(interceptorBindings != null) {
			List list = (List)interceptorBindings.getValue();
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
		createInterceptorBindingField(composite);
		createMethodNameField(composite);
	}

	protected void createInterceptorBindingField(Composite composite) {
		interceptorBindings = new ListFieldEditor("interceptorBindings", CDIUIMessages.FIELD_EDITOR_INTERCEPTOR_BINDINGS_LABEL, new ArrayList<Object>());
		interceptorBindings.setProvider(interceptorBindingsProvider);
		interceptorBindingsProvider.setEditorField(interceptorBindings);
		interceptorBindings.doFillIntoGrid(composite);
		setInterceptorBindings(getPackageFragmentRoot());
		interceptorBindings.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onInterceptorBindingChange();
				doStatusUpdate();
			}
		});
	}

	void onInterceptorBindingChange() {
		interceptorBindingsStatus = new StatusInfo();
		if(((List)interceptorBindings.getValue()).isEmpty()) {
			interceptorBindingsStatus.setWarning(CDIUIMessages.MESSAGE_INTERCEPTOR_BINDINGS_EMPTY);
		}
	}

	protected void createMethodNameField(Composite composite) {
		String label = "Around Invoke Method Name:";
		methodName = IFieldEditorFactory.INSTANCE.createTextEditor("methodName", label, "manage");
		((CompositeEditor)methodName).addFieldEditors(new IFieldEditor[]{new LabelFieldEditor("methodName", "")});
		methodName.doFillIntoGrid(composite);
		methodName.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onMethodNameChange();
				doStatusUpdate();
			}
		});
	}

	void setInterceptorBindings(IPackageFragmentRoot root) {
		interceptorBindingsProvider.setProject(null);
		if(root != null) {
			IJavaProject jp = root.getJavaProject();
			ICDIProject cdi = NewCDIAnnotationWizardPage.getCDIProject(jp);
			if(cdi != null) {
				interceptorBindingsProvider.setProject(cdi);
			}
		}
	}

	void onMethodNameChange() {
		String methodName = this.methodName.getValueAsString();
		methodNameStatus = new StatusInfo();
		if(methodName.length() == 0) {
			methodNameStatus.setError(CDIUIMessages.MESSAGE_METHOD_NAME_EMPTY);
			return;
		}
		IStatus val = JavaConventionsUtil.validateMethodName(methodName, null);
		if(val != null && val.getSeverity() == IStatus.ERROR) {
			methodNameStatus.setError(NLS.bind(CDIUIMessages.MESSAGE_METHOD_NAME_NOT_VALID, val.getMessage()));
		}

	}
	
	protected IMethod createAroundInvokeMethod(IType type, ImportsManager imports, IProgressMonitor monitor, String lineDelimiter) throws CoreException {
		String methodName = "" + this.methodName.getValue();
		
		imports.addImport("javax.interceptor.InvocationContext");
		imports.addImport("javax.interceptor.AroundInvoke");
		
		final ICompilationUnit cu= type.getCompilationUnit();
		JavaModelUtil.reconcile(cu);
		CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings(type.getJavaProject());
		settings.createComments = isAddComments();
		String access = "public";
		 String javatype = "Object";

		String methodHeader = "@AroundInvoke" + lineDelimiter + access + " " + javatype + " " + methodName + "(InvocationContext ic) throws Exception"; //$NON-NLS-1$ //$NON-NLS-2$
		String stub = null;
		if(!type.isInterface()) {
			methodHeader += " {" + lineDelimiter; //$NON-NLS-1$
			stub = methodHeader  + "}" + lineDelimiter; //$NON-NLS-1$
		} else {
			methodHeader += ";" + lineDelimiter; //$NON-NLS-1$
			stub = methodHeader;
		}
		IMethod m = type.createMethod(stub, null, true, null);
		
		editMethod(cu, m, methodHeader, "return null;", lineDelimiter);
		return m;
	}
	
	void editMethod(ICompilationUnit cu, IMethod m, String methodHeader, String methodContent, String lineDelimiter) throws CoreException {
		synchronized(cu) {
			cu.reconcile(ICompilationUnit.NO_AST, true, null, null);
		}
		ISourceRange range = m.getSourceRange();
		IBuffer buf = cu.getBuffer();
		StringBuffer sb = new StringBuffer(lineDelimiter);
		if(isAddComments()) {
			String methodComment = CodeGeneration.getMethodComment(m, null, lineDelimiter);
			sb.append(methodComment);			
		}
		sb.append(methodHeader);
		if(methodContent != null) {
			sb.append(methodContent).append("}").append(lineDelimiter); //$NON-NLS-1$
		}
		String formattedContent = JavaBeanGenerator.codeFormat2(CodeFormatter.K_CLASS_BODY_DECLARATIONS, sb.toString(), 1, lineDelimiter, cu.getJavaProject());
		if(formattedContent != null && formattedContent.startsWith("\t")) { //$NON-NLS-1$
			formattedContent = formattedContent.substring(1);
		}
		buf.replace(range.getOffset(), range.getLength(), formattedContent);
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
		IStatus[] ns = new IStatus[status.length + 2];
		System.arraycopy(status, 0, ns, 0, status.length);
		ns[status.length] = methodNameStatus;
		ns[status.length + 1] = interceptorBindingsStatus;
		status = ns;
		updateStatus(StatusUtil.getMostSevere(status));
	}

}