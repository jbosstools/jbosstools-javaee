package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.validator.ui.Messages;
import org.jboss.tools.struts.validator.ui.ValidatorAttributeEditor;
import org.jboss.tools.struts.validator.ui.formset.model.*;

public class FieldEditor {
	protected Composite control;
	protected FieldAttributeEditor pageEditor = 
	  new PageAttributeEditor("page", Messages.FieldEditor_Page, "EditActions.EditPage"); //$NON-NLS-1$ //$NON-NLS-2$
	protected FieldAttributeEditor indexEditor = 
	  new PropertyIndexAttributeEditor("indexedListProperty", Messages.FieldEditor_IndexedListProperty, "EditActions.EditIndex"); //$NON-NLS-1$ //$NON-NLS-2$
	
	public FieldEditor() {}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		VerticalFillLayout vfl = new VerticalFillLayout();
		vfl.leftMargin = 4; 
		control.setLayout(vfl);
		pageEditor.createControl(control);
		VerticalFillLayout.createSeparator(control, 2);
		indexEditor.createControl(control); 
		VerticalFillLayout.createSeparator(control, 2);
		return control;	
	}
	
	public Control getControl() {
		return control;
	}
	
	public void setFModel(FModel fmodel) {
		pageEditor.setFModel(fmodel);
		indexEditor.setFModel(fmodel);
	}

	public void setEnabled(boolean b) {
		pageEditor.setEnabled(b);
		indexEditor.setEnabled(b);
	}

	public void update() {
		pageEditor.update();
		indexEditor.update();
	}

}

abstract class FieldAttributeEditor extends ValidatorAttributeEditor {
	static String EDIT = Messages.FieldEditor_Edit;
	static String OVERWRITE = Messages.FieldEditor_Override;
	static String DEFAULT = Messages.FieldEditor_Default;
	protected FieldModel fmodel = null;
	protected boolean enabled = true;
	protected int status = 0;
	protected String displayName;
	Label label;
	protected Text text;

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public FieldAttributeEditor(String name, String displayName, String command) {
		super(name, new String[]{EDIT}, new String[]{command});		
		this.displayName = displayName;
	}
	
	public void setFModel(FModel fmodel) {
		if(this.fmodel == fmodel) return;
		this.fmodel = (FieldModel)fmodel;
		update();
	}

	public void update() {
		text.setText(getText());
		int s = getStatus();
		if(s == status) return;
		status = s;
		if(s == FieldModel.INHERITED) text.setForeground(FEditorConstants.INHERITED);
		else text.setForeground(FEditorConstants.DEFAULT_COLOR);
		if(s == FieldModel.DEFINED) bar.setCommands(new String[]{EDIT});
		else if(s == FieldModel.OVERWRITTEN) bar.setCommands(new String[]{EDIT, DEFAULT});
		else  bar.setCommands(new String[]{OVERWRITE});
		bar.update();
		bar.getControl().getParent().layout();
	}
	
	public void setEnabled(boolean b) {
		enabled = fmodel != null && fmodel.isEditable();
		int s = getStatus();
		if (s == FieldModel.DEFINED) {
			bar.setEnabled(EDIT, enabled);
		} else if (s == FieldModel.OVERWRITTEN) {
			bar.setEnabled(EDIT, enabled);
			bar.setEnabled(DEFAULT, enabled);
			bar.setCommands(new String[]{EDIT, DEFAULT});
		} else  {
			bar.setEnabled(OVERWRITE, enabled);
		} 
	}

	public void action(String name) {
		if(OVERWRITE.equals(name)) {
			overwrite();
		} else if(DEFAULT.equals(name)) {
			setDefault();
		} else if(EDIT.equals(name)) {
			edit();
		}
	}

	public XModelObject getModelObject() {
		if(fmodel == null) return null;
		XModelObject[] os = fmodel.getModelObjects();
		return (os.length == 0) ? null : os[0];
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

		text = new Text(control, SWT.READ_ONLY | SWT.BORDER);
		text.setBackground(new Color(null, 255, 255, 255));
		gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);

		bar.createControl(control);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		bar.getControl().setLayoutData(gd);

		return control;
	}
	
	protected abstract String getText();
	protected abstract int getStatus();
	protected abstract void setDefault();
	protected abstract void overwrite();
	
	protected void edit() {
		XModelObject o = getModelObject();
		if(o != null) invoke(commands[0], o);
	}

}

class PageAttributeEditor extends FieldAttributeEditor {

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public PageAttributeEditor(String name, String displayName, String command) {
		super(name, displayName, command);
	}

	protected String getText() {
		return (fmodel == null) ? "" : fmodel.getPage(); //$NON-NLS-1$
	}

	protected int getStatus() {
		return (fmodel == null) ? 0 : fmodel.getPageStatus();
	}

	protected void setDefault() {
		XModelObject o = getModelObject();
		if(o != null) {
			try {
				o.getModel().changeObjectAttribute(o, "page", ""); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
	}

	protected void overwrite() {
		XModelObject[] ts = FieldDataEditor.getTarget(fmodel);
		if(ts == null || ts[0] == null) return;
		if(!ts[0].isActive()) ts[0].setAttributeValue("page", getText()); //$NON-NLS-1$
		long t = ts[0].getTimeStamp();
		invoke("EditActions.EditPage", ts[0]);  //$NON-NLS-1$
		if(t != ts[0].getTimeStamp() && ts[1] != null) {
			ts[0].setAttributeValue("indexedListProperty", ""); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				DefaultCreateHandler.addCreatedObject(ts[1], ts[2], FindObjectHelper.IN_EDITOR_ONLY);
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
	}

}

class PropertyIndexAttributeEditor extends FieldAttributeEditor {

	/**
	 * 
	 * @param name (non-translatable)
	 * @param displayName (translatable)
	 * @param command (non-translatable)
	 */
	public PropertyIndexAttributeEditor(String name, String displayName, String command) {
		super(name, displayName, command);
	}

	protected String getText() {
		return (fmodel == null) ? "" : fmodel.getIndex(); //$NON-NLS-1$
	}

	protected int getStatus() {
		return (fmodel == null) ? 0 : fmodel.getIndexStatus();
	}

	protected void setDefault() {
		XModelObject o = getModelObject();
		if(o != null) {
			try {
				o.getModel().changeObjectAttribute(o, "indexedListProperty", ""); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
	}

	protected void overwrite() {
		XModelObject[] ts = FieldDataEditor.getTarget(fmodel);
		if(ts == null || ts[0] == null) return;
		if(!ts[0].isActive()) ts[0].setAttributeValue("indexedListProperty", getText()); //$NON-NLS-1$
		long t = ts[0].getTimeStamp();
		invoke("EditActions.EditIndex", ts[0]); //$NON-NLS-1$
		if(t != ts[0].getTimeStamp() && ts[1] != null) {
			ts[0].setAttributeValue("page", ""); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				DefaultCreateHandler.addCreatedObject(ts[1], ts[2], FindObjectHelper.IN_EDITOR_ONLY);
			} catch (XModelException e) {
				ModelPlugin.getPluginLog().logError(e);
			}
		}
	}

}

