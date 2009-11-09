package org.jboss.tools.seam.xml.ds.model;

import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class DSFileLoader extends SimpleWebFileLoader {

	public DSFileLoader() {}

	protected XModelObjectLoaderUtil createUtil() {
		return new DSFileLoaderUtil();
	}

	protected boolean isCheckingDTD() {
		return false;
	}

}
