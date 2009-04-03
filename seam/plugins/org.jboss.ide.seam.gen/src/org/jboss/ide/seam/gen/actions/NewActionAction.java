package org.jboss.ide.seam.gen.actions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.ide.seam.gen.SeamGenProperty;

public class NewActionAction extends SeamGenAction {

	protected String getTarget() {
		return "new-action"; //$NON-NLS-1$
	}

	public String getTitle() {
		return "Create new Action";
	}
	
	public String getDescription() {
		return "Create a new Java interface and SLSB\n with key Seam/EJB3 annotations.";
	}
	
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();
		properties.put( "component.name", new SeamGenProperty("Seam component name") ); //$NON-NLS-1$
		properties.put( "interface.name", new SeamGenProperty("Local interface name") { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return upper(property);
			}
		});
		properties.put( "bean.name", new SeamGenProperty("Bean name") { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return upper(property + "Bean");
			}
		});
		properties.put( "method.name", new SeamGenProperty("Method name") { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property);
			}
		});
		
		properties.put( "page.name", new SeamGenProperty("Page name") { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property);
			}
		});
		return properties;
	}

}
