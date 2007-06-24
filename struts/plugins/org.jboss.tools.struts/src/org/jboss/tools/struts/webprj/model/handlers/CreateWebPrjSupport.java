/*
 * CreateWebPrjSupport.java
 *
 * Created on March 7, 2003, 3:50 PM
 */

package org.jboss.tools.struts.webprj.model.handlers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectHelper;

import java.io.*;
import java.util.*;

import org.eclipse.osgi.util.NLS;

/**
 *
 * @author  valera
 */
public class CreateWebPrjSupport extends WebPrjSupport {
    
    //protected List list = null;
    protected Throwable exc = null;
    protected Step[] steps = new Step[] {new Step0(), new Step1()};
    
    /** Creates a new instance of CreateWebPrjSupport */
    public CreateWebPrjSupport() {
    }
    
    protected void reset() {
        //setStepId(1);
        super.reset();
        /*if (target.getParent() == null) {
            XModelObject depl = target.getModel().getByPath("Deployment");
            DefaultCreateHandler.addCreatedObject(depl, target);
        }*/
    }
    
    public String getDescription() {
        return StrutsUIMessages.CREATE_WEB_PROJECT;
    }
    
    public Step getStep(int step) {
        return steps[step];
    }
    
    class Step0 implements Step {
        
        public String[] getActionNames() {
            return new String[] {NEXT, CANCEL};
        }
        
        public String getMessage() {
            return StrutsUIMessages.ENTER_PROJECTNAME_AND_CONFIGURATIONFILE_VERSION;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP1_PROJECTNAME_AND_VERSION;
        }
        
        public int prepareStep(XModelObject object) {
            String name = p.getProperty("name", ""); //$NON-NLS-1$ //$NON-NLS-2$
            String version = p.getProperty("version"); //$NON-NLS-1$
            setAttributeValue(0, "name", name); //$NON-NLS-1$
            if (version != null) setAttributeValue(0, "version", version); //$NON-NLS-1$
            return 0;
        }
        
        public int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(0);
            String name = p2.getProperty("name"); //$NON-NLS-1$
            String version = p2.getProperty("version"); //$NON-NLS-1$
            p.setProperty("name", name); //$NON-NLS-1$
            p.setProperty("version", version); //$NON-NLS-1$
            return 1;
        }
        
        public int undoStep(XModelObject object) {
            return -1;
        }
    }
    
    class Step1 implements Step {
        
        private Map<String,String> templates;
        
        public String[] getActionNames() {
            return new String[] {BACK, FINISH, CANCEL};
        }
        
        public String getMessage() {
            return StrutsUIMessages.PROJECT_ROOTFOLDER_AND_SELECT_TEMPLATE;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP2_LOCATION_AND_TEMPLATE;
        }
        
        public int prepareStep(XModelObject object) {
            XModel model = object.getModel();
            String home = XModelConstants.getHome(model);
            String templ = XModelObjectUtil.expand(home, model, null).replace('\\', '/')+"/templates"; //$NON-NLS-1$
            templates = NewWebProjectHelper.getTemplates(p.getProperty("version"), templ); //$NON-NLS-1$
            setConstraint(getEntityData()[1], "template", new ArrayList<String>(templates.keySet())); //$NON-NLS-1$
            String location = p.getProperty("location"); //$NON-NLS-1$
            String template = p.getProperty("template"); //$NON-NLS-1$
            if (location != null) {
                setAttributeValue(1, "location", location); //$NON-NLS-1$
            } else {
                XModelObject tomcat = model.getRoot("Options").getChildByPath("tomcat server"); //$NON-NLS-1$ //$NON-NLS-2$
                File webapps = null;
                if (tomcat != null) {
                    String root = tomcat.getAttributeValue("root dir"); //$NON-NLS-1$
                    if (root != null && root.length() > 0) {
                        root = XModelObjectUtil.expand(root, model, null).replace('\\', '/');
                        webapps = new File(root, "webapps"); //$NON-NLS-1$
                    }
                }
                if (webapps == null) {
                    webapps = new File(home, "tomcat/webapps"); //$NON-NLS-1$
                }
                if (webapps.exists() && webapps.isDirectory()) {
                    setAttributeValue(1, "location", webapps.getAbsolutePath()+File.separator+p.getProperty("name")); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            if(template == null || template.length() == 0) {
                if(templates.containsKey("Blank")) template = "Blank"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (template != null) setAttributeValue(1, "template", template); //$NON-NLS-1$
            return 0;
        }
        
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(1);
            String location = p2.getProperty("location"); //$NON-NLS-1$
            String template = p2.getProperty("template"); //$NON-NLS-1$

            File webDir = new File(location);
            File[] list = webDir.listFiles();
            if (list != null && list.length > 0) {
                int i = object.getModel().getService().showDialog(getDescription(),
                        NLS.bind(StrutsUIMessages.FOLDER_EXISTS_AND_ISNOT_EMPTY, location),
                        new String[] {StrutsUIMessages.YES, StrutsUIMessages.NO}, null, 0);
                if (i == 1 || i < 0) return 0;
            }
            p.setProperty("location", location); //$NON-NLS-1$
            p.setProperty("template", template); //$NON-NLS-1$
            p.setProperty("templateURL", (String)templates.get(template)); //$NON-NLS-1$
            return 1;
        }
        
        public int undoStep(XModelObject object) { return -1; }
    }
    
}
