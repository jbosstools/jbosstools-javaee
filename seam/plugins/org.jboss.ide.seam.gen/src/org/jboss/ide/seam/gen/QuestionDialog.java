package org.jboss.ide.seam.gen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jboss.ide.seam.gen.actions.SeamGenAction;

public class QuestionDialog extends TitleAreaDialog {

	private final String title;
	private final String description;
	private final Map questions;
	private final Set groups;

	protected Point getInitialSize() {
		return new Point(706,478);
	}
	
	public QuestionDialog(Shell parentShell, String title, String description, Map questions, Set groups) {
		super( parentShell );
		setShellStyle(getShellStyle() | SWT.RESIZE  | SWT.MAX);
		this.title = title;
		this.description = description;
		this.questions = questions;
		this.groups = groups;
	}
	
	Map propertyToField = new HashMap();
	Map propertyToDefaultLabel = new HashMap();
	private Map result = Collections.EMPTY_MAP;
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText(title); 
		setTitle(description); 
		Composite control = (Composite) super.createDialogArea( parent );
		
		Composite defaultComposite = new Composite(control,SWT.NONE);
		defaultComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(4,false);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        defaultComposite.setLayout(layout);
        
        ModifyListener modifyListener = new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updateStatus();
			}
		
		};

		Map groupContainers = new HashMap();
		if(!groups.isEmpty()) {
			
			TabFolder folder = new TabFolder(defaultComposite,SWT.TOP);
			folder.setLayoutData( new GridData(GridData.FILL_BOTH ));
			Iterator iterator = groups.iterator();
			while ( iterator.hasNext() ) {
				String name = (String) iterator.next();
				TabItem item = new TabItem(folder, SWT.NONE);
				item.setText( name );
				
				Composite container = new Composite(folder, SWT.NULL);
				GridLayout gridLayout = new GridLayout(4, false);
				//gridLayout.verticalSpacing = 9;		
				
				container.setLayout(gridLayout);
				item.setControl( container );
				groupContainers.put(name, container);					
			}
		}
		
		Map properties = questions;
	
		Properties existing = new Properties();
		ILaunchConfiguration configuration;
		try {
			configuration = SeamGenAction.findLaunchConfig( "seamgen" );
			if(configuration!=null) {
				existing = SeamGenAction.getSeamGenProperties( configuration );
			}
			
		}
		catch (CoreException e1) {
			SeamGenPlugin.logError( "Error while preloading build.properties", e1 );
		}
		
		updating = true;
		Text firstField = null;
		Iterator iterator = properties.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry element = (Map.Entry) iterator.next();
			String name = (String) element.getKey();
			final SeamGenProperty sgp = (SeamGenProperty) element.getValue();
		
			Composite composite = (Composite) groupContainers.get(sgp.getGroup());
			if(composite==null) {
				composite = defaultComposite;
			}
			Label label = new Label(composite, SWT.NONE);
			label.setText( sgp.getDescription() + ":" );
			
			
			
			if(sgp.getInputType()==sgp.YES_NO) {
				Button button = new Button(composite, SWT.CHECK);
				propertyToField.put( name, button );
				sgp.applyValue( existing, button );
				GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
				data.horizontalSpan= 2;
				button.setLayoutData(data);
			} else {
				final Text text = new Text(composite, SWT.BORDER | SWT.LEAD);
				text.setFocus();
				text.addModifyListener( modifyListener );
				if(firstField==null) {
					firstField = text;
				}
				propertyToField.put(name, text);
				sgp.applyValue( existing, text );
				if(sgp.getInputType()==sgp.JAR || sgp.getInputType()==sgp.DIR) {
					GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
					data.horizontalSpan= 1;
					text.setLayoutData(data);

					Button button = new Button(composite, SWT.PUSH);
					button.setText("Browse...");
					button.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							String paths = null;
							switch(sgp.getInputType()) {
							case SeamGenProperty.JAR:
								paths = chooseExternalFile(getShell() );
								break;
							case SeamGenProperty.DIR:	
								paths = chooseExternalDirectory( getShell() );
								break;
							default:
								paths = null;
							}

							if(paths!=null) {
								text.setText(paths);
							}

						}
					});
				} else {
					GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
					data.horizontalSpan = 2;
					text.setLayoutData(data);

				}
			}
			label = new Label(composite, SWT.NONE);
			label.setVisible(false);
			label.setText( "                       " );
			
			propertyToDefaultLabel.put( name, label );
			updating = false;
			//updateStatus();
			
		}
		
		
		Label label = new Label(defaultComposite, SWT.NONE); // here to make gtk less ugly.
		//label.setText( "--------------" );
		
		
		firstField.setFocus();
		//initDefaultNames(ef, propertyCombo);
		
		return control;
	}

	
	protected String chooseExternalFile(Shell shell) {
		FileDialog dialog= new FileDialog(shell, SWT.SINGLE);
		dialog.setText("Select file"); 
		dialog.setFilterExtensions(new String[] {"*.jar;*.zip"});
		//dialog.setFilterPath(lastUsedPath);
		
		String res= dialog.open();
		
		return res;
		}

	protected String chooseExternalDirectory(Shell shell) {
		DirectoryDialog dialog= new DirectoryDialog(shell, SWT.SINGLE);
		dialog.setText("Select directory"); 
		
		String res= dialog.open();
		return res;			
	}

	public Map getPropertiesResult() {
		return result;
	}

	private Map internalGetResult() {
		Map m = new HashMap();
		Iterator iterator = propertyToField.entrySet().iterator();
		while ( iterator.hasNext() ) {
			Map.Entry element = (Map.Entry) iterator.next();
			Object value = element.getValue();
			
			String text;
			if(value instanceof Text) {
				Text t = (Text) element.getValue();
				text = t.getText();
			} else {
				Button t = (Button) element.getValue();
				if(t.getSelection()) {
					text = "y";
				} else {
					text = "n";
				}
			}
			SeamGenProperty sgp = (SeamGenProperty) questions.get( element.getKey() );


			if(!sgp.isRequired() || text.trim().length()>0) {
				m.put( element.getKey(), text);	
			} else {				
				Label dv = (Label) propertyToDefaultLabel.get(element.getKey());
				String text2 = dv.getText();
				if(text2.trim().length()>0) {
					m.put( element.getKey(), text2);
				}
			}
			
			//			hack to make sure seamgen does not mess with the path names.
			switch(sgp.getInputType()) {
			case SeamGenProperty.DIR:
			case SeamGenProperty.JAR:
				String str = (String) m.get( element.getKey() );
				if(str!=null) {
					m.put( element.getKey(), str.replace('\\', '/'));
				}
				break;
			default: 
				break;
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
				
				String text2 = getText( element, text );
				
				if(haveNoWarning) {					
					String msg = gp.valid(text2.trim());
					if(text2.trim().length()>0 && msg!=null) {
						setMessage( msg, IMessageProvider.ERROR );
						haveNoWarning = false;
						getButton( IDialogConstants.OK_ID ).setEnabled( false );
					} else if(gp.isRequired() && text2.trim().length()==0 && text.getText().trim().length()==0 ) {
						setMessage( "'" + gp.getDescription() + "' requires a value", IMessageProvider.ERROR );
						haveNoWarning = false;
						getButton( IDialogConstants.OK_ID ).setEnabled( false );
					}	
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

	private String getText(Map.Entry element, Label text) {
		Control object = (Control) propertyToField.get(element.getKey());
		if(object instanceof Button) {
			Button b = (Button) object;
			b.setToolTipText( text.getText() );
			if(b.getSelection()) {
				return "y";
			} else {
				return "n";
			}
			
		} else {
			Text enteredValue = (Text) object;
			enteredValue.setToolTipText( text.getText() );
			return enteredValue.getText();
		}
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
