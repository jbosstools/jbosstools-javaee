/**
 * 
 */
package org.jboss.tools.seam.ui.widget.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor.ButtonPressedAction;

/**
 * @author eskimo
 *
 */
public class PushButtonField extends BaseField {
	
	Button button;
	
	/**
	 * 
	 */
	@Override
	public Control getControl() {
		return button;
	}


	public PushButtonField(Composite composite, ButtonPressedAction listener) {
		button = new Button(composite,SWT.PUSH);
		button.setText(listener.getText());
		button.addSelectionListener(listener);
	}
}
