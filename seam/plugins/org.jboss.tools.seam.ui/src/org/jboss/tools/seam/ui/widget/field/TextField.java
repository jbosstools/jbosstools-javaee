package org.jboss.tools.seam.ui.widget.field;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class TextField extends BaseField implements ModifyListener{


	Text textField = null;
	
	public TextField(Composite parent, int style) {
		textField = new Text(parent,style);
		textField.addModifyListener(this);
	}

	public void modifyText(ModifyEvent e) {
		firePropertyChange("",((Text)e.widget).getText());
		System.out.println("modify text to - " + ((Text)e.widget).getText());
	}

	public Text getTextControl() {
		return textField;
	}
}
