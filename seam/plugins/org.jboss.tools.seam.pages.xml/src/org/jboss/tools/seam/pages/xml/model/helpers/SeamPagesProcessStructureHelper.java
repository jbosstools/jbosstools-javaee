package org.jboss.tools.seam.pages.xml.model.helpers;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.helpers.WebProcessStructureHelper;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class SeamPagesProcessStructureHelper extends WebProcessStructureHelper implements SeamPagesConstants {
	SeamPagesProcessStructureHelper instance = new SeamPagesProcessStructureHelper();

	public XModelObject getParentProcess(XModelObject element) {
		XModelObject p = element;
		while(p != null && p.getFileType() == XModelObject.NONE &&
			  !ENT_PROCESS.equals(p.getModelEntity().getName())) p = p.getParent();
		return p;
	}

	public XModelObject[] getItems(XModelObject process) {
		return process.getChildren(ENT_PROCESS_ITEM);
	}

	public XModelObject[] getOutputs(XModelObject item) {
		return item.getChildren(ENT_PROCESS_ITEM_OUTPUT);
	}

	public String getPath(XModelObject element) {
		return element.getAttributeValue(ATTR_PATH);
	}

	public XModelObject getItemOutputTarget(XModelObject itemOutput) {
		return itemOutput.getParent().getParent().getChildByPath(itemOutput.getAttributeValue(ATTR_TARGET));
	}
	
	public String getItemOutputPresentation(XModelObject itemOutput) {
//		boolean s = isShortcut(itemOutput);
		return itemOutput.getPresentationString();
	}
	
	public boolean isPattern(XModelObject item) {
		String path = item.getAttributeValue(ATTR_PATH);
		return (path != null) && (path.length() == 0 || path.indexOf('*') >= 0);
	}
	
	public boolean isUnconfirmedPage(XModelObject item) {
		String type = item.getAttributeValue(ATTR_TYPE);
		if(!TYPE_PAGE.equals(type)) return false;
		if(isPattern(item)) return false;
		return !"true".equals(item.getAttributeValue("confirmed"));
	}

	public XModelObject getReference(XModelObject diagramObject) {
		if(diagramObject instanceof ReferenceObject) {
			return ((ReferenceObject)diagramObject).getReference();
		}
		return null; 
	}
	
}
