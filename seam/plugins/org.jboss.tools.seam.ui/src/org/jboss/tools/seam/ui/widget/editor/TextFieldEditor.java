/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.widget.editor;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.seam.ui.widget.field.TextField;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
public class TextFieldEditor extends BaseFieldEditor implements PropertyChangeListener{
	
	/**
	 * 
	 */
	public static final int UNLIMITED = -1;
	
	protected int style = -1;
	
	public TextFieldEditor(String name,String aLabelText,String defaultvalue) {
		super(name, aLabelText, defaultvalue);
	}
	
	/**
	 * 
	 */
	protected Text  fTextField = null;
	
	/**
	 * 
	 */
	protected int fWidthInChars = 0;

	/**
	 * 
	 */
	public Object[] getEditorControls() {
		return new Object[] {getLabelControl(),getTextControl()};
	}

	
	/**
	 * @see com.kabira.ide.ex.workbench.ui.feature.IFeatureFieldEditor#doFillIntoGrid(java.lang.Object, int)
	 */
	public void doFillIntoGrid(Object aParent,int aNnumColumns) {
		Assert.isTrue(aParent instanceof Composite);
		Composite aComposite = (Composite) aParent;
		createLabelControl(aComposite);
		fTextField = getTextControl(aComposite);

        GridData gd = new GridData();
        
        gd.horizontalSpan = aNnumColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        
        fTextField.setLayoutData(gd);
	}

    /**
     * 
     * @param parent
     * @return
     */
    public Text getTextControl(Composite parent) {
        if (fTextField == null) {
        	TextField textField = new TextField(parent, getInitialStyle());
            fTextField = textField.getTextControl();
            fTextField.setFont(parent.getFont());
            Object value = getValue();
            fTextField.setText(getValue().toString());
            textField.addPropertyChangeListener(this);
        } else if (parent!=null){
        	Assert.isTrue(parent==fTextField.getParent());
        }  
        return fTextField;
    }
        
	protected void updateWidgetValues() {
		setValueAsString(getValueAsString());
	}

	protected int getInitialStyle() {
		if(this.style >= 0) return style;
    	return SWT.SINGLE | SWT.BORDER;
    }

    /*
     * @param value
     * @return 
     */
    private String checkCollection(Object value){
    	
    	return value != null && (((Collection)value).size() > 0) ? prepareCollectionToString((Collection)value) : new String("");
    }
    
    /*
     * @param collection
     * @return 
     */
    private String prepareCollectionToString(Collection collection)
    {
    	String stringValue = "";
    	Object[] objects = collection.toArray();
    	for(int i = 0; i < objects.length; i++){
    		stringValue += objects[i];
    		if(i < objects.length - 1)
    			stringValue += " ";
    	}
    	return stringValue;
    }
    
    
    /*
     * @param value
     * @return 
     */
    private String checkSimple(Object value){
    	return (value != null) ? value.toString() : new String("");
    }
    /**
     * 
     */
	public int getNumberOfControls() {
		return 2;
	}

	/**
     * Returns this field editor's text control.
     *
     * @return the text control, or <code>null</code> if no
     * text field is created yet
     */
    protected Text getTextControl() {
        return fTextField;
    }

   
    /**
     * @see com.kabira.ide.ex.workbench.ui.feature.eitors.BaseFeatureFieldEditor#setFocus()
     */
    public boolean setFocus() {
    	boolean setfocus = false;
        if(fTextField!=null && !fTextField.isDisposed())
        	setfocus = fTextField.setFocus();
        return setfocus;
    }

	@Override
	public void createEditorControls(Object composite) {
		// TODO Auto-generated method stub
		
	}


	public void save(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setValue() {
		// TODO Auto-generated method stub
		
	}


	public boolean isEditable() {
		// TODO Auto-generated method stub
		return false;
	}


	public void setEditable(boolean aEdiatble) {
		// TODO Auto-generated method stub
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setValue(evt.getNewValue());
	}
}
