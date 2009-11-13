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
package org.jboss.tools.struts.model.handlers.page.create;

import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.DefaultWizardDataValidator;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.WizardDataValidator;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.undo.XTransactionUndo;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.StrutsProcessHelper;
import org.jboss.tools.struts.model.helpers.page.PageUpdateManager;

public class CreatePageSupport extends SpecialWizardSupport {
	static String LAST_CREATE_FILE_PREFERENCE = "org.jboss.tools.struts.lastCreateFileValue";
    static String ATTR_FILE_SYSTEM = "file system";
    protected CreatePageContext context = new CreatePageContext();
	protected StrutsUtils templates = new StrutsUtils();
	String lastCreateFileValue = "true";

    public CreatePageSupport() {
        context.setSupport(this);
    }

    public String[] getActionNames(int stepId) {
        return new String[]{FINISH, CANCEL};
    }

    protected void reset() {
        context.reset();
        if(context.getSelectedFileSystem() != null) {
            setStepId(1);
        } else {
            setStepId(0);
        }
        if(getStepId() > 0) setAttributeContext(getStepId(), "name", this);
        if(context.isPreselected()) {
        	try {
            	onSelection();
	        } catch (XModelException e) {
	        	StrutsModelPlugin.getPluginLog().logError("Exception caught in CreatePageSupport:reset " + e.getMessage(), e);
	        }
        } else {
			templates.updatePageTemplates();
			String[] s = templates.getPageTemplateList();
			setValueList(getStepId(), "template", s);
			if(s.length > 0) {
				setAttributeValue(getStepId(), "template", getDefaultTemplate(s));
			}
			String last = StrutsModelPlugin.getDefault().getPreferenceStore().getString(LAST_CREATE_FILE_PREFERENCE);
//			String last = StrutsModelPlugin.getDefault().getPluginPreferences().getString(LAST_CREATE_FILE_PREFERENCE);
			if(last == null || last.length() == 0) {
				last = "true";
			} else if(!"true".equals(last)) {
				last = "false"; 
			} 
			setAttributeValue(getStepId(), "create file", last);
        }
    }

    public void action(String name) throws XModelException {
        if(FINISH.equals(name)) {
            finish();
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            setFinished(true);
        } else if(name.startsWith("...")) {
            context.update();
            callSelector();
        }
    }

    private void callSelector() throws XModelException {
        String b = getValidatedName(false);
        if(b != null && b.endsWith("/")) b = b.substring(0, b.length() - 1);
        XModelObject so = (b == null || b.length() == 0) ? null : getTarget().getModel().getByPath(b);
        if(so != null) {
            getProperties().put("selectedObject", so);
        } else {
            getProperties().remove("selectedObject");
        }
        XModelObject fs = context.getSelectedFileSystem();
        if(fs != null) {
            getProperties().put("selectedFileSystem", fs);
        } else {
            getProperties().remove("selectedFileSystem");
        }
        getProperties().remove("SelectPage.cancelled");
        XActionInvoker.invoke("StrutsCreatePageWizard_Selector", "SelectPage", getTarget(), getProperties());
        if(getProperties().getProperty("SelectPage.cancelled") != null) return;
        onSelection();
    }

    private void onSelection() throws XModelException {
        XModelObject so = (XModelObject)getProperties().get("selectedObject");
        if(so != null) {
            String p = XModelObjectLoaderUtil.getResourcePath(so);
            if(so.getFileType() > XFileObject.FILE) p += "/";
            setAttributeValue(getStepId(), "name", p);
        }
        XModelObject fs = (XModelObject)getProperties().get("selectedFileSystem");
        if(fs != null) context.setSelectedFileSystem(fs);
        if(so != null && getAttributeValue(getStepId(), "name").length() > 0
                      && so.getFileType() == XFileObject.FILE) {
            Properties pp = new Properties();
            pp.setProperty("name", getAttributeValue(getStepId(), "name"));
            getValidator(getStepId()).validate(pp);
            if(validator.getErrorMessage() == null) action(FINISH);
        }
    }

    public boolean isActionEnabled(String name) {
        if(FINISH.equals(name)) {
            int id = getStepId();
            String v = getAttributeValue(id, "name");
            if(v == null || v.length() == 0 || !context.isPage(v)) return false;
        }
        return true;
    }

    public String[][] getInfo(int stepId) {
        if(stepId != 1) return null;
        XModelObject fs = context.getSelectedFileSystem();
        String s = (fs == null) ? "" : context.getRootInfo(fs);
        return new String[][]{{"Current Module Root", s}};
    }

    private String getValidatedName(boolean save) {
        String b = getAttributeValue(getStepId(), "name");
        if(b != null && b.length() > 0) {
            if(!b.startsWith("/")) {
                b = "/" + b;
                if(save) setAttributeValue(getStepId(), "name", b);
            }
            if(!b.endsWith("/") && b.indexOf('.') < 0) {
                b += getExtension();
                if(save) setAttributeValue(getStepId(), "name", b);
            }
            String b1 = context.revalidatePath(b); /*4598*/
            if(!b1.equals(b)) {
                b = b1;
                setAttributeValue(getStepId(), "name", b);
            }
        }
        return b;
    }

    private void finish() throws XModelException {
        getValidatedName(true);
        extractStepData(getStepId());
        context.update();
        XUndoManager undo = getTarget().getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("create page in struts process " + DefaultCreateHandler.title(getTarget().getParent(), false), XTransactionUndo.ADD);
        undo.addUndoable(u);
        try {
            transaction();
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw new XModelException(e);
        } finally {
            u.commit();
        }
    }
    
    private String getFullPath(String path) {
		String root = context.getRoot(), thisRoot = context.getThisRoot();
		String fullpath = path;
		if(root != null) {
			if(root.equals(thisRoot)) {
				if(path.startsWith(root + "/")) fullpath = path = path.substring(root.length());
			} else if(!path.startsWith(root + "/")) {
				fullpath = root + path;
			}
		}
		return fullpath;
    }
    
    private String getExistsMessage(String fullpath, String path) {
		if(context.pageExists(fullpath.toLowerCase())) {
			return DefaultCreateHandler.title(getTarget().getParent(), true) +
						 " contains page " + path;
		}
		return null;
    }

    private void transaction() throws Exception {
        int id = getStepId();
        String path = getAttributeValue(id, "name");
		String fullpath = getFullPath(path);
		String msg = getExistsMessage(fullpath, path);
		if(msg != null) throw new Exception(msg);
        createFile(path);
        StrutsProcessHelper ph = StrutsProcessHelper.getHelper(getTarget());
        XModelObject item = ph.getPage(fullpath);
        if(item != null) return;
        createPage(getTarget(), fullpath, getProperties());
    }

    public static XModelObject createPage(XModelObject process, String path, Properties p) throws XModelException {
        XModelObject item = StrutsProcessHelper.createPage(process, path);
        String x = (p == null) ? null : p.getProperty("process.mouse.x");
        String y = (p == null) ? null : p.getProperty("process.mouse.y");
        if(x != null && y != null) {
            item.setAttributeValue("shape", "" + x + "," + y + ",0,0");
        }
        DefaultCreateHandler.addCreatedObject(process, item, -1);
        StrutsProcessHelper ph = StrutsProcessHelper.getHelper(process);
        ph.updateTiles();
        PageUpdateManager.getInstance(item.getModel()).updatePage(ph, item);
        return item;
    }

    private void createFile(String path) throws Exception {
    	if(!canCreateFile()) return;

		String lastCreateFileValue = getAttributeValue(getStepId(), "create file");
		StrutsModelPlugin.getDefault().getPreferenceStore().setDefault(LAST_CREATE_FILE_PREFERENCE, lastCreateFileValue);
//		StrutsModelPlugin.getDefault().getPluginPreferences().setDefault(LAST_CREATE_FILE_PREFERENCE, lastCreateFileValue);
		if(!"true".equals(lastCreateFileValue)) return;

		File templateFile = null;
		String template = getAttributeValue(getStepId(), "template").trim();
		if(template.length() > 0) {
			templateFile = (File)templates.getPageTemplates().get(template);
			if(templateFile == null || !templateFile.isFile()) throw new Exception("Template " + template + " is not found.");
		}
		
		String body = (templateFile == null) ? "" : FileUtil.readFile(templateFile);

        XModelObject fs = context.getSelectedFileSystem();
        if(fs == null) return;
        createFile(fs, path, body);
    }

    public static void createFile(XModelObject fs, String path, String body) throws XModelException {
        StringTokenizer st = new StringTokenizer(path, "/");
        int c = st.countTokens(), i = 0;
        while(i < c - 1) {
            String s = st.nextToken();
            XModelObject o = fs.getChildByPath(s);
            if(o == null) {
                o = fs.getModel().createModelObject("FileFolder", null);
                o.setAttributeValue("name", s);
                DefaultCreateHandler.addCreatedObject(fs, o, -1);
                ((FolderImpl)o).save();
            }
            fs = o;
            i++;
        }
        String s = st.nextToken().trim();
        if(s.length() == 0) return;
        int dot = s.lastIndexOf('.');
        if(dot < 0) {
        	s += getExtension();
        	dot = s.indexOf('.');
        }
        String n = s.substring(0, dot);
        String e = s.substring(dot + 1);
        String entity = ("jsp".equals(e)) ? "FileJSP" :
                        ("htm".equals(e)) ? "FileHTML" :
                        ("html".equals(e)) ? "FileHTML" :
                        (getExtension().equals(e)) ? "FileTXT" :
                        "FileAny";
        XModelObject f = fs.getModel().createModelObject(entity, null);
        f.setAttributeValue("name", n);
        f.setAttributeValue("extension", e);
        if(body != null) f.setAttributeValue("body", body);
        if(fs.getChildByPath(f.getPathPart()) != null) return;
        DefaultCreateHandler.addCreatedObject(fs, f, -1);
        ((FolderImpl)fs).saveChild(f);
    }
    
	static XModelObject getPreferenceObject() {
		return PreferenceModelUtilities.getPreferenceModel().getByPath(StrutsPreference.ADD_PAGE_PATH);
	}
	
	public String getDefaultTemplate(String[] list) {
		if(list.length == 0) return "";
		XModelObject addPage = getPreferenceObject();
		String v = (addPage == null) ? "" : addPage.getAttributeValue("Page Template");
		if(v != null) for (int i = 0; i < list.length; i++) if(v.equals(list[i])) return list[i];
		return list[0];
	}
	
	public static String getExtension() {
		XModelObject addPage = getPreferenceObject();
		String v = (addPage == null) ? "" : addPage.getAttributeValue("Extension");
		if(v == null || v.length() == 0) return ".jsp";
		if(!v.startsWith(".")) v = "." + v;
		return v;
	}

	protected DefaultWizardDataValidator validator = new Validator();
    
	public WizardDataValidator getValidator(int step) {
		validator.setSupport(this, step);
		return validator;    	
	}

    class Validator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			String path = data.getProperty("name"); //$NON-NLS-1$
			if(path != null && path.length() > 0) {
				if(!path.startsWith("/")) { //$NON-NLS-1$
					path = "/" + path; //$NON-NLS-1$
				}
				if(!path.endsWith("/") && path.indexOf('.') < 0) { //$NON-NLS-1$
					path += getExtension();
				}
				String b1 = context.revalidatePath(path); /*4598*/
				if(!b1.equals(path)) path = b1;
			}
			message = getExistsMessage(getFullPath(path), path);
			if(message != null) return;
			String template = data.getProperty("template"); //$NON-NLS-1$
			if(template != null && isFieldEditorEnabled(0, "template", data)) { //$NON-NLS-1$
				if(template.trim().length() == 0) {
					message = StrutsUIMessages.TEMPLATE_ISNOT_SPECIFIED;
					return;
				}
				File templateFile = (File)templates.getPageTemplates().get(template.trim());
				if(templateFile == null || !templateFile.isFile()) {
					message = StrutsUIMessages.TEMPLATE_DOESNT_EXIST;
				}
			}
			if(message != null) return;
			if(!context.isPage(path)) {
				if(path.endsWith("/")) { //$NON-NLS-1$
					message = StrutsUIMessages.PATH_CANNOT_END_WITH;
				} else {
					String d = getExtension().substring(1);
					if(d.equals(".jsp")) { //$NON-NLS-1$
						message = StrutsUIMessages.PATH_EXTENSION_MUST_BE_JSP_HTM_HTML;
					} else {
						message = NLS.bind(StrutsUIMessages.PATH_EXTENSION_MUST_BE, d); //$NON-NLS-2$
					}
				}
			}
		}
    }

	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		setAttributeValue(getStepId(), "name", values.getProperty("name"));
		boolean c = canCreateFile();
		if(name.equals("create file")) {
			return c;
		}
		boolean g = c && "true".equals(values.getProperty("create file"));
		if(name.equals("template")) {
			return g;
		}
		return true;
	}

	public boolean canCreateFile() {
		String path = getValidatedName(false);
		if(path == null || path.length() == 0 || path.startsWith("http:")) return false;
		return isCorrectPath(path) && !fileExists(path);
	}
	
	boolean isCorrectPath(String path) {
		if(path == null || path.equals("/") || path.indexOf("//") >= 0) return false;
		  return true;
	}
	
	boolean fileExists(String path) {
		if(context.getSelectedFileSystem() != null && path != null && path.startsWith("/")) {
			return context.getSelectedFileSystem().getChildByPath(path.substring(1)) != null;
		}
		return getTarget().getModel().getByPath(path) != null;
	} 
	
}
