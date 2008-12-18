package org.jboss.tools.seam.pages.xml.model;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.ui.IViewReference;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.jst.web.model.tree.WebPagesTree;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class ViewIdsTree extends WebPagesTree {
	Map<String, XModelObject> pageMap;
	XModelObject root;
	XModelObject webRoot;
	XModelObject pages;

    public void setConstraint(Object object) {
    	super.setConstraint(object);

        Object[] os = (Object[])object;
        XModelObject c = (XModelObject)os[1];

    	root = FileSystemsHelper.getFileSystems(c.getModel()); 
    	webRoot = FileSystemsHelper.getWebRoot(c.getModel());    		
     
        XModelObject f = SeamPagesDiagramStructureHelper.getInstance().getParentFile(c);
        pages = f.getChildByPath("Pages");
        XModelObject[] ps = pages == null ? new XModelObject[0] : pages.getChildren();
        
        pageMap = new TreeMap<String, XModelObject>();
        
        for (int i = 0; i < ps.length; i++) {
        	String viewId = ps[i].getAttributeValue(SeamPagesConstants.ATTR_VIEW_ID);
        	if(viewId == null || viewId.length() == 0 || viewId.indexOf('*') >= 0) continue;
        	pageMap.put(viewId, ps[i]);
        }
    }

	public XModelObject getRoot() {
		return root; 
	}

    public XModelObject[] getChildren(XModelObject object) {
    	if(object == root) {
    		if(webRoot == null) {
    			XModelObject[] rs = super.getChildren(object);
    			if(pages == null) {
    				return rs;
    			} else {
    				XModelObject[] rs1 = new XModelObject[rs.length + 1];
    				System.arraycopy(rs, 0, rs1, 0, rs.length);
    				rs1[rs.length] = pages;
    				return rs1;
    			}
    		}
    		if(pages == null) {
    			return new XModelObject[]{webRoot};
    		} else {
    			return new XModelObject[]{webRoot, pages};
    		}
    	}
    	if(object == pages) return pageMap.values().toArray(new XModelObject[0]);
        return super.getChildren(object);
    }

    public String getValue(XModelObject object) {
    	if(isLocalPage(object)) {
    		return object.getAttributeValue(SeamPagesConstants.ATTR_VIEW_ID);
    	}
    	return super.getValue(object);
    }

    public XModelObject find(String value) {
    	if(value != null && pageMap.containsKey(value)) {
    		return pageMap.get(value);
    	}
    	return super.find(value);
    }

    public boolean isSelectable(XModelObject object) {
    	if(isLocalPage(object)) {
    		return true;
    	}
    	return super.isSelectable(object);
    }

    public boolean hasChildren(XModelObject object) {
    	if(isLocalPage(object)) {
    		return false;
    	}
    	return super.hasChildren(object);
    }

    private boolean isLocalPage(XModelObject object) {
    	return (object != null && object.getParent() == pages);
    }

}
