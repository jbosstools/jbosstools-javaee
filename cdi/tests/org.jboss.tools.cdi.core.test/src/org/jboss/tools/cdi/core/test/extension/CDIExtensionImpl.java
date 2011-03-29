package org.jboss.tools.cdi.core.test.extension;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IBeanNameFeature;

public class CDIExtensionImpl implements ICDIExtension, IBeanNameFeature {

	public Object getAdapter(Class adapter) {
		if(adapter == IBeanNameFeature.class) {
			return this;
		}
		return null;
	}

	public String computeBeanName(IBean bean) {
		return null;
	}

}
