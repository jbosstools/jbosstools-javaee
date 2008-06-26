package org.jboss.ide.seam.gen.actions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.ide.seam.gen.SeamGenProperty;

public class NewEntityAction extends SeamGenAction {

	protected String getTarget() {
		return "new-entity";
	}

	public String getTitle() {
		return "New entity";
	}
	
	public String getDescription() {
		return "Create a new entity bean\nwith key Seam/EJB3 annotations and example attributes.";
	}
	
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();
		properties.put( "entity.name", new SeamGenProperty("Entity class name") );
		properties.put( "masterPage.name", new SeamGenProperty("Master page name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "entity.name", "" );
				return lower(property) + "List";
			}
		});
			
		properties.put( "page.name", new SeamGenProperty("Page name") {
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "entity.name", "" );
				return lower(property);
			}
		});
		return properties;
	}


}
