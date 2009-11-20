/*
 * StrutsProcessHelper.java
 *
 * Created on February 20, 2003, 10:27 AM
 */

package org.jboss.tools.struts.model.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.model.ReferenceObjectImpl;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.struts.model.handlers.page.create.CreatePageSupport;
import org.jboss.tools.struts.model.helpers.autolayout.StrutsItems;
import org.jboss.tools.struts.model.helpers.page.JSPLinksParser;
import org.jboss.tools.struts.model.helpers.page.PageUpdateManager;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoLayout;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoPlacement;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

/**
 *
 * @author  valera
 */
public class StrutsProcessHelper implements StrutsConstants {
	class Task {
		public void execute() {}
	}
	class BuildTask extends Task {
		public void execute() {
			buildInternal();
		}
	}
	class UpdateProcessTask extends Task {
		public void execute() {
			updateProcessInternal();
		}
	}
	class UpdateActionTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject action;
		UpdateActionTask(XModelObject parent, XModelObject ref, XModelObject action) {
			this.parent = parent;
			this.ref = ref;
			this.action = action;
		}
		public void execute() {
			updateActionInternal(parent, ref, action);
		}
	}
	class ReloadActionTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject action;
		ReloadActionTask(XModelObject parent, XModelObject ref, XModelObject action) {
			this.parent = parent;
			this.ref = ref;
			this.action = action;
		}
		public void execute() {
			reloadActionInternal(parent, ref, action);
		}
	}
	class UpdateForwardTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject forward;
		UpdateForwardTask(XModelObject parent, XModelObject ref, XModelObject forward) {
			this.parent = parent;
			this.ref = ref;
			this.forward = forward;
		}
		public void execute() {
			updateForwardInternal(parent, ref, forward);
		}
	}
	class ReloadForwardTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject forward;
		ReloadForwardTask(XModelObject parent, XModelObject ref, XModelObject forward) {
			this.parent = parent;
			this.ref = ref;
			this.forward = forward;
		}
		public void execute() {
			reloadForwardInternal(parent, ref, forward);
		}
	}
	class UpdatePageTask extends Task {
		XModelObject page;
		JSPLinksParser p;
		UpdatePageTask(XModelObject page, JSPLinksParser p) {
			this.page = page;
			this.p = p;
		}
		public void execute() {
			updatePageInternal(page, p);
		}
	}
	class UpdateExceptionTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject exception;
		UpdateExceptionTask(XModelObject parent, XModelObject ref, XModelObject exception) {
			this.parent = parent;
			this.ref = ref;
			this.exception = exception;
		}
		public void execute() {
			updateExceptionInternal(parent, ref, exception);
		}
	}
	class ReloadExceptionTask extends Task {
		XModelObject parent;
		XModelObject ref;
		XModelObject exception;
		ReloadExceptionTask(XModelObject parent, XModelObject ref, XModelObject exception) {
			this.parent = parent;
			this.ref = ref;
			this.exception = exception;
		}
		public void execute() {
			reloadExceptionInternal(parent, ref, exception);
		}
	}
	class ResolveTask extends Task {
		public void execute() {
			resolveInternal();
		}
	}
	
	//Probably, Set is enough, but List worked very long successfully
	//so let'n not take a risk and use Set for optimizing only.
	private class Binds {
		List<XModelObject> list = new ArrayList<XModelObject>();
		Set<XModelObject> set = new HashSet<XModelObject>();
		
		public void add(XModelObject o) {
			if(set.contains(o)) return;
			set.add(o);
			list.add(o);
			if(set.size() != list.size()) {
				System.out.println("Panikk: " + set.size() + " " + list.size());
			}
		}
		
		public int size() {
			return list.size();
		}
		
		public void clear() {
			list.clear();
			set.clear();
		}
	}
	
	private ArrayList<Task> tasks = new ArrayList<Task>();
	
	private void addTask(Task task) {
		synchronized (this) {
			tasks.add(task);
		}
		runTasks();
	}
	
	private int isRunning = 0;
	
	private void runTasks() {
		if(isRunning > 0) return;
		isRunning++;
		while(true) {
			Task ts = null;
			synchronized(this) {
				if(tasks.size() == 0) break;
				ts = (Task)tasks.remove(0);
			}
			try {
				ts.execute();
			} catch (Exception e) {
				StrutsModelPlugin.getPluginLog().logError(e);
			}
		}
		isRunning--;
	}

    private XModelObject process;
    private XModelObject config;
    private Map<String,XModelObject> objects = new HashMap<String,XModelObject>();
    private Map<String,XModelObject> actions = new HashMap<String,XModelObject>();
    private Binds binds = new Binds();
    private Map<String,XModelObject> pages = new HashMap<String,XModelObject>();
    StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();
    boolean is_10 = true;
    Set<String> tiles = new TreeSet<String>();
    String module = "";
    UrlPattern urlPattern = null;
    StrutsBreakpointManager breakpointManager = null;

    /** Creates a new instance of StrutsProcessHelper */
    public StrutsProcessHelper(XModelObject process) {
        this.process = process;
		breakpointManager = new StrutsBreakpointManager(process); 
    }

    private void reset() {
        this.objects.clear();
        this.actions.clear();
        this.binds.clear();
        this.pages.clear();
        this.config = process.getParent();
    }

    public void build() {
    	addTask(new BuildTask());
    }

    private void buildInternal() {
        reset();
        removeChildren(process);
        XModelObject globalForw = config.getChildByPath(ELM_GLOBALFORW);
        XModelObject globalExc = config.getChildByPath(ELM_GLOBALEXC);
        XModelObject actionMap = config.getChildByPath(ELM_ACTIONMAP);

        XModelObject[] children = globalForw.getChildren();
        for (int i = 0; i < children.length; i++) {
            reloadForwardInternal(process, children[i], null);
        }
        if (globalExc != null) {
            children = globalExc.getChildren();
            for (int i = 0; i < children.length; i++) {
                reloadExceptionInternal(process, children[i], null);
            }
        }
        children = actionMap.getChildren();
        for (int i = 0; i < children.length; i++) {
            reloadActionInternal(process, children[i], null);
        }
        resolveInternal();
        updatePages();
        removeUnconfirmed();
    }
    
    public void updateProcess() {
    	addTask(new UpdateProcessTask());
    }
    private void updateProcessInternal() {
		StrutsBreakpointManager bpManager = getBreakpointManager();
		if (bpManager != null) bpManager.update();
		 
        is_10 = process.getParent().getModelEntity().getName().endsWith(VER_SUFFIX_10);
        reset();
        XModelObject globalForw = config.getChildByPath(ELM_GLOBALFORW);
        XModelObject globalExc = config.getChildByPath(ELM_GLOBALEXC);
        XModelObject actionMap = config.getChildByPath(ELM_ACTIONMAP);

        Map<String,XModelObject> forwMap = getChildren(globalForw);
        Map<String,XModelObject> excMap = globalExc == null ? new HashMap<String,XModelObject>() : getChildren(globalExc);
        Map<String,XModelObject> actMap = getChildren(actionMap);
        
        XModelObject[] children = process.getChildren();
        for (int i = 0; i < children.length; i++) {
            if(!(children[i] instanceof ReferenceObjectImpl)) continue;
            ReferenceObjectImpl child = (ReferenceObjectImpl)children[i];
            if (!child.isActive()) continue;
            XModelObject ref = child.getReference();
            if (ref != null && !ref.isActive()) ref = null;
            String type = child.getAttributeValue(ATT_TYPE);
            String id = child.getAttributeValue(ATT_ID);
            if (TYPE_ACTION.equals(type)) {
                if (ref == null) {
                    ref = (XModelObject)actMap.remove(id);
                } else {
                    actMap.remove(ref.getPathPart());
                }
                reloadActionInternal(process, ref, child);
            } else if (TYPE_FORWARD.equals(type)) {
                if (ref == null) {
                    ref = (XModelObject)forwMap.remove(id);
                } else {
                    forwMap.remove(ref.getPathPart());
                }
                reloadForwardInternal(process, ref, child);
            } else if (TYPE_EXCEPTION.equals(type)) {
                if (ref == null) {
                    ref = (XModelObject)excMap.remove(id);
                } else {
                    excMap.remove(ref.getPathPart());
                }
                reloadExceptionInternal(process, ref, child);
            } else if (TYPE_PAGE.equals(type)) {
                pages.put(child.getAttributeValue(ATT_PATH), child);
            }
        }
        
        for(XModelObject o: forwMap.values()) {
            reloadForwardInternal(process, o, null);
        }
        for(XModelObject o: excMap.values()) {
            reloadExceptionInternal(process, o, null);
        }
        for(XModelObject o: actMap.values()) {
            reloadActionInternal(process, o, null);
        }
        cleanObjects();
        resolveInternal();
        updatePages();
        removeUnconfirmed();
////        autolayout();
    }

    public void reloadAction(XModelObject parent, XModelObject ref, XModelObject action) {
    	addTask(new ReloadActionTask(parent, ref, action));
    }

    private void reloadActionInternal(XModelObject parent, XModelObject ref, XModelObject action) {
        if (action == null) {
            action = createAction(parent, ref);
            objects.put(getLocalPath(ref), action);
        } else {
            String path = getLocalPath2(action);
            XModelObject prev = (XModelObject)objects.remove(path);
            if (prev != null && prev != action) {
                if (ref == null && prev.getParent() != null) {
                    action.removeFromParent();
                    objects.put(path, prev);
                    return;
                } else {
                    prev.removeFromParent();
                }
            }
            updateActionInternal(parent, ref, action);
            prev = (XModelObject)objects.put(getLocalPath2(action), action);
            if (prev != null) {
                prev.removeFromParent();
            }
            List<XModelObject> list = getReferers(parent, action.getPathPart());
            for (XModelObject o: list) addBind(o);
        }
    }

    public void removeAction(XModelObject action) {
        objects.remove(getLocalPath2(action));
        action.removeFromParent();
        updateProcess();
    }
    
    private void addBind(XModelObject o) {
   		binds.add(o);
    }

    private XModelObject createAction(XModelObject parent, XModelObject ref) {
        Properties props = new Properties();
//        String name = ref.getAttributeValue(ATT_TYPE);
        props.setProperty(ATT_NAME, createName(parent, "action"));
        props.setProperty(ATT_TYPE, TYPE_ACTION);
        String shape = ref.get("_shape");
        if(shape != null) props.setProperty(ATT_SHAPE, shape);
        XModelObject action = ref.getModel().createModelObject(ENT_PROCESSITEM, props);
        updateActionInternal(parent, ref, action);
        parent.addChild(action);
        return action;
    }

    public void updateAction(XModelObject parent, XModelObject ref, XModelObject action) {
    	addTask(new UpdateActionTask(parent, ref, action));
    }

    private void updateActionInternal(XModelObject parent, XModelObject ref, XModelObject action) {
        actions.remove(action.getAttributeValue(ATT_PATH));
        if (ref == null) {
            ((ReferenceObjectImpl)action).setReference(null);
            String subtype = action.getAttributeValue(ATT_SUBTYPE);
            if(!SUBTYPE_UNKNOWN.equals(subtype)) {
            	//6389//
				List<XModelObject> l = getReferers(action.getParent(), action.getPathPart());
				if(l.size() == 0) {
					action.removeFromParent();
					return;
				}
            }
            action.setAttributeValue(ATT_SUBTYPE, SUBTYPE_UNKNOWN);
            XModelObject[] children = action.getChildren();
            for (int i = 0; i < children.length; i++) {
                ((ReferenceObjectImpl)children[i]).setReference(null);
            }
        } else {
            String path = ref.getAttributeValue(ATT_PATH);
            ((ReferenceObjectImpl)action).setReference(ref);
            action.setAttributeValue(ATT_ID, path.replace('/', '#'));
            action.setAttributeValue(ATT_PATH, path);
            action.setAttributeValue(ATT_TITLE, ref.getAttributeValue(ATT_NAME));
            String frw = ref.getAttributeValue(ATT_FORWARD).trim();
            String inc = ref.getAttributeValue(ATT_INCLUDE).trim();
            String redirect = "";
            String jtype = ref.getAttributeValue(ATT_TYPE);
            String subtype = "";
            if("org.apache.struts.actions.SwitchAction".equals(jtype)) {
                subtype = SUBTYPE_SWITCH;
            } else if(frw.length() > 0) {
                redirect = frw;
                subtype = SUBTYPE_FORWARD;
            } else if(inc.length() > 0) {
                redirect = inc;
                subtype = SUBTYPE_INCLUDE;
            } else if("org.apache.struts.actions.ForwardAction".equals(jtype)) {
                redirect = ref.getAttributeValue("parameter");
                subtype = SUBTYPE_FORWARDACTION;
            }
            if (!subtype.equals(action.getAttributeValue(ATT_SUBTYPE))) {
                removeChildren(action);
            }
            if(SUBTYPE_SWITCH.equals(subtype)) {
            } else if (subtype.length() > 0) {
                XModelObject forward = action.getChildAt(0);
                if (forward == null) {
                    Properties props2 = new Properties();
                    props2.setProperty(ATT_NAME, action.getAttributeValue(ATT_NAME));
                    props2.setProperty(ATT_TYPE, TYPE_FORWARD);
                    forward = ref.getModel().createModelObject(ENT_PROCESSITEMOUT, props2);
                    action.addChild(forward);
                } else {
                    forward.setAttributeValue(ATT_NAME, action.getAttributeValue(ATT_NAME));
                }
                forward.setAttributeValue(ATT_PATH, redirect);
                forward.setAttributeValue(ATT_TITLE, subtype);
                ((ReferenceObjectImpl)forward).setReference(ref);
                action.setAttributeValue(ATT_SUBTYPE, subtype);
            } else {
                Map<String,XModelObject> refMap = getChildren(ref);
                XModelObject[] localBinds = action.getChildren();
                for (int j = 0; j < localBinds.length; j++) {
                    String type = localBinds[j].getAttributeValue(ATT_TYPE);
                    XModelObject refBind = (XModelObject)refMap.remove(localBinds[j].getAttributeValue(ATT_ID));
                    if (type.equals(TYPE_FORWARD)) {
                        reloadForwardInternal(action, refBind, localBinds[j]);
                    } else if (type.equals(TYPE_EXCEPTION)) {
                        reloadExceptionInternal(action, refBind, localBinds[j]);
                    }
                }
                for(XModelObject refBind: refMap.values()) {
                    String entity = refBind.getModelEntity().getName();
                    if (entity.startsWith(ENT_FORWARD)) {
                        reloadForwardInternal(action, refBind, null);
                    } else if (entity.startsWith(ENT_EXCEPTION)) {
                        reloadExceptionInternal(action, refBind, null);
                    }
                }
            }
            action.setAttributeValue(ATT_SUBTYPE, subtype);
        }
        XModelObject[] children = action.getChildren();
        for (int i = 0; i < children.length; i++) {
        	addBind(children[i]);
        }
        actions.put(action.getAttributeValue(ATT_PATH), action);
    }

    public XModelObject getAction(String path) {
        return (XModelObject)actions.get(path);
    }

    public void registerAction(XModelObject action) {
        actions.put(action.getAttributeValue(ATT_PATH), action);
    }

    public void reloadForward(XModelObject parent, XModelObject ref, XModelObject forward) {
    	addTask(new ReloadForwardTask(parent, ref, forward));
    }
    private void reloadForwardInternal(XModelObject parent, XModelObject ref, XModelObject forward) {
        if (ref == null) {
            objects.remove(getLocalPath2(forward));
            forward.removeFromParent();
        } else if (forward == null) {
            forward = createForward(parent, ref, parent.getModelEntity().getChildren()[0].getName());
            objects.put(getLocalPath(ref), forward);
        } else {
            objects.remove(getLocalPath2(forward));
            updateForwardInternal(parent, ref, forward);
            objects.put(getLocalPath(ref), forward);
        }
    }
    
    private XModelObject createForward(XModelObject parent, XModelObject ref, String entity) {
        Properties props = new Properties();
        props.setProperty(ATT_NAME, createName(parent, "forward"));
        props.setProperty(ATT_TYPE, TYPE_FORWARD);
        String shape = ref.get("_shape");
        if(shape != null) props.setProperty(ATT_SHAPE, shape);
        XModelObject forward = ref.getModel().createModelObject(entity, props);
        updateForwardInternal(parent, ref, forward);
        parent.addChild(forward);
        return forward;
    }

    public void updateForward(XModelObject parent, XModelObject ref, XModelObject forward) {
    	addTask(new UpdateForwardTask(parent, ref, forward));
    }

    private void updateForwardInternal(XModelObject parent, XModelObject ref, XModelObject forward) {
        String name = ref.getAttributeValue(ATT_NAME);
        forward.setAttributeValue(ATT_ID, name);
        forward.setAttributeValue(ATT_PATH, ref.getAttributeValue(ATT_PATH));
        forward.setAttributeValue(ATT_TITLE, name);
        ((ReferenceObjectImpl)forward).setReference(ref);
        addBind(forward);
    }

    public void reloadException(XModelObject parent, XModelObject ref, XModelObject exception) {
    	addTask(new ReloadExceptionTask(parent, ref, exception));
    }

    private void reloadExceptionInternal(XModelObject parent, XModelObject ref, XModelObject exception) {
        if (ref == null) {
            objects.remove(getLocalPath2(exception));
            exception.removeFromParent();
        } else if (exception == null) {
            exception = createException(parent, ref, parent.getModelEntity().getChildren()[0].getName());
            objects.put(getLocalPath(ref), exception);
        } else {
            objects.remove(getLocalPath2(exception));
            updateException(parent, ref, exception);
            objects.put(getLocalPath(ref), exception);
        }
    }

    private XModelObject createException(XModelObject parent, XModelObject ref, String entity) {
        Properties props = new Properties();
        props.setProperty(ATT_NAME, createName(parent, "exception"));
        props.setProperty(ATT_TYPE, TYPE_EXCEPTION);
        String shape = ref.get("_shape");
        if(shape != null) props.setProperty(ATT_SHAPE, shape);
        XModelObject exception = ref.getModel().createModelObject(entity, props);
        updateExceptionInternal(parent, ref, exception);
        parent.addChild(exception);
        return exception;
    }

    public void updateException(XModelObject parent, XModelObject ref, XModelObject exception) {
    	addTask(new UpdateExceptionTask(parent, ref, exception));
    }

    private void updateExceptionInternal(XModelObject parent, XModelObject ref, XModelObject exception) {
        String name = ref.getAttributeValue(ATT_TYPE);
        exception.setAttributeValue(ATT_ID, name);
        exception.setAttributeValue(ATT_PATH, ref.getAttributeValue(ATT_PATH));
        exception.setAttributeValue(ATT_TITLE, name.substring(name.lastIndexOf('.')+1));
        ((ReferenceObjectImpl)exception).setReference(ref);
        addBind(exception);
    }
    

    public void resolve() {
    	addTask(new ResolveTask());
    }

    private void resolveInternal() {
        if(binds.size() == 0 && urlPattern != null) return;
        
		WebModulesHelper wmh = WebModulesHelper.getInstance(process.getModel());
        Set<String> modules = (is_10) ? new HashSet<String>() : wmh.getModules();
        module = (is_10) ? "" : WebModulesHelper.getInstance(process.getModel()).getModuleForConfig(process.getParent());
        if(module == null) module = "";
        urlPattern = wmh.getUrlPattern(module);

        Iterator<XModelObject> it = binds.list.iterator();
        while (it.hasNext()) {
            XModelObject bind = it.next();
            if (!bind.isActive()) {
                it.remove();
                binds.set.remove(bind);
                continue;
            }
            String path = bind.getAttributeValue(ATT_PATH);
            if(path == null) {
            	//this is comment node
            	continue;
            }
            int ind = path.indexOf('?');
            if (ind > 0) {
                path = path.substring(0, ind);
            }
            if (path.length() == 0) {
                bind.setAttributeValue(ATT_TARGET, "");
                continue;
            }
//            String name = path.substring(path.lastIndexOf('/')+1);
//            int dot = name.lastIndexOf('.');
//            String ext = dot == -1 ? "" : name.substring(dot).toLowerCase();
            if (path.startsWith("/")) {
                if (urlPattern.isActionUrl(path)) { //(dot == -1 || ".do".equals(ext))
                    path = urlPattern.getActionPath(path); //path.substring(0, path.length()-ext.length());
                    String actionpath = path;
                    boolean other = false;
                    if(!is_10 && module.length() > 0) {
                    	String mrp = urlPattern.getModuleRelativePath(actionpath, module);
                        if(!actionpath.equals(mrp)) actionpath = mrp;
                        else if(isContextRelative(bind)) {
//                            int sl = actionpath.indexOf('/', 1);
                            String othermodule = urlPattern.getModule(actionpath, modules, "");
                            if(othermodule.length() == 0) {
                                actionpath = "/" + actionpath;
                                other = true;
                            }
                        }
                    }
                    XModelObject action = (XModelObject)actions.get(actionpath);
                    if (action == null) {
                        Properties props = new Properties();
                        props.setProperty(ATT_NAME, createName(process, "action"));
                        props.setProperty(ATT_TYPE, TYPE_ACTION);
                        props.setProperty(ATT_SUBTYPE, SUBTYPE_UNKNOWN);
                        if(!other) props.setProperty(ATT_ID, path.replace('/', '#'));
                        props.setProperty(ATT_PATH, actionpath);
                        action = bind.getModel().createModelObject(ENT_PROCESSITEM, props);
                        String shape = bind.getAttributeValue(ATT_SHAPE);
                        if(shape != null && shape.length() > 0 && ENT_PROCESSITEMOUT.equals(bind.getModelEntity().getName())) {
							action.setAttributeValue(ATT_SHAPE, shape);
							bind.setAttributeValue(ATT_SHAPE, "");                        	
                        } else {
							new AutoPlacement().place(process, bind, action);
						}
                        process.addChild(action);
                        updateAction(process, null, action);
                        objects.put(ELM_ACTIONMAP+"/"+action.getAttributeValue(ATT_ID), action);
                    }
                    bind.setAttributeValue(ATT_TARGET, action.getAttributeValue(ATT_NAME));
                    continue;
                }
            }

            String pagepath = path;
            if(!is_10 && module.length() > 0 && !isTile(path) && !isHttp(path)) {
                if(!pagepath.startsWith("/")) pagepath = "/" + pagepath;
                if(pagepath.startsWith(module + "/")) pagepath = pagepath.substring(module.length());
            }

            XModelObject page = getPage(pagepath);
            if (page == null) {
                page = createPage(process, pagepath);
                process.addChild(page);
                new AutoPlacement().place(process, bind, page);
            }
            bind.setAttributeValue(ATT_TARGET, page.getAttributeValue(ATT_NAME));
        }
        binds.clear();
    }
    
    public void removeUnconfirmed() {
    	Set<String> set = getReferredTargets(process);
    	XModelObject[] os = process.getChildren();
    	for (int i = 0; i < os.length; i++) {
			if(set.contains(os[i].getPathPart())) continue;
			String type = os[i].getAttributeValue(ATT_TYPE);
			String subtype = os[i].getAttributeValue(ATT_SUBTYPE);
			String objpath = os[i].getAttributeValue(ATT_PATH);
			if(TYPE_ACTION.equals(type) && SUBTYPE_UNKNOWN.equals(subtype)) {
				actions.remove(objpath);
				os[i].removeFromParent();
			} else if(TYPE_PAGE.equals(type) && !h.isPageConfirmed(os[i])) {
				PageUpdateManager pu = PageUpdateManager.getInstance(process.getModel());
				pu.lock();
				pu.updatePage(this, os[i]);
				pu.unlock();
				if(h.isPageConfirmed(os[i])) continue;
				pages.remove(objpath);
				os[i].removeFromParent();
			}    		
    	}
    }

    private boolean isTile(String path) {
        if(path.startsWith("/")) return false;
        if(tiles.contains(path)) return true;
        int dot = path.indexOf('.');
        if(dot < 0) return true;
        String ext = path.substring(dot + 1);
        return !ext.equals("jsp") && !ext.equals("html") && !ext.equals("htm")
        		&& !ext.equals(CreatePageSupport.getExtension().substring(1));
    }
    
    public static boolean isHttp(String path) {
    	return path.startsWith("http:");
    }

    private static boolean isContextRelative(XModelObject item) {
        if(!(item instanceof ReferenceObjectImpl)) return false;
        XModelObject r = ((ReferenceObjectImpl)item).getReference();
        if(r == null) return false;
        if("true".equals(r.getAttributeValue("contextRelative"))) return true;
        String module = r.getAttributeValue("module");
        if(module != null && module.startsWith("/")) return true;
        return false;
    }

    public static StrutsProcessHelper getHelper(XModelObject process) {
        return ((StrutsProcessImpl)process).getHelper();
    }

    public static XModelObject createPage(XModelObject process, String path) {
        String name = path.substring(path.lastIndexOf('/')+1);
        int dot = name.lastIndexOf('.');
        String ext = dot == -1 ? "" : name.substring(dot).toLowerCase();
        Properties props = new Properties();
        String subtype;
        if(isHttp(path)) {
			subtype = SUBTYPE_HTML;
        } else if (".jsp".equals(ext) || CreatePageSupport.getExtension().equals(ext)) {
            subtype = SUBTYPE_JSP;
        } else if (ext.length() < 7 && ext.indexOf("htm") != -1) {
            subtype = SUBTYPE_HTML;
        } else if (!path.startsWith("/")) {
            subtype = SUBTYPE_TILE;
        } else {
            subtype = SUBTYPE_OTHER;
        }
        props.setProperty(ATT_NAME, createName(process, "page"));
        props.setProperty(ATT_TYPE, TYPE_PAGE);
        props.setProperty(ATT_SUBTYPE, subtype);
        props.setProperty(ATT_PATH, path);
        props.setProperty(ATT_TITLE, name);
        XModelObject page = process.getModel().createModelObject(ENT_PROCESSITEM, props);
        getHelper(process).pages.put(path, page);
        return page;
    }

    public void restoreRefs() {
    	breakpointManager.activate();
        this.config = process.getParent();
        XModelObject globalForw = config.getChildByPath(ELM_GLOBALFORW);
        XModelObject globalExc = config.getChildByPath(ELM_GLOBALEXC);
        XModelObject actionMap = config.getChildByPath(ELM_ACTIONMAP);
        ((ReferenceObjectImpl)process).setReference(config);
        XModelObject[] children = process.getChildren();
        for (int i = 0; i < children.length; i++) {
            String type = children[i].getAttributeValue(ATT_TYPE);
            if (TYPE_EXCEPTION.equals(type)) {
                restoreExcRef(globalExc, children[i]);
            } else if (TYPE_FORWARD.equals(type)) {
                restoreForwRef(globalForw, children[i]);
            } else if (TYPE_ACTION.equals(type)) {
                restoreActRef(actionMap, children[i]);
            }
        }
    }

    public void restoreActRef(XModelObject parentRef, XModelObject action) {
        if (parentRef == null) return;
        XModelObject ref = parentRef.getChildByPath(action.getAttributeValue(ATT_ID));
        if (ref != null) {
            ((ReferenceObjectImpl)action).setReference(ref);
            XModelObject[] children = action.getChildren();
            if (action.getAttributeValue(ATT_SUBTYPE).length() == 0) {
                for (int i = 0; i < children.length; i++) {
                    String type = children[i].getAttributeValue(ATT_TYPE);
                    if (TYPE_EXCEPTION.equals(type)) {
                        restoreExcRef(ref, children[i]);
                    } else if (TYPE_FORWARD.equals(type)) {
                        restoreForwRef(ref, children[i]);
                    }
                }
            } else if (!SUBTYPE_UNKNOWN.equals(action.getAttributeValue(ATT_SUBTYPE))) {
                for (int i = 0; i < children.length; i++) {
                    ((ReferenceObjectImpl)children[i]).setReference(ref);
                }
            }
        }
    }

    public void restoreForwRef(XModelObject parentRef, XModelObject forward) {
        if (parentRef == null) return;
        XModelObject ref = parentRef.getChildByPath(forward.getAttributeValue(ATT_ID));
        if (ref != null) {
            ((ReferenceObjectImpl)forward).setReference(ref);
        }
    }

    public void restoreExcRef(XModelObject parentRef, XModelObject exception) {
        if (parentRef == null) return;
        XModelObject ref = parentRef.getChildByPath(exception.getAttributeValue(ATT_ID));
        if (ref != null) {
            ((ReferenceObjectImpl)exception).setReference(ref);
        }
    }
    
    // drop dead objects
    private void cleanObjects() {
        if (!process.isActive()) return;
        Iterator<XModelObject> it = objects.values().iterator();
        while (it.hasNext()) {
            XModelObject o = (XModelObject)it.next();
            if (!o.isActive()) it.remove();
        }
    }
    
    private Map<String,XModelObject> getChildren(XModelObject object) {
        Map<String,XModelObject> map = new HashMap<String,XModelObject>();
        XModelObject[] children = object.getChildren();
        for (int i = 0; i < children.length; i++) {
            map.put(children[i].getPathPart(), children[i]);
        }
        return map;
    }
    
    private void removeChildren(XModelObject object) {
        XModelObject[] children = object.getChildren();
        for (int i = 0; i < children.length; i++) {
            children[i].removeFromParent();
        }
    }
    
    public static String createName(XModelObject parent, String prefix) {
        int ind = 0;
        while (parent.getChildByPath(prefix+ind) != null) ind++;
        return prefix+ind;
    }
    
    public XModelObject getObject(String path) {
        return (XModelObject)objects.get(path);
    }
    
    public XModelObject getPage(String path) {
        XModelObject page = (XModelObject)pages.get(path);
        if (page != null && page.getParent() == null) {
            pages.remove(path);
            return null;
        }
        return page;
    }

    public void resetPage(XModelObject page, String oldpath, String newpath) {
        pages.remove(oldpath);
        pages.put(newpath, page);
    }
    
    public String getLocalPath2(XModelObject object) {
        if (object == null) return null;
        String path = object.getAttributeValue(ATT_ID);
        XModelObject parent = object.getParent();
        if (parent != null && parent != process) {
            object = parent;
            path = object.getAttributeValue(ATT_ID) + '/' + path;
        }
        String type = object.getAttributeValue(ATT_TYPE);
        if (TYPE_ACTION.equals(type)) {
            path = ELM_ACTIONMAP + '/' + path;
        } else if (TYPE_FORWARD.equals(type)) {
            path = ELM_GLOBALFORW + '/' + path;
        } else if (TYPE_EXCEPTION.equals(type)) {
            path = ELM_GLOBALEXC + '/' + path;
        }
        return path;
    }
    
    public String getLocalPath(XModelObject object) {
        if (object == null) return null;
        String path = object.getPathPart();
        object = object.getParent();
        while (object != config) {
            if (object == null) return null;
            path = object.getPathPart()+"/"+path;
            object = object.getParent();
        }
        return path;
    }
    
    public static List<XModelObject> getReferers(XModelObject root, String target) {
        List<XModelObject> list = new ArrayList<XModelObject>();
        if(root != null) fillReferers(list, root, target);
        return list;
    }

    private static void fillReferers(List<XModelObject> list, XModelObject obj, String target) {
        XModelObject[] children = obj.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (target.equals(children[i].getAttributeValue(ATT_TARGET))) {
            	list.add(children[i]);
            }
            fillReferers(list, children[i], target);
        }
    }
    
    private static Set<String> getReferredTargets(XModelObject object) {
    	Set<String> set = new HashSet<String>();
    	fillReferers(set, object);
    	return set;
    }
    
	private static void fillReferers(Set<String> set, XModelObject obj) {
		XModelObject[] children = obj.getChildren();
		for (int i = 0; i < children.length; i++) {
			String target = children[i].getAttributeValue(ATT_TARGET);
			if(target != null && target.length() > 0) set.add(target);
			fillReferers(set, children[i]);
		}
	}    

    // update pages

    public void updateTiles() {
        Set<String> ts = TilesHelper.getTiles(process).keySet();
        if(!ts.equals(tiles)) updatePages();
    }

    public Set<String> getTiles() {
        return tiles;
    }

    public void updatePages() {
        tiles = TilesHelper.getTiles(process).keySet();
        PageUpdateManager pu = PageUpdateManager.getInstance(process.getModel());
        pu.lock();
        prepareMaps();
        XModelObject[] items = process.getChildren();
        for (int i = 0; i < items.length; i++) pu.updatePage(this, items[i]);
        pu.unlock();
    }
    
    Map<String,XModelObject> osX = new HashMap<String,XModelObject>();
    Map<String,XModelObject> gsX = new HashMap<String,XModelObject>();
    
    private void prepareMaps() {
        Map<String,XModelObject> os = new HashMap<String,XModelObject>();
        Map<String,XModelObject> gs = new HashMap<String,XModelObject>();
        XModelObject[] cs = process.getChildren();
        for (int i = 0; i < cs.length; i++) {
            String path = cs[i].getAttributeValue(ATT_PATH);
            String type = cs[i].getAttributeValue(ATT_TYPE);
            if(TYPE_EXCEPTION.equals(type)) continue;
            if(TYPE_FORWARD.equals(type)) {
                XModelObject r = ((ReferenceObjectImpl)cs[i]).getReference();
                if(r == null) continue;
                gs.put(r.getPathPart(), cs[i]);
            }
            if(TYPE_ACTION.equals(type)) path = urlPattern.getActionUrl(path); //path += ".do";
            os.put(path, cs[i]);
        }
        osX = os;
        gsX = gs;
    }

    public void updatePage(XModelObject page, JSPLinksParser p) {
    	addTask(new UpdatePageTask(page, p));
    }

    private void updatePageInternal(XModelObject page, JSPLinksParser p) {
    	if(urlPattern == null) return;
    	Map<String,XModelObject> os = osX;
    	Map<String,XModelObject> gs = gsX;
    	Set<String> ls = p.getAllLinks();
        XModelObject[] links = page.getChildren();
        for (int i = 0; i < links.length; i++) {
            String path = links[i].getAttributeValue(ATT_PATH);
            String trg = links[i].getAttributeValue(ATT_TARGET);
            XModelObject trgobj = process.getChildByPath(trg);
            if(ls.contains(path)) {
                links[i].setAttributeValue(ATT_SUBTYPE, SUBTYPE_CONFIRMED);
                if(trg.length() > 0 && trgobj != null)
                  ls.remove(path);
                else
                  links[i].removeFromParent();
            } else {
                if(SUBTYPE_CONFIRMED.equals(links[i].getAttributeValue(ATT_SUBTYPE))) {
                    links[i].removeFromParent();
                    if(trgobj != null) {
                        String type = trgobj.getAttributeValue(ATT_TYPE);
                        String subtype = trgobj.getAttributeValue(ATT_SUBTYPE);
                        String objpath = trgobj.getAttributeValue(ATT_PATH);
                        if(TYPE_ACTION.equals(type) && SUBTYPE_UNKNOWN.equals(subtype)) {
                        	List<XModelObject> l = getReferers(process, trgobj.getPathPart());
                        	if(l.isEmpty()) {
								actions.remove(objpath);
								trgobj.removeFromParent();
							}
                        } else if(TYPE_PAGE.equals(type) && !h.isPageConfirmed(trgobj)) {
                            pages.remove(objpath);
                            trgobj.removeFromParent();
                        } else {
///possible phantom
///        S//ystem.out.println("c:" + objpath + " type=" + type + " subtype=" + subtype);
                        }
                    }
                } else if(trg.length() == 0 || process.getChildByPath(trg) == null) {
                	addBind(links[i]);
                }
            }
        }
        for (String path: ls) {
            boolean isForward = p.isForward(path);
            XModelObject target = (isForward) ? (XModelObject)gs.get(path)
                                              : (XModelObject)os.get(path);
            XModelObject link = addLink(page, target, path);
            if(target == null) {
            	addBind(link);
            }
        }
        if(binds.size() > 0) {
        	resolveInternal();
			removeUnconfirmed();
        } 
    }

    private static XModelObject addLink(XModelObject page, XModelObject target, String path) {
        Properties q = new Properties();
        q.setProperty(ATT_NAME, createName(page, "link"));
        q.setProperty(ATT_TYPE, TYPE_LINK);
        q.setProperty(ATT_SUBTYPE, SUBTYPE_CONFIRMED);
        q.setProperty(ATT_TITLE, path);
        q.setProperty(ATT_PATH, path);
        if(target != null) {
            q.setProperty(ATT_TARGET, target.getAttributeValue(ATT_NAME));
            if(TYPE_FORWARD.equals(target.getAttributeValue(ATT_TYPE))) q.setProperty("shortcut", "yes");
        }
        XModelObject link = page.getModel().createModelObject(ENT_PROCESSITEMOUT, q);
        page.addChild(link);
        return link;
    }

    public void updateTile(XModelObject page) {
        String path = page.getAttributeValue(ATT_PATH);
        if(path.startsWith("/")) return;
        page.setAttributeValue(ATT_SUBTYPE, SUBTYPE_TILE);
        page.setAttributeValue("confirmed", "" + tiles.contains(path));
    }

    public void autolayout() {
        AutoLayout auto = new AutoLayout();
        auto.setItems(new StrutsItems());
        auto.setProcess(process);
    }

	public StrutsBreakpointManager getBreakpointManager() {
		return breakpointManager; 
	}
}

