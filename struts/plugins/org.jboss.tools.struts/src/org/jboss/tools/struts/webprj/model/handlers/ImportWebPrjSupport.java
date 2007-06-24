/*
 * ImportWebPrjSupport.java
 *
 * Created on February 12, 2003, 12:30 PM
 */

package org.jboss.tools.struts.webprj.model.handlers;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectHelper;

import java.io.*;
import java.util.*;

import org.eclipse.osgi.util.NLS;

/**
 *
 * @author  valera
 */
public class ImportWebPrjSupport extends WebPrjSupport {
    
    private static String invalidFolderMsg = StrutsUIMessages.ENTER_PATH_TO_EXISTING_FOLDER;

    protected Throwable exc = null;
    protected Step[] steps = new Step[] {new Step0(), new Step1()};
//    private static NewWebProjectHelper helper = new NewWebProjectHelper();
    
    /** Creates a new instance of ImportWebPrjSupport */
    public ImportWebPrjSupport() {
    }
    
    protected void reset() {
        super.reset();
    }
    
    public String getDescription() {
        return StrutsUIMessages.IMPORT_WEB_PROJECT;
    }
    
    public Step getStep(int step) {
        return steps[step];
    }
    
    public String getAttributeMessage(int stepId, String attrname) {
        if (stepId == 0) {
            return Character.toUpperCase(attrname.charAt(0))+attrname.substring(1)+"*"; //$NON-NLS-1$
        } else {
            return super.getAttributeMessage(stepId, attrname);
        }
    }

    class Step0 implements Step {
        
        public String[] getActionNames() {
            return new String[] {NEXT, FINISH, CANCEL};
        }
        
        String MESSAGE = StrutsUIMessages.ENTER_PROJECT_NAME_AND_SELECT_THE_FOLDER;
        String HTML_MESSAGE = "<html><body><font style=\"font-family:arial;font-size:12;\">" + //$NON-NLS-1$
		                      MESSAGE + "</font></body></html>"; //$NON-NLS-1$
        
        public String getMessage() {
            return MESSAGE;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP1_PROJECTNAME_AND_LOCATION;
        }
        
        public int prepareStep(XModelObject object) {
            String name = p.getProperty("name", ""); //$NON-NLS-1$ //$NON-NLS-2$
            String location = p.getProperty("location", ""); //$NON-NLS-1$ //$NON-NLS-2$
            setAttributeValue(0, "name", name); //$NON-NLS-1$
            setAttributeValue(0, "location", location); //$NON-NLS-1$
            return 0;
        }
        
        public int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(0);
            String name = p2.getProperty("name"); //$NON-NLS-1$
            String location = p2.getProperty("location"); //$NON-NLS-1$
            File webInfDir = new File(location);
            if (webInfDir.isFile()) {
                webInfDir = webInfDir.getParentFile();
                location = webInfDir.getAbsolutePath();
            }
            p.setProperty("name", name); //$NON-NLS-1$
            Object prevloc = p.setProperty("location", location); //$NON-NLS-1$
            if (!webInfDir.exists() || !webInfDir.isDirectory()) {
                throw new RuntimeException(StrutsUIMessages.SPECIFY_EXISTING_FOLDER);
            }
            String wrk = NewWebProjectHelper.getWorkspace(webInfDir);
            if (wrk != null) {
                int i = object.getModel().getService().showDialog(action.getDisplayName(),
                        NLS.bind(StrutsUIMessages.FOLDER_ALREADY_CONTAINS_ADOPTED_PROJECT, location), //$NON-NLS-2$
                        new String[] {BACK, StrutsUIMessages.REOPEN, StrutsUIMessages.OVERWRITE}, null, ServiceDialog.WARNING);
                if (i <= 0) return 0;
                if (i == 1) {
					if(true) throw new Exception("Method Removed"); //$NON-NLS-1$
///					NewWebProjectHelper.switchWorkspace(target.getModel(), wrk);
                    p.setProperty("canceled", "true"); //$NON-NLS-1$ //$NON-NLS-2$
                    setFinished(true);
                    return 0;
                }
				NewWebProjectHelper.removeWorkspace(webInfDir);
            }
            if (!location.equals(prevloc) || p.getProperty("webroot") == null) { //$NON-NLS-1$
            	throw new Exception("Method Removed"); //$NON-NLS-1$
///                helper.setDefaultFolders(location, p);
            }
            return 1;
        }
        
        public int undoStep(XModelObject object) {
            return -1;
        }
    }
    
    class Step1 implements Step {
        
        public String[] getActionNames() {
            return new String[] {BACK, FINISH, CANCEL};
        }
        
        public String getMessage() {
            return "Enter project's folders."; //$NON-NLS-1$
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP2_FOLDERS;
        }
        
        public int prepareStep(XModelObject object) {
            String webroot = p.getProperty("webroot"); //$NON-NLS-1$
            String classes = p.getProperty("classes"); //$NON-NLS-1$
            String lib = p.getProperty("lib"); //$NON-NLS-1$
            String src = p.getProperty("src"); //$NON-NLS-1$
            setAttributeValue(1, "webroot", webroot); //$NON-NLS-1$
            setAttributeValue(1, "classes", classes); //$NON-NLS-1$
            setAttributeValue(1, "lib", lib); //$NON-NLS-1$
            setAttributeValue(1, "src", src); //$NON-NLS-1$
            return 0;
        }
        
        
        private void checkFolder(String folder, String msg) {
            if (folder == null || folder.length() == 0) return;
            File file = new File(folder);
            if (!file.exists() || !file.isDirectory())
                throw new RuntimeException(msg);
        }
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(1);
            String webroot = p2.getProperty("webroot").trim(); //$NON-NLS-1$
            String classes = p2.getProperty("classes").trim(); //$NON-NLS-1$
            String lib = p2.getProperty("lib").trim(); //$NON-NLS-1$
            String src = p2.getProperty("src").trim(); //$NON-NLS-1$

            checkFolder(webroot, invalidFolderMsg+getAttributeMessage(1, "webroot")); //$NON-NLS-1$
            checkFolder(classes, invalidFolderMsg+getAttributeMessage(1, "classes")); //$NON-NLS-1$
            checkFolder(lib, invalidFolderMsg+getAttributeMessage(1, "lib")); //$NON-NLS-1$
            checkFolder(src, invalidFolderMsg+getAttributeMessage(1, "src")); //$NON-NLS-1$
            
            p.setProperty("webroot", webroot); //$NON-NLS-1$
            p.setProperty("classes", classes); //$NON-NLS-1$
            p.setProperty("lib", lib); //$NON-NLS-1$
            p.setProperty("src", src); //$NON-NLS-1$
            return 1;
        }
        
        public int undoStep(XModelObject object) { return -1; }
    }
    
}
