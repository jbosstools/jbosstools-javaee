/*
 * GenJavaCodeSupport.java
 *
 * Created on March 13, 2003, 12:10 PM
 */

package org.jboss.tools.struts.model.handlers;

import java.util.Properties;

import org.eclipse.core.resources.IResource;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.StrutsGenerator;

/**
 *
 * @author  valera
 */
public class GenJavaCodeSupport extends GenBaseSupport {
    
    /** Creates a new instance of GenJavaCodeSupport */
    public GenJavaCodeSupport() {
        this.steps = new Step[] {new StepStart(), new StepActions(),
            new StepFormBeans(), new StepForwards(), new StepExceptions(),
            new StepGen(), new StepFinish()};
    }

    protected void generate() throws Exception {
        XModelObject fs = target.getModel().getByPath("FileSystems/"+output_fs); //$NON-NLS-1$
        StrutsGenerator gen = createStrutsGenerator(fs, overwrite ? StrutsGenerator.OVER_TRUE : StrutsGenerator.OVER_FALSE);
        gen.setBasePackage(base_pack);
        int version = StrutsGenerator.getVersion(target);
        int[] stat = new int[4];
        if (actions) {
            XModelObject[] acts = target.getChildByPath(ELM_ACTIONMAP).getChildren();
            for (int i = 0; i < acts.length; i++) {
                String s = gen.generateAction(acts[i], action_base, action_imports, action_props, action_globforw, action_locforw);
                if (s != null) {
                    stat[0]++;
                    writeLog("Generated action: ", s); //$NON-NLS-1$
                }
                if (exceptions) {
                    XModelObject[] excs = acts[i].getChildren(ENT_EXCEPTION+version);
                    for (int j = 0; j < excs.length; j++) {
                        s = gen.generateException(excs[j], exception_base, exception_imports, exception_props);
                        if (s != null) {
                            stat[3]++;
                            writeLog("Generated exception: ", s); //$NON-NLS-1$
                        }
                    }
                }
                if (forwards) {
                    XModelObject[] forws = acts[i].getChildren(ENT_FORWARD+version);
                    for (int j = 0; j < forws.length; j++) {
                        s = gen.generateForward(forws[j], forward_base, forward_imports, forward_props);
                        if (s != null) {
                            stat[2]++;
                            writeLog("Generated forward: ", s); //$NON-NLS-1$
                        }
                    }
                }
            }
        }
        if (formbeans) {
            XModelObject[] forms = target.getChildByPath(ELM_FORMBEANS).getChildren();
            for (int i = 0; i < forms.length; i++) {
                String s = gen.generateFormBean(forms[i], formbean_base, formbean_imports, formbean_props);
                if (s != null) {
                    stat[1]++;
                    writeLog("Generated form bean: ", s); //$NON-NLS-1$
                }
            }
        }
        if (exceptions) {
            XModelObject[] excs = target.getChildByPath(ELM_GLOBALEXC).getChildren();
            for (int i = 0; i < excs.length; i++) {
                String s = gen.generateException(excs[i], exception_base, exception_imports, exception_props);
                if (s != null) {
                    stat[3]++;
                    writeLog("Generated global exception: ", s); //$NON-NLS-1$
                }
            }
        }
        if (forwards) {
            XModelObject[] forws = target.getChildByPath(ELM_GLOBALFORW).getChildren();
            for (int i = 0; i < forws.length; i++) {
                String s = gen.generateForward(forws[i], forward_base, forward_imports, forward_props);
                if (s != null) {
                    stat[2]++;
                    writeLog("Generated global forward: ", s); //$NON-NLS-1$
                }
            }
        }
        int total = stat[0] + stat[1] + stat[2] + stat[3];
        message = "Generated classes: "+total+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
        if (stat[0] != 0) {
            message += "Actions: "+stat[0]+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (stat[1] != 0) {
            message += "Form beans: "+stat[1]+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (stat[2] != 0) {
            message += "Forwards: "+stat[2]+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (stat[3] != 0) {
            message += "Exceptions: "+stat[3]+"\n"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (total > 0) {
            XModelObjectLoaderUtil.getObjectLoader(fs).update(fs);
			EclipseResourceUtil.getResource(fs).refreshLocal(IResource.DEPTH_INFINITE, null);
        }
    }
    
    protected void reset() {
        action_base = getAttributeValue(1, "base class"); //$NON-NLS-1$
        formbean_base = getAttributeValue(2, "base class"); //$NON-NLS-1$
        forward_base = getAttributeValue(3, "base class"); //$NON-NLS-1$
        exception_base = getAttributeValue(4, "base class"); //$NON-NLS-1$
        super.reset();
    }
    
    private int nextStep(int step) {
        int i = (actions ? 1 : 0) + (formbeans ? 2 : 0) +
                (forwards ? 4 : 0) + (exceptions ? 8 : 0);
        i >>= step;
        if (i == 0) return -1;
        while ((i & 1) == 0) {
            step++;
            i >>= 1;
        }
        return step+1;
    }
    
    private int prevStep(int step) {
        int i = (actions ? 8 : 0) + (formbeans ? 4 : 0) +
                (forwards ? 2 : 0) + (exceptions ? 1 : 0);
        i >>= 5 - step;
        if (i == 0) return 0;
        while ((i & 1) == 0) {
            step--;
            i >>= 1;
        }
        return step-1;
    }
    
    class StepStart implements Step {
        
        public String[] getActionNames() {
            return new String[] {NEXT, GENERATE, CANCEL, HELP};
        }
        
        public String getMessage() {
            return null;
        }
        
        public String getTitle() {
            return "Step 1"; //$NON-NLS-1$
        }
        
        public int prepareStep(XModelObject object) {
            prepareOutputFS();
            return 0;
        }
        
        public int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(0);
            actions = "true".equals(p2.getProperty("actions")); //$NON-NLS-1$ //$NON-NLS-2$
            formbeans = "true".equals(p2.getProperty("formbeans")); //$NON-NLS-1$ //$NON-NLS-2$
            forwards = "true".equals(p2.getProperty("forwards")); //$NON-NLS-1$ //$NON-NLS-2$
            exceptions = "true".equals(p2.getProperty("exceptions")); //$NON-NLS-1$ //$NON-NLS-2$
            ////output_fs = p2.getProperty("output path");
            base_pack = p2.getProperty("base package"); //$NON-NLS-1$
            overwrite = "true".equals(p2.getProperty("overwrite")); //$NON-NLS-1$ //$NON-NLS-2$
            int next = nextStep(0);
            if (next < 0) throw new RuntimeException(StrutsUIMessages.SELECT_AT_LEAST_ONE_CLASS_TYPE);
            return next;
        }
        
        public int undoStep(XModelObject object) {
            return -1;
        }
    }
    
    class StepActions implements Step {
        
        public String[] getActionNames() {
            if (nextStep(1) > 0) {
                return new String[] {BACK, NEXT, GENERATE, CANCEL, HELP};
            } else {
                return new String[] {BACK, GENERATE, CANCEL, HELP};
            }
        }
        
        public String getMessage() {
            return null;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP_2_ACTIONS;
        }
        
        public int prepareStep(XModelObject object) {
            ////setAttributeValue(1, "imports", action_imports);
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(1);
            action_base = p2.getProperty("base class"); //$NON-NLS-1$
            ////action_imports = p2.getProperty("imports");
            action_props = "true".equals(p2.getProperty("properties")); //$NON-NLS-1$ //$NON-NLS-2$
            action_locforw = "true".equals(p2.getProperty("local forwards")); //$NON-NLS-1$ //$NON-NLS-2$
            action_globforw = "true".equals(p2.getProperty("global forwards")); //$NON-NLS-1$ //$NON-NLS-2$
            return nextStep(1)-1;
        }
        
        public int undoStep(XModelObject object) {
            return prevStep(1)-1;
        }
    }
    
    class StepFormBeans implements Step {
        
        public String[] getActionNames() {
            if (nextStep(2) > 0) {
                return new String[] {BACK, NEXT, GENERATE, CANCEL, HELP};
            } else {
                return new String[] {BACK, GENERATE, CANCEL, HELP};
            }
        }
        
        public String getMessage() {
            return null;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP_3_FORMBEANS;
        }
        
        public int prepareStep(XModelObject object) {
            setAttributeValue(2, "imports", formbean_imports); //$NON-NLS-1$
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(2);
            formbean_base = p2.getProperty("base class"); //$NON-NLS-1$
            formbean_imports = p2.getProperty("imports"); //$NON-NLS-1$
            formbean_props = "true".equals(p2.getProperty("properties")); //$NON-NLS-1$ //$NON-NLS-2$
            return nextStep(2)-2;
        }
        
        public int undoStep(XModelObject object) {
            return prevStep(2)-2;
        }
    }
    
    class StepForwards implements Step {
        
        public String[] getActionNames() {
            if (nextStep(3) > 0) {
                return new String[] {BACK, NEXT, GENERATE, CANCEL, HELP};
            } else {
                return new String[] {BACK, GENERATE, CANCEL, HELP};
            }
        }
        
        public String getMessage() {
            return null;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP_4_FORWARDS;
        }
        
        public int prepareStep(XModelObject object) {
            setAttributeValue(3, "imports", forward_imports); //$NON-NLS-1$
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(3);
            forward_base = p2.getProperty("base class"); //$NON-NLS-1$
            forward_imports = p2.getProperty("imports"); //$NON-NLS-1$
            forward_props = "true".equals(p2.getProperty("properties")); //$NON-NLS-1$ //$NON-NLS-2$
            return nextStep(3)-3;
        }
        
        public int undoStep(XModelObject object) {
            return prevStep(3)-3;
        }
    }
    
    class StepExceptions implements Step {
        
        public String[] getActionNames() {
            return new String[] {BACK, GENERATE, CANCEL, HELP};
        }
        
        public String getMessage() {
            return null;
        }
        
        public String getTitle() {
            return StrutsUIMessages.STEP_5_EXCEPTIONS;
        }
        
        public int prepareStep(XModelObject object) {
            setAttributeValue(4, "imports", exception_imports); //$NON-NLS-1$
            return 0;
        }
        
        public synchronized int doStep(XModelObject object) throws Exception {
            Properties p2 = extractStepData(1);
            exception_base = p2.getProperty("base class"); //$NON-NLS-1$
            exception_imports = p2.getProperty("imports"); //$NON-NLS-1$
            exception_props = "true".equals(p2.getProperty("properties")); //$NON-NLS-1$ //$NON-NLS-2$
            return 1;
        }
        
        public int undoStep(XModelObject object) {
            return prevStep(4)-4;
        }
    }
    
}
