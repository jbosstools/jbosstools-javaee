package org.jboss.tools.seam.ui.widget.field;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BaseField {
	public static final String PROPERTY_NAME = "value";
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	public void firePropertyChange(Object oldValue, Object newValue) {
		pcs.firePropertyChange(PROPERTY_NAME, oldValue, newValue);
	}
}
