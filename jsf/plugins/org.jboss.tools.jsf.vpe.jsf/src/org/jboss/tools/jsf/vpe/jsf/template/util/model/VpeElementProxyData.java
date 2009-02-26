package org.jboss.tools.jsf.vpe.jsf.template.util.model;

import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.w3c.dom.NodeList;

/**
 * TODO check if this class is necessary
 *
 */
public class VpeElementProxyData extends VpeElementData {

	// private IStructuredModel model;

	// private int offset;
	//
	// public void setModel(IStructuredModel model) {
	// this.model = model;
	// }
	//
	// public IStructuredModel getModel() {
	// return model;
	// }
	//
	// public int getOffset() {
	// return offset;
	// }
	//
	// public void setOffset(int offset) {
	// this.offset = offset;
	// }

	private NodeList nodelist;

	public NodeList getNodelist() {
		return nodelist;
	}

	public void setNodelist(NodeList nodelist) {
		this.nodelist = nodelist;
	}

}
