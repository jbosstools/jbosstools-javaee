package org.jboss.tools.cdi.bot.test;

public class CDIConstants {

	public static final String CLASS_OPEN_TAG = "<class>";
	public static final String CLASS_END_TAG = "</class>";		
	public static final String STEREOTYPE_OPEN_TAG = "<stereotype>";
	public static final String STEREOTYPE_END_TAG = "</stereotype>";
	public static final String JAVA_RESOURCES = "Java Resources";
	public static final String JAVA_SOURCE = "JavaSource";
	public static final String SRC = "src";
	public static final String JAVA_RESOURCES_SRC_FOLDER = JAVA_RESOURCES + "/" + SRC + "/";
	public static final String META_INF = "META-INF";
	public static final String WEB_INF = "WEB-INF";
	public static final String WEBCONTENT = "WebContent";
	public static final String BEANS_XML = "beans.xml";	
	public static final String META_INF_BEANS_XML_PATH = WEBCONTENT + "/" + META_INF + "/" + BEANS_XML;
	public static final String WEB_INF_BEANS_XML_PATH = WEBCONTENT + "/" + WEB_INF + "/" + BEANS_XML;
	public static final String NAVIGATE = "Navigate";
	public static final String REFRESH = "Refresh";
	public static final String OPEN_CDI_NAMED_BEANS = "Open CDI Named Bean";
	public static final String SHOW_ALL_ASSIGNABLE = "Show All Assignable Beans...";
	public static final String OPEN_INJECT_BEAN = "Open @Inject Bean";
	public static final String CDI_WEB_PROJECT = "CDI Web Project";
	public static final String MULTIPLE_BEANS = "Multiple beans are eligible for injection";
	public static final String NO_BEAN_IS_ELIGIBLE = "No bean is eligible for injection to " +
			"the injection point";
	public static final String CDI_GROUP = "CDI (Context and Dependency Injection)";
	public static final String WEB_GROUP = "Web";
	public static final String DYNAMIC_WEB_PROJECT = "Dynamic Web Project";
	public static final String CONFIGURE = "Configure";
	public static final String ADD_CDI_SUPPORT = "Add CDI (Context and Dependency Injection) support...";
	public static final String WINDOW = "Window";
	public static final String PROPERTIES = "Properties";
	public static final String PREFERENCES = "Preferences";
	public static final String CDI_PROPERTIES_SETTINGS = "CDI (Context and Dependency Injection) Settings";
	public static final String JAVA_BUILD_PATH = "Java Build Path";
	public static final String LIBRARIES = "Libraries";
	public static final String ADD_JARS = "Add JARs...";
	public static final String OPEN_HYPERLINK = "Open Hyperlink";
	public static final String QUICK_FIX = "Quick Fix";
	public static final String CDI_PRESET = "Dynamic Web Project " +
			"with CDI (Context and Dependency Injection)";
	public static final String CDI_FACET = "CDI (Contexts and Dependency Injection)";
	public static final String CONFIGURATION = "Configuration";
	public static final String PROJECT_FACETS = "Project Facets";
	public static final String RESOURCE_ANNOTATION = "@Resource";
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	
}
