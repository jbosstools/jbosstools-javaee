package org.jboss.tools.jsf.facelet.model;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class FaceletTaglibLoader extends SimpleWebFileLoader {

	public FaceletTaglibLoader() {}

	protected boolean isCheckingDTD() {
		return true;
	}

	protected XModelObjectLoaderUtil createUtil() {
		return new FaceletTaglibLoaderUtil();
	}
}

class FaceletTaglibLoaderUtil extends XModelObjectLoaderUtil {

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

}