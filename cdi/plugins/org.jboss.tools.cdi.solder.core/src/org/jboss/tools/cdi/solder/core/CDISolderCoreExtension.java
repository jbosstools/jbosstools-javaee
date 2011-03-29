package org.jboss.tools.cdi.solder.core;

import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBeanNameFeature;

public class CDISolderCoreExtension implements ICDIExtension {

	public Object getAdapter(Class adapter) {
		if(adapter == IBeanNameFeature.class) {
			return BeanNameFeature.instance;
		}
		return null;
	}

}
