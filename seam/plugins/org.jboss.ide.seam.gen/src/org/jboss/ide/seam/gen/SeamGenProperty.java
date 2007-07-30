package org.jboss.ide.seam.gen;

import java.util.Properties;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class SeamGenProperty {

	private final String description;

	private String defaultPropertyName;

	public static final String GENERAL = "General";
	public final static int TEXT = 0;
	public final static int JAR = 1;
	public final static int DIR = 2;
	public final static int YES_NO = 3;
	
	public SeamGenProperty(String description) {
		this(description, null);
	}
	
	public SeamGenProperty(String description, String defaultPropertyName) {
			this.description = description;
			this.defaultPropertyName = defaultPropertyName;
	}

	public String getGroup() {
		return GENERAL;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDefaultValue(Properties others) {
		return null;
	}
	
	protected String upper(String name)
	{
		if(name==null || name.length()==0) return "";
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	protected String lower(String name) {
		if ( name == null || name.length() == 0 )
			return "";
		return name.substring( 0, 1 ).toLowerCase() + name.substring( 1 );
	}
	
	public boolean isRequired() {
		return true;
	}

	public int getInputType() {
		return TEXT;
	}

	public String valid(String string) {
		return null;
	}
	
	
	public void applyValue(Properties existing, Control control) {
			if(getDefaultPropertyName()==null) return;
			String property = "";
			if(existing == null || existing.get(getDefaultPropertyName())==null) {
				property = getDefaultValue(existing)==null?"":getDefaultValue(existing);
			} else {
				property = existing.getProperty( getDefaultPropertyName() );
			}
			if(property!=null) {
				if(control instanceof Text) {
					((Text)control).setText( property );
				} else if (control instanceof Button) {
					if("y".equalsIgnoreCase( property )) {
						((Button)control).setSelection( true );
					} else {
						((Button)control).setSelection( false );
					}
				} else {
					System.out.println(getDefaultPropertyName() + " " + property);
				}
			}
	}

	public String getDefaultPropertyName() {
		return defaultPropertyName;
	}
}
