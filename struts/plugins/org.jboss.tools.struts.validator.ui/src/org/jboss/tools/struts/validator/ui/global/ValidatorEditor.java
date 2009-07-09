package org.jboss.tools.struts.validator.ui.global;

import java.util.Properties;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.ui.editor.DefaultEditorPart;
import org.jboss.tools.struts.messages.StrutsUIMessages;

import org.jboss.tools.struts.validator.ui.*;
import org.jboss.tools.common.model.ui.wizards.query.AbstractQueryWizard;
import org.jboss.tools.struts.validator.ui.wizard.depends.DependencyWizard;
import org.jboss.tools.struts.validator.ui.wizard.key.SelectKeyWizard;

public class ValidatorEditor extends DefaultEditorPart {
	static int EDITOR_HEIGHT = 22;
	protected Composite control;
	protected Composite control2; 
	private XModelObject object = null;
	protected Group dependsGroup;
	protected Group javaGroup;
	protected Group javaScriptGroup;
	ValidatorAttributeEditor dependsEditor = new DependsAttributeEditor();
	ValidatorAttributeEditor messageKeyEditor = new KeyAttributeEditor();
	ValidatorAttributeEditor classEditor = new TextAttributeEditor("classname", "Class Name", "EditActions.EditClassname"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	ValidatorAttributeEditor methodEditor = new TextAttributeEditor("method", "Method", "EditActions.EditMethod"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	ValidatorAttributeEditor methodParamsEditor = new TextAttributeEditor("methodParams", "Method Params", "EditActions.EditMethodParams"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	ValidatorAttributeEditor functionNameEditor = new TextAttributeEditor("jsFunctionName", "Function Name", "EditActions.EditFunctionName"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	ValidatorAttributeEditor functionBodyEditor = new TextAreaAttributeEditor("javascript", "Function Body"); //$NON-NLS-1$ //$NON-NLS-2$
	ValidatorAttributeEditor[] editors = new ValidatorAttributeEditor[]{
		dependsEditor, messageKeyEditor, classEditor, methodEditor,
		methodParamsEditor, functionNameEditor, functionBodyEditor
	}; 

	public void dispose() {
		if (editors!=null) {
			for(int i=0;i<editors.length;++i) {
				if (editors[i]!=null) editors[i].dispose();
			}
		}
		editors = null;
		dependsEditor = null;
		messageKeyEditor = null;
		classEditor = null;
		methodEditor = null;
		methodParamsEditor = null;
		functionNameEditor = null;
		functionBodyEditor = null;
	}
	
	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new FillLayout(SWT.VERTICAL));
		control2 = new Composite(control, SWT.NONE);
		BorderLayout bl = new BorderLayout(); 
		control2.setLayout(bl);
		Composite c1 = new Composite(control2, SWT.NONE);
		bl.northComposite = c1;
		c1.setLayout(new VerticalFillLayout());
		createDependsGroup(c1);
		createJavaGroup(c1);
		Composite c2 = new Composite(control2, SWT.NONE);
		bl.centerComposite = c2;
		c2.setLayout(new FillLayout(SWT.VERTICAL));
		createJavaScriptGroup(c2);
		control2.setVisible(false);
		return control;
	}
	
	protected void createDependsGroup(Composite parent) {
		dependsGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4;
		dependsGroup.setLayout(vfl);	
		dependsEditor.createControl(dependsGroup);
		VerticalFillLayout.createSeparator(dependsGroup, 2);	
		messageKeyEditor.createControl(dependsGroup);
		VerticalFillLayout.createSeparator(dependsGroup, 2);
	}
	
	protected void createJavaGroup(Composite parent) {
		javaGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		javaGroup.setText("Java"); //$NON-NLS-1$
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4;
		javaGroup.setLayout(vfl);
		classEditor.createControl(javaGroup);
		VerticalFillLayout.createSeparator(javaGroup, 2);	
		methodEditor.createControl(javaGroup);
		VerticalFillLayout.createSeparator(javaGroup, 2);	
		methodParamsEditor.createControl(javaGroup);
		VerticalFillLayout.createSeparator(javaGroup, 2);
	}
	
	protected void createJavaScriptGroup(Composite parent) {
		javaScriptGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		javaScriptGroup.setText("JavaScript"); //$NON-NLS-1$
		BorderLayout bl = new BorderLayout();
		javaScriptGroup.setLayout(bl);
		Composite c = new Composite(javaScriptGroup, SWT.NONE);
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4;
		c.setLayout(vfl);		
		functionNameEditor.createControl(c);
		bl.northComposite = c;
		bl.centerComposite = functionBodyEditor.createControl(javaScriptGroup);
	}
	
	public void setObject(XModelObject object) {
		if (this.object == object) {
			update();
			return;
		} else {
			if (this.object != null) {
				//editingStopped();
			}
		}
		if((this.object == null) != (object == null)) {
			control2.setVisible(object != null);
			control2.layout(); 
		}
		this.object = object;
		for (int i = 0; i < editors.length; i++) editors[i].setObject(object);
		update();
		control.redraw();
	}
	
	public void update() {
		if(object == null) return;
		for (int i = 0; i < editors.length; i++) editors[i].load();
		updateFunctionBodyEditorComponent();
		setEnabled(object.isObjectEditable());
	}
	
	public void updateFunctionBodyEditorComponent() {
		if(object == null) return;

	}
	
	private void setEnabled(boolean enabled) {
		dependsEditor.setEnabled(enabled);
		messageKeyEditor.setEnabled(enabled);
		classEditor.setEnabled(enabled);
		methodEditor.setEnabled(enabled);
		methodParamsEditor.setEnabled(enabled);
		functionNameEditor.setEnabled(enabled);
		functionBodyEditor.setEnabled(enabled);
	}

}
	
class TextAttributeEditor extends ValidatorAttributeEditor {
	private static String CHANGE = StrutsUIMessages.CHANGE;
	public static int LABEL_WIDTH = 90;
	Label label;
	String displayName;
	protected Text text;
		
	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public TextAttributeEditor(String name, String displayName, String command) {
		super(name, CHANGE, command);
		this.displayName = displayName;
	}			
		
	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		control.setLayout(gl);

		label = new Label(control, SWT.NONE);
		label.setText(displayName);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = LABEL_WIDTH;
		label.setLayoutData(gd);

		text = new Text(control, SWT.BORDER);
		text.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				save();
			}
		});
		text.setBackground(new Color(null, 255, 255, 255));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);

		bar.createControl(control);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		bar.getControl().setLayoutData(gd);

		return control;
	}
		
	public void load() {
		if(object != null && text != null)
		  text.setText("" + object.getAttributeValue(name));  //$NON-NLS-1$
	}
	
	public void save() {
		if(object != null && text != null) {
			try {
				object.getModel().changeObjectAttribute(object, name, "" + text.getText()); //$NON-NLS-1$
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
	}

	public void setEnabled(boolean enabled) {
		if (text != null) text.setEditable(enabled);
		if (bar != null) bar.setEnabled(CHANGE, enabled); 
	}
}
	
class TextAreaAttributeEditor extends ValidatorAttributeEditor {
	Label label;
	String displayName;
	Text text;
	
	int lock = 0;

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 */
	public TextAreaAttributeEditor(String name, String displayName) {
		super(name, StrutsUIMessages.CHANGE, null);
		this.displayName = displayName;
	}			

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		BorderLayout bl = new BorderLayout();
		control.setLayout(bl);
		Composite c = new Composite(control, SWT.NONE);
		bl.northComposite = c;
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4;
		c.setLayout(vfl);		
		label = new Label(c, SWT.NONE);
		label.setText(displayName);
		VerticalFillLayout.createSeparator(c, 1);
		text = new Text(control, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				save();
			}
		});
		text.setBackground(new Color(null, 255, 255, 255));
		bl.centerComposite = text;
		return control;
	}
	
	public void load() {
		if(object == null || text == null) return;
		if(lock > 0) return;
		lock++;
		try {
			text.setText("" + object.getAttributeValue(name));  //$NON-NLS-1$
		} finally {
			lock--;
		}
	}
	
	public void save() {
		if(object == null || text == null) return;
		if(lock > 0) return;
		lock++;
		try {
			object.getModel().changeObjectAttribute(object, name, "" + text.getText()); //$NON-NLS-1$
		} catch (XModelException e) {
			ModelPlugin.getPluginLog().logError(e);
		} finally {
			lock--;
		}
	}

	public void setEnabled(boolean enabled) {
		if (text != null) text.setEditable(enabled);
	}
}

class DependsAttributeEditor extends TextAttributeEditor {
	public DependsAttributeEditor() {
		this("depends", StrutsUIMessages.DEPENDS, "EditActions.EditDepends"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public DependsAttributeEditor(String name, String displayName, String command) {
		super(name, displayName, command);
	}
	
	public void action(String command) {
		runWizard(this, "value", new DependencyWizard(), "Wizard_Validation_Dependency");  //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void runWizard(TextAttributeEditor editor, String propertyName, AbstractQueryWizard wizard, String helpkey) {
		Properties p = new Properties();
		p.setProperty(propertyName, "" + editor.text.getText()); //$NON-NLS-1$
		p.put("model", editor.getModelObject().getModel()); //$NON-NLS-1$
		p.put("object", editor.getModelObject()); //$NON-NLS-1$
		p.put("help", helpkey); //$NON-NLS-1$
		wizard.setObject(p);
		if(wizard.execute() != 0) return;
		editor.text.setText("" + p.getProperty(propertyName)); //$NON-NLS-1$
		save();
	}
	
}

class KeyAttributeEditor extends DependsAttributeEditor {
	public KeyAttributeEditor() {
		super("msg", "Message Key", "EditActions.EditMsg"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void action(String command) {
		runWizard(this, "key", new SelectKeyWizard(), "Wizard_SelectKey");  //$NON-NLS-1$ //$NON-NLS-2$
	}
}
