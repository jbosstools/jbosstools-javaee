/**
 * 
 */
package org.jboss.tools.seam.ui.widget.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author eskimo
 *
 */
public class CompositeEditor extends BaseFieldEditor implements PropertyChangeListener {

	public CompositeEditor(String name, String label, Object defaultValue) {
		super(name, label, defaultValue);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void doFillIntoGrid(Object parent) {
		Assert.isTrue(parent instanceof Composite, "Parent control should be Composite");
		Assert.isTrue(((Composite)parent).getLayout() instanceof GridLayout,"Editor supports only grid layout");
		Composite aComposite = (Composite) parent;
		Control[] controls = (Control[])getEditorControls(aComposite);
		GridLayout gl = (GridLayout)((Composite)parent).getLayout();
		
        for(int i=0;i<controls.length;i++) {
        	GridData gd = new GridData();
            gd.horizontalSpan = controls.length-1==i?gl.numColumns-i:1;
            gd.horizontalAlignment = GridData.FILL;
         	gd.grabExcessHorizontalSpace = (i==1);
            controls[i].setLayoutData(gd);
        }
	}

	List<Control> controls = new ArrayList<Control>();
	
	@Override
	public Object[] getEditorControls() {
			if(controls.size()>0) return controls.toArray();
			else throw new IllegalStateException("This metod can be invoked after getEditorControls(parent) only");
	}
	
	
	public Object[] getEditorControls(Object parent) {
		for (IFieldEditor editor : editors) {
			controls.addAll(Arrays.asList((Control[])editor.getEditorControls(parent)));
		}
		return controls.toArray(new Control[]{});
	}

	public int getNumberOfControls() {
		return editors.size();
	}
	
	public boolean isEditable() {
		return true;
	}

	public void save(Object object) {
	}

	public void setEditable(boolean ediatble) {
	}

	List<IFieldEditor> editors = new ArrayList<IFieldEditor>();
	
	public CompositeEditor addFieldEditors(IFieldEditor[] editors) {
		this.editors.addAll( Arrays.asList(editors));
		for (IFieldEditor editor : Arrays.asList(editors)) {
			editor.addPropertyChangeListener(this);
		}
		return this;
	}
	
	public void setValue(Object newValue) {
		for (IFieldEditor editor : editors) {
			editor.removePropertyChangeListener(this);
			editor.setValue(newValue);
			editor.addPropertyChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		for (IFieldEditor editor : editors) {
			if(event.getSource()!=editor) {
				editor.removePropertyChangeListener(this);
				editor.setValue(event.getNewValue());
				editor.addPropertyChangeListener(this);				
			}
		}
		super.setValue(event.getNewValue());
	}
}
