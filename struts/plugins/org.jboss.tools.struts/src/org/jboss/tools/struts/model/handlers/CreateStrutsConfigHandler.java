/*
 * CreateStrutsConfigHandler.java
 *
 * Created on March 11, 2003, 5:00 PM
 */

package org.jboss.tools.struts.model.handlers;

import org.jboss.tools.common.model.project.Watcher;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

import java.util.*;

import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebModuleImpl;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.common.model.filesystems.impl.CreateFileHandler;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.struts.webprj.model.helpers.sync.*;

/**
 *
 * @author  valera
 */
public class CreateStrutsConfigHandler extends CreateFileHandler implements StrutsConstants {
    private Set<String> modules = new HashSet<String>();
    private Set<String> incompleteModules = new HashSet<String>();
    private String module;
    private XModelObject created = null;

    public CreateStrutsConfigHandler() {}

    public XEntityData[] getEntityData(XModelObject object) {
        super.getEntityData(object);
        modules.clear();
		incompleteModules.clear();
        modules.addAll(WebModulesHelper.getInstance(object.getModel()).getModules());
        XModelObject[] ws = object.getModel().getByPath("Web").getChildren();
        for (int i = 0; i < ws.length; i++) {
        	String p = ws[i].getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH);
        	if(p == null || p.length() == 0 || object.getModel().getByPath(p) == null)
				incompleteModules.add(ws[i].getAttributeValue(WebModuleConstants.ATTR_NAME));
        }
        Set<String> names = new HashSet<String>();
        XModelObject[] cs = object.getChildren();
        for (int i = 0; i < cs.length; i++) {
        	String entity = cs[i].getModelEntity().getName();
        	if(!entity.startsWith(ENT_STRUTSCONFIG)) continue;
        	if(entity.equals(ENT_STRUTSCONFIG + VER_SUFFIX_10)) continue;
            names.add(cs[i].getAttributeValue("name"));
        }
        if(modules.size() <= incompleteModules.size() && cs.length == 0) {
            HUtil.find(data, 0, "module").setValue("");
            HUtil.find(data, 0, "name").setValue("struts-config");
            return data;
        }
        String module = "/mod";
        int i = 1;
        while(modules.contains(module + i) && !incompleteModules.contains(module + i)) ++i;
        module = module + i;
        HUtil.find(data, 0, "module").setValue(module);
        String name = "struts-config-" + (module).substring(1), namef = name;
        i = 0;
        while(names.contains(namef)) namef = name + (++i);
        HUtil.find(data, 0, "name").setValue(namef);
        return data;
    }


    public void executeHandler(XModelObject object, Properties prop) throws Exception {
    	Properties p = extractProperties(data[0]);
		checkRegister(object, p);
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Create struts config 1.1 in "+object.getAttributeValue("element type")+" "+object.getPresentationString(), XTransactionUndo.ADD);
        undo.addUndoable(u);
        try {
            super.executeHandler(object, prop);
            if(created != null) {
				StrutsProcessImpl process = (StrutsProcessImpl)created.getChildByPath("process");
				process.firePrepared();
			}            
            register(object, prop);
			SortFileSystems.sort(object.getModel());
        } catch (RuntimeException e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
            created = null;
        }
    }
    
    private void checkRegister(XModelObject object, Properties p) throws Exception {
		boolean register = "yes".equals(extractProperties(data[0]).getProperty("register in web.xml")); //$NON-NLS-1$ //$NON-NLS-2$
		if(!register) return;
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml == null) throw new Exception (StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_ISNOT_FOUND);
		if("yes".equals(webxml.get("isIncorrect"))) throw new Exception (StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_IS_INCORRECT); //$NON-NLS-1$ //$NON-NLS-2$
		if(!webxml.isObjectEditable()) throw new Exception (StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_IS_READONLY);
    }

    private void register(XModelObject object, Properties prop) throws Exception {
		String uri = "/WEB-INF/" + FileAnyImpl.toFileName(created);
		XModelObject m = object.getModel().getByPath("Web/" + module.replace('/', '#'));
    	if(incompleteModules.contains(module)) {
    		if(m != null && uri.equals(m.getAttributeValue(WebModuleConstants.ATTR_URI))) {
    			m.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_MODEL_PATH, "" + XModelObjectLoaderUtil.getResourcePath(created));
    		}
    	} else {
			if(m == null) {
				SetupModuleHandler.setupModule(created.getChildByPath(ELM_PROCESS), module, null); 
				m = object.getModel().getByPath("Web/" + module.replace('/', '#'));
			}
		}
        boolean register = "yes".equals(extractProperties(data[0]).getProperty("register in web.xml"));
        if(!register) return;
        String uri2 = StrutsWebHelper.registerConfig(object.getModel(), module, uri);
        if(m != null && uri.equals(uri2)) {
        	object.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_URI, uri);
        } else if(m != null) {
        	WebModuleImpl mi = (WebModuleImpl)m;
        	mi.setURI(uri2);
        	XModelObject c = mi.getChildByPath(uri.replace('/', '#'));
        	if(c != null) c.setAttributeValue(WebModuleConstants.ATTR_MODEL_PATH, "" + XModelObjectLoaderUtil.getResourcePath(created));
        }
        Watcher.getInstance(object.getModel()).forceUpdate();
        XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml != null && webxml.isModified()) {
			XActionInvoker.invoke("SaveActions.Save", webxml, null);
		}
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        module = p.getProperty("module");
        if(module != null && module.length() > 0 && !module.startsWith("/")) {
            module = "/" + module; p.setProperty("module", module);
        }
/*
        if(modules.contains(module) && !incompleteModules.contains(module)) {
            String mes = (module.length() == 0) ? "Folder contains main config file."
                         : "Folder contains config file for module " + module + ".";
            throw new RuntimeException(mes);
        }
*/
    }

    protected XModelObject modifyCreatedObject(XModelObject o) {
        return created = o;
    }

}

