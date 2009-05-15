package org.jboss.tools.seam.internal.core;

import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;

public interface SeamXMLConstants extends XMLStoreConstants {
	
	public String TAG_VALUE = "value";
	public String TAG_PROPERTY = "property";
	public String TAG_COMPONENT = "component";
	public String TAG_BIJECTED_ATTRIBUTE = "bijected-attribute";
	public String TAG_METHOD = "method";
	public String TAG_ROLE = "role";
	public String TAG_FACTORY = "factory";
	public String TAG_IMPORT = "import";

	public String CLS_PROPERTIES = "properties";
	public String CLS_JAVA = "java";
	public String CLS_MESSAGES = "messages";
	
	public String CLS_MAP = "map";
	public String CLS_LIST = "list";
	
	public String CLS_DATA_MODEL = "datamodel";

	public String ATTR_SCOPE = "scope";
}
