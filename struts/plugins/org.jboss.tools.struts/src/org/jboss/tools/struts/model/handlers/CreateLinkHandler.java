/*
 * CreateLinkHandler.java
 * Created on February 26, 2003, 6:00 PM
 */

package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.model.handlers.page.create.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

/**
 *
 * @author  valera
 */
public class CreateLinkHandler extends DefaultCreateHandler implements StrutsConstants {

    public CreateLinkHandler() {}

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        super.executeHandler(object, prop);
        StrutsProcessImpl pi = (StrutsProcessImpl)object.getParent();
        pi.getHelper().updatePages();
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        String name = StrutsProcessHelper.createName(object, "link");
        validatePathAttr(object, p);
        String path = p.getProperty(ATT_PATH, "").trim();
        String title = path;
        if (title.length() > 0) {
            if (title.charAt(title.length()-1) == '/') title = title.substring(0, title.length()-1);
            title = title.substring(title.lastIndexOf('/')+1);
        }
        p.setProperty(ATT_NAME, name);
        p.setProperty(ATT_TYPE, TYPE_LINK);
        p.setProperty(ATT_TITLE, title);
    }

    protected void validatePathAttr(XModelObject object, Properties p) {
        String path = p.getProperty(ATT_PATH);
        if(path == null || path.length() == 0) return;
        UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(object);
		boolean isHttp = StrutsProcessHelper.isHttp(path);
		boolean isAction = !isHttp && up.isActionUrl(path);
        if(isAction) path = up.getActionUrl(path);
        CreatePageContext context = new CreatePageContext();
        context.setProcess(object);
        context.resetRoots();
        boolean isPage = isHttp || (path.indexOf(".") >= 0 && context.isPage(path));
        boolean isTile = (!isPage && !isAction);
        if(!isTile && !isHttp) {
            if(!path.startsWith("/")) path = "/" + path;
            if(isPage) {
                String oldRoot = context.getRoot();
                String jsppath = context.setRootByPath(path);
                String newRoot = context.getRoot();
                if(!isEqualRoots(oldRoot, newRoot) && newRoot != null)
                  path = newRoot + jsppath;
                else
                  path = jsppath;
            }
        }

        HUtil.find(data, 0, ATT_PATH).setValue(path);
        p.setProperty(ATT_PATH, path);
    }

    public boolean isEnabled(XModelObject object) {
        if (!super.isEnabled(object)) return false;
        String type = object.getAttributeValue(ATT_TYPE);
        return TYPE_PAGE.equals(type);
    }
    
    private boolean isEqualRoots(String r1, String r2) {
        return (r1 == null) ? r2 == null : r1.equals(r2);
    }
    
}
