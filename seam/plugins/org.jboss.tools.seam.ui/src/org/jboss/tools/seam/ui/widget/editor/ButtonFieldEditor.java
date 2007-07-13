/**
 * 
 */
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.jboss.tools.seam.ui.widget.field.PushButtonField;

/**
 * @author eskimo
 *
 */
public class ButtonFieldEditor extends BaseFieldEditor {

	PushButtonField button= null;
	
	private ButtonPressedAction buttonAction = new ButtonPressedAction("Browse") {
		@Override
		public void run() {
			throw new RuntimeException("Not implemented yet. Please setup real acion for editor.");
		}
	};
	
	public ButtonFieldEditor(String name, String label) {
		super(name, label, new Object());
	}
	
	public ButtonFieldEditor(String name, ButtonPressedAction action, Object defaultValue) {
		super(name, action.getText(), defaultValue);
		buttonAction = action;
		buttonAction.setFieldEditor(this);
	}
	
	@Override
	public void doFillIntoGrid(Object parent) {
	}

	@Override
	public Object[] getEditorControls() {
		return null;
	}

	public boolean isEditable() {
		return false;
	}

	public void save(Object object) {
	}

	public void setEditable(boolean ediatble) {
	}
	
	public Object[] getEditorControls(Object composite) {
		if(button==null && composite!=null) {
			button = new PushButtonField((Composite)composite,buttonAction);
		}
		return new Control[]{button.getControl()};
	}

	public ButtonPressedAction getButtonaction() {
		return buttonAction;
	}
	
	public static class ButtonPressedAction extends Action implements SelectionListener{
		
		private IFieldEditor editor = null;
		
		public ButtonPressedAction(String label) {
			super(label);
		}
		
		public void setFieldEditor(IFieldEditor newEditor) {
			editor = newEditor;
		}
		
		public IFieldEditor getFieldEditor() {
			return editor;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		public void widgetSelected(SelectionEvent e) {
				run();
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.widget.editor.BaseFieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 1;
	}
}
