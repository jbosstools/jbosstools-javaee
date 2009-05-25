package org.jboss.ide.seam.gen.actions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.ide.seam.gen.Messages;
import org.jboss.ide.seam.gen.SeamGenProperty;

public class NewActionAction extends SeamGenAction {

	protected String getTarget() {
		return "new-action"; //$NON-NLS-1$
	}

	public String getTitle() {
		return Messages.NewActionAction_Title;
	}
	
	public String getDescription() {
		return Messages.NewActionAction_Description;
	}
	
	protected Map getQuestions() {
		Map properties = new LinkedHashMap();
		properties.put( "component.name", new SeamGenProperty(Messages.NewActionAction_SeamComponentName) ); //$NON-NLS-1$
		properties.put( "interface.name", new SeamGenProperty(Messages.NewActionAction_LocalInterfaceName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return upper(property);
			}
		});
		properties.put( "bean.name", new SeamGenProperty(Messages.NewActionAction_BeanName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return upper(property + "Bean"); //$NON-NLS-1$
			}
		});
		properties.put( "method.name", new SeamGenProperty(Messages.NewActionAction_MethodName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property);
			}
		});
		
		properties.put( "page.name", new SeamGenProperty(Messages.NewActionAction_PageName) { //$NON-NLS-1$
			public String getDefaultValue(Properties others) {
				String property = others.getProperty( "component.name", "" ); //$NON-NLS-1$ //$NON-NLS-2$
				return lower(property);
			}
		});
		return properties;
	}

}
