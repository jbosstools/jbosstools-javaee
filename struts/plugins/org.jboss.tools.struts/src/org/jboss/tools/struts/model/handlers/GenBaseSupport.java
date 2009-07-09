/*
 * GenBaseSupport.java
 *
 * Created on March 27, 2003, 15:10 PM
 */

package org.jboss.tools.struts.model.handlers;

import java.io.*;
import java.util.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.struts.webprj.model.handlers.WebPrjSupport;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;

/**
 *
 * @author  valera
 */
public abstract class GenBaseSupport extends WebPrjSupport implements StrutsConstants {
    
    public static final String GENERATE = StrutsUIMessages.GenBaseSupport_Generate;
    public static final String STOP = StrutsUIMessages.GenBaseSupport_Stop;
    
    @SuppressWarnings("nls")
	public static final String DEFAULT_ACTION_IMPORTS =
        "java.io.*,"+
        "javax.servlet.RequestDispatcher,"+
        "javax.servlet.ServletException,"+
        "javax.servlet.http.HttpServletRequest,"+
        "javax.servlet.http.HttpSession,"+
        "javax.servlet.http.HttpServletResponse,"+
        "org.apache.struts.action.Action,"+
        "org.apache.struts.action.ActionError,"+
        "org.apache.struts.action.ActionErrors,"+
        "org.apache.struts.action.ActionForm,"+
        "org.apache.struts.action.ActionForward,"+
        "org.apache.struts.action.ActionMapping,"+
        "org.apache.struts.action.ActionServlet,"+
        "org.apache.struts.util.MessageResources";

    @SuppressWarnings("nls")
	public static final String DEFAULT_FORMBEAN_IMPORTS =
        "javax.servlet.http.HttpServletRequest,"+
        "org.apache.struts.action.ActionError,"+
        "org.apache.struts.action.ActionErrors,"+
        "org.apache.struts.action.ActionForm,"+
        "org.apache.struts.action.ActionMapping";
    
    public static final String DEFAULT_FORWARD_IMPORTS =
        "org.apache.struts.action.ActionForward"; //$NON-NLS-1$
    
    public static final String DEFAULT_EXCEPTION_IMPORTS =
        "org.apache.struts.action.ActionError"; //$NON-NLS-1$

    public static final String DYNA_ACTION_FORM =
        "org.apache.struts.action.DynaActionForm"; //$NON-NLS-1$
    
    protected Throwable exc = null;
    protected Step[] steps = null;
            
    protected String output_fs = null;
    protected String base_pack = null;
    protected boolean overwrite = false;
    protected boolean actions = false;
    protected boolean formbeans = false;
    protected boolean forwards = false;
    protected boolean exceptions = false;
    protected String action_imports = DEFAULT_ACTION_IMPORTS;;
    protected String formbean_imports = DEFAULT_FORMBEAN_IMPORTS;
    protected String forward_imports = DEFAULT_FORWARD_IMPORTS;
    protected String exception_imports = DEFAULT_EXCEPTION_IMPORTS;
    protected boolean action_props = true;
    protected boolean formbean_props = true;
    protected boolean forward_props = true;
    protected boolean exception_props = true;
    protected boolean action_locforw = true;
    protected boolean action_globforw = true;
    protected String action_base = null;
    protected String formbean_base = null;
    protected String forward_base = null;
    protected String exception_base = null;
    protected String message = ""; //$NON-NLS-1$
    
    /** Creates a new instance of GenBaseSupport */
    public GenBaseSupport() {
    }

    protected abstract void generate() throws Exception;
    
	public String getDefaultActionName(int stepId) {
			String a = super.getDefaultActionName(stepId);
			if(a != null) return a;
			String[] actions = getActionNames(stepId);
			if(actions == null || actions.length == 0) return null;
			Set<String> set = new HashSet<String>();
			for (int i = 0; i < actions.length; i++) set.add(actions[i]);
			if(set.contains(GENERATE)) return GENERATE;
			return null;
	}
	
	protected DefaultWizardDataValidator genInputValidator = new GenInputValidator();
    
	public WizardDataValidator getValidator(int step) {
		genInputValidator.setSupport(this, step);
		return genInputValidator;    	
	}

	class GenInputValidator extends DefaultWizardDataValidator {
		public boolean isCommandEnabled(String command) {
			if(GenInputValidator.this.message == null) return true; 
			if(OK.equals(command) || NEXT.equals(command) || FINISH.equals(command) || GENERATE.equals(command)) return false;
			return true;
		}	
	}

    protected StrutsGenerator createStrutsGenerator(XModelObject fs, int overwrite) {
        String location = XModelObjectUtil.getExpandedValue(fs, "location", null); //$NON-NLS-1$
        File root = new File(location);
        return new StrutsGenerator(root, overwrite);
    }
    
    protected void writeLog(String msg, String className) {
        String path = "FileSystems/"+output_fs+"/"+className.replace('.', '/')+".java"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        target.getModel().getOut().println(msg + FindObjectHelper.makeRef(path, className));
    }
    
    public void action(String name) throws XModelException {
        if (GENERATE.equals(name)) {
            doStep(target, getStepId());
            setStepId(steps.length-2);
            prepareStep(target, getStepId());
        } else {
            super.action(name);
        }
    }

    protected void reset() {
        int ver = StrutsGenerator.getVersion(target);
        if (ver == 11) {
            action_imports = DEFAULT_ACTION_IMPORTS+","+DYNA_ACTION_FORM; //$NON-NLS-1$
        } else {
            action_imports = DEFAULT_ACTION_IMPORTS;
        }
		output_fs = null;
        while(output_fs == null || output_fs.length() == 0) {
			prepareOutputFS();
			if(output_fs == null || output_fs.length() == 0) {
				String mod = StrutsProcessStructureHelper.instance.getProcessModule(getTarget());
				if(mod == null || mod.length() == 0) mod = "<default>"; //$NON-NLS-1$
			
				ServiceDialog d = getTarget().getModel().getService();
				String msg = StrutsUIMessages.SOURCE_FOLDER_FOR_MODULE_ISNOT_FOUND + mod + ""; //$NON-NLS-1$
				int q = d.showDialog(StrutsUIMessages.ERROR, msg, new String[]{OK, CANCEL}, null, ServiceDialog.ERROR);
				if(q != 0) {
					setFinished(true);
					return; 
				}
				Properties p = new Properties();
				XActionInvoker.invoke("SynchronizeModules", getTarget().getModel().getByPath("FileSystems"), p); //$NON-NLS-1$ //$NON-NLS-2$
				if("true".equals(p.getProperty("cancel"))) { //$NON-NLS-1$ //$NON-NLS-2$
					setFinished(true);
					return;
				}
			}
    	}
		super.reset();
    }
    
    public String getDescription() {
        return "Generate Java Code"; //$NON-NLS-1$
    }
    
    public Step getStep(int step) {
        return steps[step];
    }
    
    public String getAttributeMessage(int stepId, String attrname) {
        if (stepId == steps.length-1) {
            return ""; //$NON-NLS-1$
        } else if ("properties".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_JAVABEAN_PROPERTIES;
        } else if ("local forwards".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CONSTANTS_FOR_LOCAL_FORWARDS;
        } else if ("global forwards".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CONSTANTS_FOR_GLOBAL_FORWARDS;
        } else if ("forwards".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CLASSES_FOR_FORWARDS;
        } else if ("actions".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CLASSES_FOR_ACTIONS;
        } else if ("formbeans".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CLASSES_FOR_FORMBEANS;
        } else if ("exceptions".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.GENERATE_CLASSES_FOR_EXCEPTIONS;
        } else if ("overwrite".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.OVERWRITE_EXISTING_FILES;
        } else if ("base package".equals(attrname)) { //$NON-NLS-1$
            return StrutsUIMessages.BASE_PACKAGE_FROM_WHICH_TO_START_CODE_GENERATION;
        }
        return super.getAttributeMessage(stepId, attrname);
    }

    protected void prepareOutputFS() {
        String mod = StrutsProcessStructureHelper.instance.getProcessModule(getTarget());
        XModelObject m = getTarget().getModel().getByPath("Web/" + mod.replace('/', '#')); //$NON-NLS-1$
        XModelObject fs = getSrcFileSystem(m);
        if(fs == null) {
            m = getTarget().getModel().getByPath("Web/" + mod.replace('/', '#')); //$NON-NLS-1$
            fs = getSrcFileSystem(m);
        }
        if(fs == null) fs = getTarget().getModel().getByPath("FileSystems/src"); //$NON-NLS-1$
        String fsn = (fs == null) ? "" : fs.getAttributeValue("name"); //$NON-NLS-1$ //$NON-NLS-2$
        ////setAttributeValue(0, "output path", fsn);
        output_fs = fsn;
    }

    private static XModelObject getSrcFileSystem(XModelObject m) {
        if(m == null) return null;
        String fsn = m.getAttributeValue(WebModuleConstants.ATTR_SRC_FS);
        return (fsn.length() == 0) ? null : m.getModel().getByPath("FileSystems/" + fsn); //$NON-NLS-1$
    }

    class StepGen implements Step {
        
        public String[] getActionNames() {
            return new String[] {STOP};
        }
        
        public String getMessage() {
            return StrutsUIMessages.GENERATING_JAVA_CODE;
        }
        
        public String getTitle() {
            return StrutsUIMessages.GENERATION;
        }
        
        public synchronized int prepareStep(XModelObject object) {
//            startThread(new Runnable() {
//                public void run() {
                    exc = null;
                    synchronized (StepGen.this) {
                        try {
                            //Thread.sleep(500);
                            generate();
                        } catch (ThreadDeath e) {
                            exc = new RuntimeException(StrutsUIMessages.GENERATION_INTERRUPTED);
                            throw e;
                        } catch (Exception e) {
                            exc = e;
                            StrutsModelPlugin.getPluginLog().logError(e);
                        }
                    }
                    fireCommand(NEXT);
//                }
//            });
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws XModelException {
            if (exc != null) {
                setStepId(getStepId()+1);
                if(exc instanceof XModelException) {
                	throw (XModelException)exc;
                }
                if (exc instanceof Exception) {
                    throw new XModelException(exc);
                }
                throw new RuntimeException(exc.getMessage());
            }
            return 1;
        }
        
        public int undoStep(XModelObject object) {
            return -1;
        }
    }
    
    class StepFinish implements Step {
        
        public String[] getActionNames() {
            if(exc != null) return new String[]{BACK, CANCEL, HELP};
            return new String[] {FINISH, HELP};
        }
        
        public String getMessage() {
            return "Generation "+(exc == null ? StrutsUIMessages.FINISHED : StrutsUIMessages.FAILED); //$NON-NLS-1$
        }
        
        public String getTitle() {
            return exc == null ? StrutsUIMessages.FINISH : StrutsUIMessages.ERROR;
        }
        
        public int prepareStep(XModelObject object) {
            if (exc != null) {
                message = exc.getMessage();
            }
            setAttributeValue(getStepId(), "message", message); //$NON-NLS-1$
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws XModelException {
            return 1;
        }
        
        public int undoStep(XModelObject object) {
            if(exc != null) return -getStepId();
            return 0;
        }
    }
}
