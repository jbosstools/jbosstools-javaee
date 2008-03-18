package org.jboss.tools.jsf.vpe.jsf.template;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.vpe.editor.bundle.BundleMap;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public abstract class AbstractOutputJsfTemplate extends AbstractEditableJsfTemplate {

	/**
	 * name of "value" attribute
	 */
	protected static final String VALUE_ATTR_NAME = "value";

	/**
	 * name of "binding" attribute
	 */
	protected static final String BINDING_ATTR_NAME = "binding";

	/**
	 * name of "escape" attribute
	 */
	protected static final String ESCAPE_ATTR_NAME = "escape";

	/**
	 * name of "dir" attribute
	 */
	protected static final String DIR_ATTR_NAME = "dir";

	/**
	 * 
	 * @param element
	 * @return
	 */
	protected Attr getOutputAttributeNode(Element element) {

		if (element.hasAttribute(VALUE_ATTR_NAME))
			return element.getAttributeNode(VALUE_ATTR_NAME);
		else if (element.hasAttribute(BINDING_ATTR_NAME))
			return element.getAttributeNode(BINDING_ATTR_NAME);

		return null;

	}

	/**
	 * copy outputAttributes
	 * 
	 * @param visualElement
	 * @param sourceElement
	 */
	protected void copyOutputJsfAttributes(nsIDOMElement visualElement,
			Element sourceElement) {
		copyGeneralJsfAttributes(visualElement, sourceElement);
		copyAttribute(visualElement, sourceElement, DIR_ATTR_NAME,
				HTML.ATTR_DIR);

	}
}
