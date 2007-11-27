/*
 * StrutsConstants.java
 *
 * Created on February 7, 2003, 9:29 AM
 */

package org.jboss.tools.struts;

/**
 *
 * @author  valera
 */
public interface StrutsConstants {

    public static final String DOC_QUALIFIEDNAME = "struts-config";
    public static final String DOC_PUBLICID_PR   = "-//Apache Software Foundation//DTD Struts Configuration ";
    public static final String DOC_PUBLICID_10   = DOC_PUBLICID_PR + "1.0//EN";
    public static final String DOC_PUBLICID_11   = DOC_PUBLICID_PR + "1.1//EN";
    public static final String DOC_PUBLICID_12   = DOC_PUBLICID_PR + "1.2//EN";
    public static final String DOC_EXTDTD_10     = "http://jakarta.apache.org/struts/dtds/struts-config_1_0.dtd";
    public static final String DOC_EXTDTD_11     = "http://jakarta.apache.org/struts/dtds/struts-config_1_1.dtd";
    public static final String DOC_EXTDTD_12     = "http://struts.apache.org/dtds/struts-config_1_2.dtd";

    public static final String VER_SUFFIX_10 = "10";
    public static final String VER_SUFFIX_11 = "11";
    public static final String VER_SUFFIX_12 = "12";

    public static final String ENT_STRUTSCONFIG  = "StrutsConfig";
    public static final String ENT_MSGRES_FOLDER = "StrutsMessageResourcesFolder";
    public static final String ENT_PLUGIN_FOLDER = "StrutsPluginFolder";
    public static final String ENT_ACTION        = "StrutsAction";
    public static final String ENT_FORWARD       = "StrutsForward";
    public static final String ENT_EXCEPTION     = "StrutsException";
    public static final String ENT_SETPROPERTY   = "StrutsSetProperty";
    public static final String ENT_SETPLUGINPROPERTY = "StrutsPluginSetProperty";
    public static final String ENT_FORMBEAN      = "StrutsFormBean";
    public static final String ENT_MSGRES        = "StrutsMessageResources";

    public static final String ENT_PROCESS        = "StrutsProcess";
    public static final String ENT_PROCESSITEM    = "StrutsProcessItem";
    public static final String ENT_PROCESSITEMOUT = "StrutsProcessItemOutput";

    public static final String ENT_FILEJSP        = "FileJSP";
    public static final String ENT_FILEHTML       = "FileHTML";

    public static final String ELM_GLOBALFORW     = "global-forwards";
    public static final String ELM_GLOBALEXC      = "global-exceptions";
    public static final String ELM_ACTIONMAP      = "action-mappings";
    public static final String ELM_FORMBEANS      = "form-beans";
    public static final String ELM_PROCESS        = "process";

    public static final String ATT_ID             = "id";
    public static final String ATT_NAME           = "name";
    public static final String ATT_PATH           = "path";
    public static final String ATT_TYPE           = "type";
    public static final String ATT_SUBTYPE        = "subtype";
    public static final String ATT_TARGET         = "target";
    public static final String ATT_TITLE          = "title";
    public static final String ATT_SHAPE          = "shape";
    public static final String ATT_FORWARD        = "forward";
    public static final String ATT_INCLUDE        = "include";
    public static final String ATT_PROPERTY       = "property";
    public static final String ATT_VALUE          = "value";
    public static final String ATT_CLASSNAME      = "className";
    public static final String ATT_UNKNOWN        = "unknown";

    public static final String TYPE_FORWARD       = "forward";
    public static final String TYPE_EXCEPTION     = "exception";
    public static final String TYPE_ACTION        = "action";
    public static final String TYPE_PAGE          = "page";
    public static final String TYPE_LINK          = "link";
    public static final String TYPE_COMMENT       = "comment";

    public static final String SUBTYPE_UNKNOWN    = "unknown";
    public static final String SUBTYPE_FORWARD    = "forward";
    public static final String SUBTYPE_SWITCH     = "switch";
    public static final String SUBTYPE_FORWARDACTION = "parameter";
    public static final String SUBTYPE_INCLUDE    = "include";
    public static final String SUBTYPE_CONFIRMED  = "confirmed";
    public static final String SUBTYPE_JSP        = "jsp";
    public static final String SUBTYPE_HTML       = "html";
    public static final String SUBTYPE_TILE       = "tile";
    public static final String SUBTYPE_OTHER      = "other";

    public static final String PROP_ISDROP        = "isDrop";
    public static final String PROP_ORGTARGET     = "originalTarget";

    public static String CONTENT_TYPE  = "Content-Type";
    public static String STRUTS_MODULE = "Struts-Module";

}
