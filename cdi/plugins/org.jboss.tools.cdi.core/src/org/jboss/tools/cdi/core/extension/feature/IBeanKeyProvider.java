package org.jboss.tools.cdi.core.extension.feature;

import org.jboss.tools.cdi.core.IBean;

public interface IBeanKeyProvider extends ICDIFeature {
	public String getKey(IBean bean);
}
