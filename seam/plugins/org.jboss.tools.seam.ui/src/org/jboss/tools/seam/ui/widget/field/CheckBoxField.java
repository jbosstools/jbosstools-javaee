package org.jboss.tools.seam.ui.widget.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class CheckBoxField extends BaseField implements SelectionListener {
	private Button checkBox = null;
	
	public CheckBoxField(Composite parent) {
		checkBox = new Button(parent, SWT.CHECK);
		checkBox.addSelectionListener(this);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		checkBox.getSelection();
	}

	public void widgetSelected(SelectionEvent e) {
		firePropertyChange(!checkBox.getSelection(),
				checkBox.getSelection());
		System.out.println(checkBox.getSelection());
	}
	
	public Button getCheckBox() {
		return checkBox;
	}
}
