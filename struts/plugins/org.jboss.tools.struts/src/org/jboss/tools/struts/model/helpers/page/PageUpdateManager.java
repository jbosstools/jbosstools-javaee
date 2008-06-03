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
package org.jboss.tools.struts.model.helpers.page;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.page.link.*;

public class PageUpdateManager implements XModelTreeListener, Runnable, StrutsConstants {

    public static PageUpdateManager getInstance(XModel model) {
		PageUpdateManager instance = (PageUpdateManager)model.getManager("PageUpdateManager");
        if(instance == null) {
            instance = new PageUpdateManager();
            instance.model = model;
            try {
            	instance.init();
            } catch (Exception e) {
                model.addModelTreeListener(instance);
                StrutsModelPlugin.getPluginLog()
                		.logError("PageUpdateManager failed for "
                				+ model.getProperties().get("project"), e);
                return instance;
            	
            }
			model.addManager("PageUpdateManager", instance);
//			String name = "Page Update - " + XModelConstants.getWorkspace(model);
//            new Thread(instance, name).start();
            model.addModelTreeListener(instance);
        }
        return instance;
    }

    private static long id = 0;

    private XModel model;
    private Map<String,PageLinks> pages = new HashMap<String,PageLinks>();
    private int lock = 0;
    protected boolean stopped = false;
    
    Links links;
	LinksListener linksListener;
	
	PageUpdateRunnable runnable = new PageUpdateRunnable();

    public PageUpdateManager() {}
    
    private void init() {
    	links = LinkRecognizer.getInstance().getLinks();
		links.addLinksListener(linksListener = new LinksListenerImpl());
    }

    public void updatePage(StrutsProcessHelper h, XModelObject page) {
        if(SUBTYPE_TILE.equals(page.getAttributeValue(ATT_SUBTYPE))) {
            h.updateTile(page);
            return;
        }
        PageLinks pl = getPageLinks(page);
        if(pl == null) return;
        pl.h = h;
        pl.update();
    }

    PageLinks getPageLinks(XModelObject page) {
        if(!TYPE_PAGE.equals(page.getAttributeValue(ATT_TYPE))) return null;
        if(SUBTYPE_TILE.equals(page.getAttributeValue(ATT_SUBTYPE))) return null;
        String pid = page.get("_page_id");
        if(pid == null) {
            pid = "" + (++id);
            page.set("_page_id", pid);
        }
        PageLinks pl = (PageLinks)pages.get(pid);
        if(pl == null) {
            pl = new PageLinks();
            pl.setPage(page);
            pages.put(pid, pl);
        }
        return pl;
    }

    public void updateAll() {
        if(isLocked()) return;
        lock();
        try {
            String[] ks = (String[])pages.keySet().toArray(new String[0]);
            for (int i = 0; i < ks.length; i++) {
                PageLinks pl = (PageLinks)pages.get(ks[i]);
                if(pl == null) continue;
                if(!pl.page.isActive()) {
                    pages.remove(ks[i]);
                } else {
                    pl.update();
                }
            }
        } finally {
            unlock();
        }
    }
    
    public boolean isLocked() {
    	return lock > 0;
    }

    public void lock() {
        lock++;
    }

    public void unlock() {
        lock--;
    }

    public void structureChanged(XModelTreeEvent event) {
        XModel model = event.getModelObject().getModel();
        if (event.kind() == XModelTreeEvent.STRUCTURE_CHANGED &&
                event.getModelObject() == model.getRoot()) {
            model.removeModelTreeListener(this);
			PageUpdateManager instance = (PageUpdateManager)model.getManager("PageUpdateManager");
			if(instance != null) { 
				instance.stopped = true;
				model.removeManager("PageUpdateManager");
			}
			if(linksListener != null) {
				links.addLinksListener(linksListener);
				linksListener = null;
				links = null;
			}
            return;
        } else if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
            onChildAdded(event);
        }
        if(isLocked()) return;
//        synchronized(this) {
//            notifyAll();
//        }
        XJob.addRunnable(runnable);
    }

    private void onChildAdded(XModelTreeEvent event) {
        XModelObject c = (XModelObject)event.getInfo();
        if(!c.getModelEntity().getName().equals("StrutsProcessItem")) return;
        StrutsProcessImpl pi = (StrutsProcessImpl)event.getModelObject();
        PageLinks pl = getPageLinks(c);
        if(pl == null || pi.getHelper() == null) return;
        pl.h = pi.getHelper();
    }

    public void nodeChanged(XModelTreeEvent event) {
        if(isLocked()) return;
//        synchronized(this) {
//            notifyAll();
//        }
        XJob.addRunnable(runnable);
    }

    public void run() {
        while(!stopped) {
            synchronized(this) {
                try {
                	wait();
                } catch (InterruptedException ie) {
                	StrutsModelPlugin.getPluginLog().logInfo(ie.getMessage(), ie);
                } catch (Exception e) {
                	StrutsModelPlugin.getPluginLog().logError(e);
                }
            }

            try {
            	Thread.sleep(250);
            } catch (InterruptedException ie) {
            	StrutsModelPlugin.getPluginLog().logInfo(ie.getMessage(), ie);
            } catch (Exception e) {
            	StrutsModelPlugin.getPluginLog().logError(e);
            }
            
            if(stopped) break;
            
            if(!isLocked()) {
                try {
                	updateAll();
                } catch (Exception t) {
                	StrutsModelPlugin.getPluginLog().logError(t);
                }
            }
        }
    }
    
    class PageUpdateRunnable implements XJob.XRunnable {

		public String getId() {
			return "Page Update - " + XModelConstants.getWorkspace(model);
		}

		public void run() {
            if(!isLocked()) {
                try {
                	updateAll();
                } catch (Exception t) {
                	StrutsModelPlugin.getPluginLog().logError(t);
                }
            }
		}
    	
    }
    
    class LinksListenerImpl implements LinksListener {

		public void linksChanged() {
			PageLinks[] ps = (PageLinks[])pages.values().toArray(new PageLinks[0]);
			for (int i = 0; i < ps.length; i++) ps[i].jspTimeStamp = -1;
			nodeChanged(null);			
		}
    	
    }

}

class PageLinks implements StrutsConstants {
    StrutsProcessHelper h;
    XModelObject page;
    XModelObject jsp;
    boolean confirmed = false;
    long jspTimeStamp;
    long pageTimeStamp;
    JSPLinksParser parser = new JSPLinksParser(LinkRecognizer.getInstance().getLinks());
    
    public void setPage(XModelObject page) {
    	this.page = page;
		parser.setUrlPattern(StrutsProcessStructureHelper.instance.getUrlPattern(page));
    }

    public void update() {
        if(page.getParent() == null) return;
        boolean modified = (pageTimeStamp != page.getTimeStamp());
        pageTimeStamp = page.getTimeStamp();

        String path = StrutsProcessStructureHelper.instance.getModulePagePath(page);
        if(path == null || StrutsProcessHelper.isHttp(path)) {
            jsp = null;
            setConfirmed(false);
            jspTimeStamp = -1;
            if(modified) update0();
            return;
        }

        XModelObject jsp1 = StrutsProcessStructureHelper.instance.findReferencedJSPInCurrentModule(page);
                            ////page.getModel().getByPath(path);
        if(jsp == null || !jsp.isActive() || jsp != jsp1) {
            jsp = jsp1;
            setConfirmed(jsp != null);
            jspTimeStamp = -1;
            if(jsp == null) {
                if(modified) update0();
                return;
            }
        }
        if(jsp.getTimeStamp() != jspTimeStamp) {
            JSPLinksParser p1 = new JSPLinksParser(LinkRecognizer.getInstance().getLinks());
            p1.setUrlPattern(StrutsProcessStructureHelper.instance.getUrlPattern(page));
            p1.setSource(jsp.getAttributeValue("body"));
            p1.parse();
            modified = p1.areLinksModified(parser) || jspTimeStamp == -1;
            parser = p1;
            jspTimeStamp = jsp.getTimeStamp();
        }
//        if(modified) 
        	update0();
    }

    private void update0() {
        h.updatePage(page, parser);
    }

    private void setConfirmed(boolean b) {
///        if(confirmed == b) return;
        confirmed = b;
        if(confirmed == ("true".equals(page.get("confirmed")))) return;
        page.setAttributeValue("confirmed", "" + confirmed);
        if(!confirmed) {
            XModelObject[] links = page.getChildren();
            for (int i = 0; i < links.length; i++)
              links[i].setAttributeValue(ATT_SUBTYPE, "");
        }
    }

}
