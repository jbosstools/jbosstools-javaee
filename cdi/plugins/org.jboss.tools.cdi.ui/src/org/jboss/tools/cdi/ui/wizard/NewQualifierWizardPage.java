package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.ui.widget.editor.CheckBoxFieldEditor;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
//import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;

public class NewQualifierWizardPage extends NewAnnotationWizardPage {	
	IFieldEditor inherited = null;
	CheckBoxFieldEditor cbInherited = null;

	protected void createTypeMembers(IType newType, final ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		ISourceRange range = newType.getSourceRange();
		IBuffer buf = newType.getCompilationUnit().getBuffer();

		StringBuffer sb = new StringBuffer();

		if(inherited != null && inherited.getValue() == Boolean.TRUE) {
			imports.addImport("java.lang.annotation.Inherited");
			sb.append("@Inherited").append("\n");
		}

		imports.addImport("java.lang.annotation.Target");
		imports.addImport("static java.lang.annotation.ElementType.TYPE");
		imports.addImport("static java.lang.annotation.ElementType.METHOD");
		imports.addImport("static java.lang.annotation.ElementType.PARAMETER");
		imports.addImport("static java.lang.annotation.ElementType.FIELD");
		sb.append("@Target( {TYPE, METHOD, PARAMETER, FIELD} )").append("\n");

		imports.addImport("java.lang.annotation.Retention");
		imports.addImport("static java.lang.annotation.RetentionPolicy.RUNTIME");
		sb.append("@Retention(RUNTIME)").append("\n");

		imports.addImport("java.lang.annotation.Documented");
		sb.append("@Documented").append("\n");

		imports.addImport("javax.inject.Qualifier");		
		sb.append("@Qualifier").append("\n");
		
		buf.replace(range.getOffset(), 0, sb.toString());
	}

	public void createControl(Composite parent) {
		super_createControl(parent);
		
		Composite composite = (Composite)getControl();
		
		String label = "Add @Inherited";
		
		inherited = /*IFieldEditorFactory.INSTANCE.*/createCheckboxEditor("isInherited", label, false);
		inherited.doFillIntoGrid(composite);
		
		((Button)cbInherited.getCheckBoxControl()).setText(label);
		
	}

	void super_createControl(Composite parent) {
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

		createCommentControls(composite, nColumns);
		enableCommentControl(true);

		setControl(composite);

		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.NEW_ANNOTATION_WIZARD_PAGE);
	}

	public IFieldEditor createCheckboxEditor(String name, String label,
			boolean defaultValue) {
		cbInherited = new CheckBoxFieldEditor(name,label,Boolean.valueOf(defaultValue));
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,""), cbInherited,});
		return editor;
	}

}
