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
package org.jboss.tools.seam.pages.xml.model.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jst.jsp.ui.internal.JSPUIPlugin;
import org.eclipse.jst.jsp.ui.internal.templates.TemplateContextTypeIdsJSP;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.WizardDataValidator;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLMessages;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLPlugin;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;

public class AddViewSupport extends SpecialWizardSupport implements SeamPagesConstants {
	public static String JSF_ADD_VIEW_PATH = ""; //preference name
	static String LAST_CREATE_FILE_PREFERENCE = "org.jboss.tools.jsf.lastCreateFileValue";
	XModelObject sample;
	
	Map<String, Template> templates = null;
	
	public AddViewSupport() {}

	public void reset() {
		sample = (XModelObject)getProperties().get("sample");
		if(sample != null) {
			setAttributeValue(0, ATTR_VIEW_ID, sample.getAttributeValue(ATTR_PATH));
		}
			
		String[] s = templates.keySet().toArray(new String[0]);
		setValueList(0, "template", s);
		//take from preferences
		setAttributeValue(0, "template", getDefaultTemplate(s));

			//TODO combine this feature with jsf
		String last = SeamPagesXMLPlugin.getDefault().getPluginPreferences().getString(LAST_CREATE_FILE_PREFERENCE);
		if(last == null || last.length() == 0) {
			last = "true";
		} else if(!"true".equals(last)) {
			last = "false"; 
		}
		setAttributeValue(0, "create file", last);
	}
	
	TemplateStore getTemplateStore() {
		return JSPUIPlugin.getInstance().getTemplateStore();
	}
	
	String getTemplateString(String templateName) {
		if(templateName == null) return null;
		String templateString = null;

		Template template = templates.get(templateName);
		if (template != null) {
			TemplateContextType contextType =JSPUIPlugin.getInstance().getTemplateContextRegistry().getContextType(TemplateContextTypeIdsJSP.NEW);
			IDocument document = new Document();
			TemplateContext context = new DocumentTemplateContext(contextType, document, 0, 0);
			try {
				TemplateBuffer buffer = context.evaluate(template);
				templateString = buffer.getString();
			}
			catch (Exception e) {
				SeamPagesXMLPlugin.getDefault().logError("Could not create template for new html", e); //$NON-NLS-1$
			}
		}

		return templateString;
	}

	void loadTemplates() {
		templates = new TreeMap<String, Template>();
		Template[] ts = getTemplateStore().getTemplates(TemplateContextTypeIdsJSP.NEW);
		for (Template t: ts) {
			templates.put(t.getName(), t);
		}
	}
	
	static XModelObject getPreferenceObject() {
		return PreferenceModelUtilities.getPreferenceModel().getByPath(JSF_ADD_VIEW_PATH);
	}
	
	public String getDefaultTemplate(String[] list) {
		if(list.length == 0) return "";
		XModelObject addView = getPreferenceObject();
		String v = (addView == null) ? "" : addView.getAttributeValue("Page Template");
		if(v != null) for (int i = 0; i < list.length; i++) if(v.equals(list[i])) return list[i];
		return list[0];
	}
	
	public static String getExtension(String template) {
		if(template != null) {
			int i = template.trim().lastIndexOf('.');
			if(i > 0) {
				return template.trim().substring(i);
			}
		}
		//Let user specify extension
		return ""; //getExtension();
	}

	public static String getExtension() {
		XModelObject addView = getPreferenceObject();
		String v = (addView == null) ? "" : addView.getAttributeValue("Extension");
		if(v == null || v.length() == 0) return ".xhtml";
		if(!v.startsWith(".")) v = "." + v;
		return v;
	}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		}
	}

	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}

	protected void execute() throws XModelException {
		boolean doNotCreateEmptyRule = false; //"yes".equals(JSFPreference.DO_NOT_CREATE_EMPTY_RULE.getValue());
		Properties p = extractStepData(0);
		String path = p.getProperty(ATTR_VIEW_ID);
		path = revalidatePath(path, getAttributeValue(0, "template"));
		
		createFile(path);

		XModelObject m = getTarget().getParent().getChildByPath(FOLDER_PAGES);
		String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(path);
		boolean isPattern = SeamPagesDiagramHelper.isPattern(path);
		boolean existsR = m.getChildByPath(path) != null; // m.getRuleCount(path) != 0;
		boolean existsV = findView(path) != null;
		
		boolean exists = existsR || existsV;
		if(exists) {
			ServiceDialog d = getTarget().getModel().getService();
			String message = NLS.bind(SeamPagesXMLMessages.THE_VIEW_WITH_PATH_IS_ALREADY_CREATED, path);
			if(existsR) {
//				message += JSFUIMessages.YOU_WANT_TO_ADD_ADDITIONAL_NAVIGATION_RULE_WITH_SAME_FROM_VIEW_ID;
				//Do not support
			} else if(existsV && isPattern) {
//				message += JSFUIMessages.YOU_WANT_TO_CREATE_AN_ADDITIONAL_VIEW_WITH_THE_SAME_FROM_VIEW_ID;
				//Do not support
			} else {
//				message += JSFUIMessages.YOU_WANT_TO_CREATE_A_NAVIGATION_RULE_FOR_THIS_FROM_VIEW_ID;
				//Do not support
			}
			int q = d.showDialog(SeamPagesXMLMessages.WARNING, message, new String[]{OK/*, CANCEL*/}, null, ServiceDialog.WARNING);
//			if(q != 0) return;
			return;
		}

		XModelObject created = null;
		if(!doNotCreateEmptyRule || (exists && !isPattern) || sample != null) {
			XModelObject rule = addPage(m, path); // m.addRule(path);
			addCasesFromSample(rule);
			m.setModified(true);
			created = getTarget().getChildByPath(pp);
		} else {
			created = getTarget().getModel().createModelObject(SeamPagesConstants.ENT_DIAGRAM_ITEM, null);
			String ppi = pp;
//			if(exists && SeamPagesDiagramHelper.isPattern(path)) {
//				int index = -1;
//				while(getTarget().getChildByPath(ppi + ":" + index) != null) index--;
//				ppi = ppi + ":" + index;
//			}
			created.setAttributeValue(SeamPagesConstants.ATTR_NAME, ppi);
			created.setAttributeValue(SeamPagesConstants.ATTR_PATH, path);
			created.setAttributeValue("persistent", "true");
			DefaultCreateHandler.addCreatedObject(getTarget(), created, getProperties());
		}
		
		if(!exists || isPattern) {
			String shape = getShape();
			if(created != null && shape != null) created.setAttributeValue("shape", shape);
		}
		if(created != null) {
			FindObjectHelper.findModelObject(created, FindObjectHelper.IN_EDITOR_ONLY);
		}		
	}

	/**
	 * Adds XML object for page
	 * @param pages
	 * @param path
	 * @return
	 */
	public static XModelObject addPage(XModelObject pages, String path) {
		String childEntity = pages.getModelEntity().getChildren()[0].getName();
		XModelObject page = pages.getModel().createModelObject(childEntity, null);
		page.setAttributeValue(ATTR_VIEW_ID, path);
		pages.addChild(page);
		return page;
	}
	
	private XModelObject findView(String path) {
		String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(path);
		XModelObject o = getTarget().getChildByPath(pp);
		if(o != null) return o;
		if(!SeamPagesDiagramHelper.isPattern(path)) return null;
		XModelObject[] cs = getTarget().getChildren();
		for (int i = 0; i < cs.length; i++) {
			String p = cs[i].getPathPart();
			if(pp.equals(p) || p.startsWith(pp + ":")) return cs[i];			
		}
		return null;
	}
	
	private void addCasesFromSample(XModelObject rule) {
		if(!(sample instanceof ReferenceObject)) return;
		XModelObject rs = ((ReferenceObject)sample).getReference();
		if(rs == null) return;
		XModelObject[] cs = rs.getChildren();
		for (int j = 0; j < cs.length; j++) {
			rule.addChild(cs[j].copy());
		}
		
	}
	
	private String getShape() {
		return AddExceptionHandler.getShape(getProperties());
	}
	
	public static String revalidatePath(String path) {
		if(path != null) path = path.trim();
		if(path == null || path.length() == 0) return path;
		if(!path.startsWith("/") && !path.startsWith("*")) path = "/" + path;
		if(hasWildCard(path)) return path;
		if(path.indexOf('.') < 0 && !path.endsWith("/")) {
//			path += getExtension();
		}
		return path;
	}

	public static String revalidatePath(String path, String template) {
		if(path != null) path = path.trim();
		if(path == null || path.length() == 0) return path;
		if(!path.startsWith("/") && !path.startsWith("*")) path = "/" + path;
		if(hasWildCard(path)) return path;
		if(path.indexOf('.') < 0 && !path.endsWith("/")) {
			path += template == null ? "" : getExtension(template);
		}
		return path;
	}
	
	static boolean hasWildCard(String path) {
		return path.indexOf('*') >= 0 || path.indexOf("#{") >= 0;
	}
	
	public boolean canCreateFile(String path) {
		XModelObject fs = getTarget().getModel().getByPath("FileSystems/WEB-ROOT");
		if(fs == null) return false;
		path = revalidatePath(path, getAttributeValue(0, "template"));
		if(path == null || path.length() == 0 
			|| hasWildCard(path)) return false;
		//extension must be available
		int m = path.lastIndexOf('.');
		if(m < 0 || m >= path.length() - 1) return false;

		return isCorrectPath(path) && path.indexOf('*') < 0 && !fileExists(path)
			&& path.lastIndexOf('.') < path.length() - 1;
	}
	
	static String FORBIDDEN_INDICES = "\"\n\t\\:<>?|"; //* is allowed anywhere
	
	public static boolean isCorrectPath(String path) {
		if(path == null || path.equals("/") || path.indexOf("//") >= 0) return false;
		if(path.endsWith("/") || path.indexOf("../") >= 0) return false;
		if(path.endsWith("..")) return false;
		if(path.endsWith("*")) return true;
		for (int i = 0; i < FORBIDDEN_INDICES.length(); i++) {
			if(path.indexOf(FORBIDDEN_INDICES.charAt(i)) >= 0) {
				return false;
			}
		}				
		return true;
	}
	
	boolean fileExists(String path) {
		return getTarget().getModel().getByPath(path) != null;
	} 
	
	void createFile(String path) throws XModelException {
		if(!canCreateFile(path)) return;
		String lastCreateFileValue = getAttributeValue(0, "create file");
		SeamPagesXMLPlugin.getDefault().getPluginPreferences().setDefault(LAST_CREATE_FILE_PREFERENCE, lastCreateFileValue);
		if(!"true".equals(lastCreateFileValue)) return;
		String template = getAttributeValue(0, "template");
		if(template != null) template = template.trim();

		String location = ((FileSystemImpl)getTarget().getModel().getByPath("FileSystems/WEB-ROOT")).getAbsoluteLocation();
		location += path;
		File ft = new File(location);

		String templateString = getTemplateString(template);
		try {
			ft.createNewFile();
		} catch (IOException e) {
			SeamPagesXMLPlugin.getDefault().logError(e);
		}
		if(templateString != null) {
			FileUtil.writeFile(ft, templateString);
		}
		getTarget().getModel().update();
		try {
			EclipseResourceUtil.getResource(getTarget()).getProject().refreshLocal(IProject.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			throw new XModelException(e);
		}
	}

	protected DefaultWizardDataValidator viewValidator = new ViewValidator();
    
	public WizardDataValidator getValidator(int step) {
		viewValidator.setSupport(this, step);
		return viewValidator;    	
	}
	
	class ViewValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			String path = revalidatePath(data.getProperty(SeamPagesConstants.ATTR_VIEW_ID), data.getProperty("template"));
			if(!isCorrectPath(path)) {
				message = SeamPagesXMLMessages.ATTRIBUTE_VIEW_ID_IS_NOT_CORRECT;
			} 
			if(message != null) return;

			String template = data.getProperty("template");
			if(template != null && isFieldEditorEnabled(0, "template", data)) {
				if(template.trim().length() == 0) {
					message = SeamPagesXMLMessages.TEMPLATE_IS_NOT_SPECIFIED;
					return;
				}
				String t =  getTemplateString(template.trim());
				if(t == null) {
					message = SeamPagesXMLMessages.TEMPLATE_DOES_NOT_EXIST;
				}
			}
			if(message != null) return;

			boolean doNotCreateEmptyRule = false; //"yes".equals(JSFPreference.DO_NOT_CREATE_EMPTY_RULE.getValue());
			String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(path);
			boolean exists = getTarget().getChildByPath(pp) != null;
			if(doNotCreateEmptyRule && exists /*&& !SeamPagesDiagramHelper.isPattern(path)*/) {
				message = "View exists."; //JSFUIMessages.THE_VIEW_EXISTS;
			}
			
		}		
	}

	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		String path = values.getProperty(ATTR_VIEW_ID);
		boolean c = canCreateFile(path);
		if(name.equals("create file")) {
			return c;
		}
		boolean g = c && "true".equals(values.getProperty("create file"));
		if(name.equals("template")) {
			return g;
		}
		return true;
	}

}
