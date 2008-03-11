/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.files.handlers.*;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.model.project.Watcher;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebModuleImpl;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.SortFileSystems;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;

public class CreateStrutsConfigSupport extends CreateFileSupport implements StrutsConstants {
	private Set<String> modules = new HashSet<String>();
	private Set<String> incompleteModules = new HashSet<String>();
	private String module;

	public void reset() {
		super.reset();
		initDefaultName();
//		initRegister();
	}
	
	public void initDefaultName() {
		modules.clear();
		incompleteModules.clear();
		modules.addAll(WebModulesHelper.getInstance(getTarget().getModel()).getModules());
		XModelObject web = getTarget().getModel().getByPath("Web"); //$NON-NLS-1$
		if(web == null) return;
		XModelObject[] ws = web.getChildren(WebModulesHelper.ENT_STRUTS_WEB_MODULE);
		for (int i = 0; i < ws.length; i++) {
			String p = ws[i].getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH);
			if(p == null || p.length() == 0 || getTarget().getModel().getByPath(p) == null)
				incompleteModules.add(ws[i].getAttributeValue(WebModuleConstants.ATTR_NAME));
		}
		Set<String> names = new HashSet<String>();
        XModelObject[] cs = getTarget().getChildren();
        for (int i = 0; i < cs.length; i++) {
        	String entity = cs[i].getModelEntity().getName();
        	if(!entity.startsWith(ENT_STRUTSCONFIG)) continue;
        	if(entity.equals(ENT_STRUTSCONFIG + VER_SUFFIX_10)) continue;
			names.add(cs[i].getAttributeValue("name")); //$NON-NLS-1$
		}
		if(modules.size() <= incompleteModules.size() && names.size() == 0) {
			setAttributeValue(0, "module", ""); //$NON-NLS-1$ //$NON-NLS-2$
			setAttributeValue(0, "name", "struts-config"); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		String module = "/mod"; //$NON-NLS-1$
		int i = 1;
		while(modules.contains(module + i) && !incompleteModules.contains(module + i)) ++i;
		module = module + i;
		setAttributeValue(0, "module", module); //$NON-NLS-1$
		String name = "struts-config-" + (module).substring(1), namef = name; //$NON-NLS-1$
		i = 0;
		while(names.contains(namef)) namef = name + (++i);
		setAttributeValue(0, "name", namef); //$NON-NLS-1$
	}

	protected void execute() throws Exception {
		Properties p0 = extractStepData(0);
		XUndoManager undo = getTarget().getModel().getUndoManager();
		XTransactionUndo u = new XTransactionUndo(StrutsUIMessages.CREATE_STRUTS_CONFIG + getTarget().getAttributeValue("element type")+" "+getTarget().getPresentationString(), XTransactionUndo.ADD); //$NON-NLS-2$ //$NON-NLS-3$
		undo.addUndoable(u);
		try {
			doExecute(p0);
		} catch (RuntimeException e) {
			undo.rollbackTransactionInProgress();
			throw e;
		} finally {
			u.commit();
		}
	}
	
	private void doExecute(Properties p0) throws Exception {
		Properties p = extractStepData(0);
		String path = p.getProperty("name"); //$NON-NLS-1$
		path = revalidatePath(path);
		XModelObject file = createFile(path);
		if(file == null) return;		

		StrutsProcessImpl process = (StrutsProcessImpl)file.getChildByPath("process"); //$NON-NLS-1$
		process.firePrepared();

		register(file, p0);
		SortFileSystems.sort(getTarget().getModel());

		final XModelObject q = file;
		open(q);
	}

	protected XModelObject modifyCreatedObject(XModelObject o) {
		module = getAttributeValue(0, "module"); //$NON-NLS-1$
		if(module != null && module.length() > 0 && !module.startsWith("/")) { //$NON-NLS-1$
			module = "/" + module; //$NON-NLS-1$
			setAttributeValue(0, "module", module); //$NON-NLS-1$
		}
		return o;
	}
	
	private String checkRegister(XModelObject object, String register) {
		if(!"yes".equals(register)) return null; //$NON-NLS-1$
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml == null) return StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_ISNOT_FOUND;
		if("yes".equals(webxml.get("isIncorrect"))) return StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_IS_INCORRECT; //$NON-NLS-1$ //$NON-NLS-2$
		if(!webxml.isObjectEditable()) return StrutsUIMessages.MODULE_CANNOT_BE_REGISTERED_IS_READONLY;
		return null;
	}

	private void register(XModelObject object, Properties prop) throws Exception {
		String uri = getURI(object);
		XModelObject m = object.getModel().getByPath("Web/" + module.replace('/', '#')); //$NON-NLS-1$
		if(incompleteModules.contains(module)) {
			if(m != null && uri.equals(m.getAttributeValue(WebModuleConstants.ATTR_URI))) {
				m.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_MODEL_PATH, "" + XModelObjectLoaderUtil.getResourcePath(object)); //$NON-NLS-1$
			}
		} else {
			if(m == null) {
				SetupModuleHandler.setupModule(object.getChildByPath(ELM_PROCESS), module, null); 
				m = object.getModel().getByPath("Web/" + module.replace('/', '#')); //$NON-NLS-1$
			}
		}
		boolean register = "yes".equals(getAttributeValue(0, "register in web.xml")); //$NON-NLS-1$ //$NON-NLS-2$
		if(!register) return;
		String uri2 = StrutsWebHelper.registerConfig(object.getModel(), module, uri);
		if(m != null && uri.equals(uri2)) {
			object.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_URI, uri);
		} else if(m != null) {
			WebModuleImpl mi = (WebModuleImpl)m;
			mi.setURI(uri2);
			XModelObject c = mi.getChildByPath(uri.replace('/', '#'));
			if(c != null) c.setAttributeValue(WebModuleConstants.ATTR_MODEL_PATH, "" + XModelObjectLoaderUtil.getResourcePath(object)); //$NON-NLS-1$
		}
		Watcher.getInstance(object.getModel()).forceUpdate();
		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		if(webxml != null && webxml.isModified()) {
			XActionInvoker.invoke("SaveActions.Save", webxml, null); //$NON-NLS-1$
		}
	}

	//same as in CreateFacesConfigSupport
	private String getURI(XModelObject file) {
		String result = "/" + FileAnyImpl.toFileName(file); //$NON-NLS-1$
		XModelObject o = file.getParent();
		while(o != null && o.getFileType() != XModelObject.SYSTEM) {
			result = "/" + o.getAttributeValue("name") + result; //$NON-NLS-1$ //$NON-NLS-2$
			o = o.getParent();
		}
		if(o == null || !"WEB-ROOT".equals(o.getAttributeValue("name"))) { //$NON-NLS-1$ //$NON-NLS-2$
			result = "/WEB-INF" + result; //$NON-NLS-1$
		}
		return result;
	}

	protected DefaultWizardDataValidator createValidator() {
		return new CreateStrutsConfigValidator(); 
	}
	
	class CreateStrutsConfigValidator extends CreateFileSupport.Validator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			message = checkRegister(getTarget(), data.getProperty("register in web.xml")); //$NON-NLS-1$
		}
	}

}
