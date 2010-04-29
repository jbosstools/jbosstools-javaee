package org.jboss.tools.jsf.web.validation.jsf2.components;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;
import org.w3c.dom.NamedNodeMap;

@SuppressWarnings("restriction")
public class JSF2CompositeTempComponent implements IJSF2ValidationComponent {
	private int length;
	private int startOffSet;
	private int line;
	private String validationMessage = ""; //$NON-NLS-1$
	private Object[] messageParams;
	private List<String> attrNames = new ArrayList<String>(0);
	private ElementImpl element;
	private String componentResLoc;

	public JSF2CompositeTempComponent(ElementImpl element) {
		this.element = element;
	}

	public int getLength() {
		return length;
	}

	void setLength(int length) {
		this.length = length;
	}

	public int getLine() {
		return line;
	}

	void setLine(int lineNumber) {
		this.line = lineNumber;
	}

	public int getStartOffSet() {
		return startOffSet;
	}

	void setStartOffSet(int startOffSet) {
		this.startOffSet = startOffSet;
	}

	void createValidationMessage() {
		String nodeName = element.getNodeName();
		if (nodeName.indexOf(':') > -1) {
			nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
		}
		this.validationMessage = MessageFormat.format(
				JSFUIMessages.Missing_JSF_2_Composite_Component, nodeName);
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	void createMessageParams() {
		NamedNodeMap attrsMap = element.getAttributes();
		if (attrsMap != null && attrsMap.getLength() != 0) {
			for (int i = 0; i < attrsMap.getLength(); i++) {
				IDOMAttr attr = (IDOMAttr) attrsMap.item(i);
				attrNames.add(attr.getName());
			}
		}
		this.messageParams = new Object[] { this };
	}

	public Object[] getMessageParams() {
		return messageParams;
	}

	public String[] getAttrNames() {
		return attrNames.toArray(new String[0]);
	}

	public String getType() {
		return JSF2_COMPOSITE_COMPONENT_TYPE;
	}

	public String getComponentResourceLocation() {
		if (componentResLoc == null) {
			String uriString = element.getNamespaceURI();
			String relativeLocation = uriString.replaceFirst(
					JSF2ResourceUtil.JSF2_URI_PREFIX, ""); //$NON-NLS-1$
			String nodeName = element.getNodeName();
			if (nodeName.indexOf(':') > -1) {
				nodeName = nodeName.substring(nodeName.lastIndexOf(':') + 1);
			}
			componentResLoc = relativeLocation + "/" + nodeName + ".xhtml"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return componentResLoc;
	}

	public int getSeverity() {
		return IMessage.NORMAL_SEVERITY;
	}

}
