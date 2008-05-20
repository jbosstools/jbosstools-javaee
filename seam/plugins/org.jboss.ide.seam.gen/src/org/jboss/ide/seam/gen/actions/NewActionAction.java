package org.jboss.ide.seam.gen.actions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.ide.seam.gen.SeamGenProperty;

public class NewActionAction extends SeamGenAction {

	protected String getTarget() {
		return "new-action";
	}

	public String getTitle() {
		return "Create new Action";
	}
	
	public String getDescription() {
		return "Create a new Java interface and SLSB\n with key Seam/EJB3 annotations.";
	}
	
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();
		properties.put( "component.name", new SeamGenProperty("Seam component name") );
		properties.put( "interface.name", new SeamGenProperty("Local interface name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" );
				return upper(property);
			}
		});
		properties.put( "bean.name", new SeamGenProperty("Bean name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" );
				return upper(property + "Bean");
			}
		});
		properties.put( "method.name", new SeamGenProperty("Method name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" );
				return lower(property);
			}
		});
		
		properties.put( "page.name", new SeamGenProperty("Page name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" );
				return lower(property);
			}
		});
		return properties;
	}

}
