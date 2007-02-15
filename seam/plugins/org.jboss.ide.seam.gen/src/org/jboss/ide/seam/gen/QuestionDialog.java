package org.jboss.ide.seam.gen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class QuestionDialog extends TitleAreaDialog {

	private final String title;
	private final String description;
	private final Map questions;

	public QuestionDialog(Shell parentShell, String title, String description, Map questions) {
		super( parentShell );
		this.title = title;
		this.description = description;
		this.questions = questions;
	}
	
	Map propertyToField = new HashMap();
	Map propertyToDefaultLabel = new HashMap();
	private Map result = Collections.EMPTY_MAP;
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title); 
		setTitle(description); 
		Composite control = (Composite) super.createDialogArea( parent );
		
		Composite composite = new Composite(control,SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(3,false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        
        ModifyListener modifyListener = new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updateStatus();
			}
		
		};

		Map properties = questions;
		
		Text firstField = null;
		Iterator iterator = properties.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry element = (Map.Entry) iterator.next();
			String name = (String) element.getKey();
			SeamGenProperty sgp = (SeamGenProperty) element.getValue();
		
			Label label = new Label(composite, SWT.NONE);
			label.setText( sgp.getDescription() + ":" );
			
			Text text = new Text(composite, SWT.BORDER | SWT.LEAD);
			text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
			text.setFocus();
			text.addModifyListener( modifyListener );
			if(firstField==null) {
				firstField = text;
			}
			propertyToField.put(name, text);
			
			label = new Label(composite, SWT.NONE);
			label.setText( "                       " );
			propertyToDefaultLabel.put( name, label );
			
		}
		
		firstField.setFocus();
		//initDefaultNames(ef, propertyCombo);
		
		return control;
	}

	
	public Map getPropertiesResult() {
		return result;
	}

	private Map internalGetResult() {
		Map m = new HashMap();
		Iterator iterator = propertyToField.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry element = (Map.Entry) iterator.next();
			Text t = (Text) element.getValue();
			
			
			String text = t.getText();
			if(text.trim().length()>0) {
				m.put( element.getKey(), text);	
			} else {
				Label dv = (Label) propertyToDefaultLabel.get(element.getKey());
				if(dv.getText().trim().length()>0) {
					m.put( element.getKey(), dv.getText());
				}
			}
		}
		return m;
	}

	boolean updating = false;
	void updateStatus() {
		if(!updating) {
			setMessage( null );
			getButton( IDialogConstants.OK_ID ).setEnabled( true );
			updating = true;
			// Big ineffective hack!
			Map others = internalGetResult();
			Properties properties = new Properties();
			properties.putAll( others );

			boolean haveNoWarning = true;
			Iterator iter = questions.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry element = (Map.Entry) iter.next();
				SeamGenProperty gp = (SeamGenProperty) element.getValue();

				Label text = (Label) propertyToDefaultLabel.get( element.getKey() );
				
				String defaultValue = gp.getDefaultValue( properties );
				text.setText( defaultValue==null?"":defaultValue );
				
				Text enteredValue = (Text) propertyToField.get(element.getKey());
				if(haveNoWarning && enteredValue.getText().trim().length()==0 && text.getText().trim().length()==0 ) {
					setMessage( gp.getDescription() + " requires a value", IMessageProvider.ERROR );
					haveNoWarning = false;
					getButton( IDialogConstants.OK_ID ).setEnabled( false );
				}
				
			}
			updating = false;
		}
		
	/*	if(StringHelper.isEmpty( getPropertyName() )) {
			setMessage( "The property name must be chosen or entered",  IMessageProvider.ERROR);			
		} else if (getPropertyName().indexOf( ' ' )>=0 || getPropertyName().indexOf( '\t' )>=0) {
			setMessage( "The property name may not contain whitespaces", IMessageProvider.ERROR);
		} else if(StringHelper.isEmpty( getPropertyValue() )) {
			setMessage( "The property value must be non-empty",  IMessageProvider.ERROR);
		} else {
			if (ef.hasLocalValueFor( getPropertyName() )) {
				setMessage( "The property " + getPropertyName() + " is already set, pressing ok will overwrite the current value",  IMessageProvider.WARNING);
			} else {
				setMessage( null, IMessageProvider.ERROR );
			}
			ok = true;
		}
		
		Button button = getButton(IDialogConstants.OK_ID);
		if(button!=null) {
			button.setEnabled( ok );
		}*/
	}
	
	protected void okPressed() {
		result = internalGetResult();
		super.okPressed();		
		
	}
	
	public void create() {
		super.create();
		updateStatus();
	}
}
