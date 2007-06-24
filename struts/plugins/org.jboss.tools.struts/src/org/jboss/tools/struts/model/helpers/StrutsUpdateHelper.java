/*
 * StrutsUpdateHelper.java
 *
 * Created on February 24, 2003, 12:21 PM
 */

package org.jboss.tools.struts.model.helpers;

import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.jst.web.model.helpers.WebProcessUpdateHelper;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import java.util.*;

/**
 *
 * @author  valera
 */
public class StrutsUpdateHelper implements StrutsConstants, WebProcessUpdateHelper {
    
    private XModelObject config;
    private StrutsProcessImpl process;
    private StrutsProcessHelper helper;
    private Map<String,Updater> updaters = new HashMap<String,Updater>();
    
    /** Creates a new instance of StrutsUpdateHelper */
    public StrutsUpdateHelper(StrutsProcessImpl process) {
        this.process = process;
        this.helper = process.getHelper();
        this.config = process.getParent();
        this.updaters.put(ELM_GLOBALFORW, new ForwardUpdater());
        this.updaters.put(ELM_GLOBALEXC, new ExceptionUpdater());
        this.updaters.put(ELM_ACTIONMAP, new ActionUpdater());
        StrutsUpdateManager.getInstance(process.getModel()).register(config.getPath(), this);
    }
    
    public void unregister() {
        StrutsUpdateManager.getInstance(process.getModel()).unregister(config.getPath(), this);
    }
    
    public boolean isActive() {
        return process.isActive();
    }
    
    public void nodeChanged(XModelTreeEvent event, String localPath) {
        int index = localPath == null ? -1 : localPath.indexOf('/');
        String subRootName = index == -1 ? localPath : localPath.substring(0, index);
        Updater updater = (Updater)updaters.get(subRootName);
        if (updater != null) {
            updater.nodeChanged(localPath);
        }
    }
    
    public void structureChanged(XModelTreeEvent event, String localPath) {
        int index = localPath == null ? -1 : localPath.indexOf('/');
        String subRootName = index == -1 ? localPath : localPath.substring(0, index);
        Updater updater = (Updater)updaters.get(subRootName);
        if (updater != null) {
            switch (event.kind()) {
                case XModelTreeEvent.CHILD_ADDED:
                    XModelObject child = (XModelObject)event.getInfo();
                    updater.childAdded(child);
                    break;
                case XModelTreeEvent.CHILD_REMOVED:
                    updater.childRemoved(localPath);
                    break;
                case XModelTreeEvent.STRUCTURE_CHANGED:
                    updater.structureChanged(localPath);
                    break;
            }
        }
    }
    
    interface Updater {
        void childAdded(XModelObject child);
        void childRemoved(String localPath);
        void structureChanged(String localPath);
        void nodeChanged(String localPath);
    }
    
    private void resolveAndReduce() {
		helper.resolve();
		helper.removeUnconfirmed();
    }
    
    class ForwardUpdater implements Updater {
        
        public void childAdded(XModelObject child) {
            if (!child.getModelEntity().getName().startsWith(ENT_FORWARD)) return;
            helper.reloadForward(process, child, null);
            helper.resolve();
        }
        
        public void childRemoved(String localPath) {
            XModelObject forward = helper.getObject(localPath);
            if (forward != null) {
                helper.reloadForward(process, null, forward);
				resolveAndReduce();
            }
        }
        
        public void nodeChanged(String localPath) {
            XModelObject forward = helper.getObject(localPath);
            if (forward != null) {
                helper.reloadForward(process, ((ReferenceObjectImpl)forward).getReference(), forward);
				resolveAndReduce();
            }
        }
        
        public void structureChanged(String localPath) {
            if (localPath.indexOf('/') == -1) helper.updateProcess();
        }
        
    }

    class ExceptionUpdater implements Updater  {
        
        public void childAdded(XModelObject child) {
            if (!child.getModelEntity().getName().startsWith(ENT_EXCEPTION)) return;
            helper.reloadException(process, child, null);
            helper.resolve();
        }
        
        public void childRemoved(String localPath) {
            XModelObject exception = helper.getObject(localPath);
            if (exception != null) {
                helper.reloadException(process, null, exception);
				resolveAndReduce();
            }
        }
        
        public void nodeChanged(String localPath) {
            XModelObject exception = helper.getObject(localPath);
            if (exception != null) {
                helper.reloadException(process, ((ReferenceObjectImpl)exception).getReference(), exception);
				resolveAndReduce();
            }
        }
        
        public void structureChanged(String localPath) {
            if (localPath.indexOf('/') == -1) helper.updateProcess();
        }

    }

    class ActionUpdater implements Updater  {
        
        public void childAdded(XModelObject child) {
            while (!child.getModelEntity().getName().startsWith(ENT_ACTION)) child = child.getParent();
            helper.reloadAction(process, child, helper.getObject(ELM_ACTIONMAP+"/"+child.getPathPart()));
            helper.resolve();
        }
        
        public void childRemoved(String localPath) {
            String actionPath = getActionPath(localPath);
            XModelObject action = helper.getObject(actionPath);
            if (action != null) {
                if (localPath.equals(actionPath)) {
                    helper.removeAction(action);
                } else {
                    helper.reloadAction(process, config.getChildByPath(actionPath), action);
                }
				resolveAndReduce();
            }
        }
        
        public void nodeChanged(String localPath) {
            localPath = getActionPath(localPath);
            XModelObject action = helper.getObject(localPath);
            if (action != null) {
                helper.reloadAction(process, ((ReferenceObjectImpl)action).getReference(), action);
				resolveAndReduce();
            }
        }
        
        public void structureChanged(String localPath) {
            if (localPath.indexOf('/') == -1) {
                helper.updateProcess();
                return;
            }
            localPath = getActionPath(localPath);
            XModelObject action = helper.getObject(localPath);
            if (action != null) {
                helper.reloadAction(process, ((ReferenceObjectImpl)action).getReference(), action);
				resolveAndReduce();
            }
        }
        
        private String getActionPath(String localPath) {
            int ind1 = localPath.indexOf('/');
            if (ind1 == -1) return null;
            int ind2 = localPath.indexOf('/', ind1+1);
            return ind2 == -1 ? localPath : localPath.substring(0, ind2);
        }
    }
}
