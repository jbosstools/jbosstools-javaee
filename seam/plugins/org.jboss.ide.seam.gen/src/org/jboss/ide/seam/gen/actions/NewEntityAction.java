package org.jboss.ide.seam.gen.actions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.ide.seam.gen.Messages;
import org.jboss.ide.seam.gen.SeamGenProperty;

public class NewEntityAction extends SeamGenAction {

	protected String getTarget() {
		return "new-entity"; //$NON-NLS-1$
	}

	public String getTitle() {
		return Messages.NewEntityAction_Title;
	}
	
	public String getDescription() {
		return Messages.NewEntityAction_Description;
	}
	
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();
		properties.put( "entity.name", new SeamGenProperty(Messages.NewEntityAction_EntityClassName) ); //$NON-NLS-1$
		properties.put( "masterPage.name", new SeamGenProperty(Messages.NewEntityAction_MasterPageName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "entity.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property) + "List"; //$NON-NLS-1$
			}
		});
			
		properties.put( "page.name", new SeamGenProperty(Messages.NewEntityAction_PageName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "entity.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property);
			}
		});
		return properties;
	}


}
