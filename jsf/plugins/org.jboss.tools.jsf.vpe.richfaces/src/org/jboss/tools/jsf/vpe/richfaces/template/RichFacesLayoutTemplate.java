package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesLayoutTemplate extends VpeAbstractTemplate {

	private static final String FLOAT_LEFT_STYLE = ";float: left;"; //$NON-NLS-1$
	private static final String FLOAT_RIGHT_STYLE = ";float: right;"; //$NON-NLS-1$
	private static final String BOTTOM_SECONDARY_DIV_STYLE = ";display: block; height: 0; clear: both; visibility: hidden;"; //$NON-NLS-1$
	private static final String BOTTOM_SECONDARY_DIV_TEXT = "."; //$NON-NLS-1$
	private static final String LAYOUT_PANEL_NAME = ":layoutPanel"; //$NON-NLS-1$
	
	/**
	 * Constructor
	 */
	public RichFacesLayoutTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement mainDiv = visualDocument.createElement(HTML.TAG_DIV);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		if (ComponentUtil.isNotBlank(style)) {
			mainDiv.setAttribute(HTML.ATTR_STYLE, style);
		}
		nsIDOMElement topDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement centerDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement leftDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement rightDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement bottomDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement bottomSecondaryDiv = visualDocument.createElement(HTML.TAG_DIV);
		creationData = new VpeCreationData(mainDiv);
		
		bottomSecondaryDiv.setAttribute(HTML.ATTR_STYLE,
				BOTTOM_SECONDARY_DIV_STYLE);
		bottomSecondaryDiv.appendChild(visualDocument
				.createTextNode(BOTTOM_SECONDARY_DIV_TEXT));
		bottomDiv.appendChild(bottomSecondaryDiv);
		
		NodeList children = sourceNode.getChildNodes();
		Map<String, Element> panels = new HashMap<String, Element>();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && node.getNodeName() != null
					&& node.getNodeName().indexOf(LAYOUT_PANEL_NAME) > 0) {
				Element element = (Element) node;
                String position = element.getAttribute(RichFaces.ATTR_POSITION);
                /*
                 * Adding several panel with the same position is forbidden.
                 * During adding to the map only the last panel  
                 * with repeating position name will be displayed.
                 */
                if (ComponentUtil.isNotBlank(position)) {
                	if (RichFaces.VALUE_TOP.equalsIgnoreCase(position)) {
                		panels.put(RichFaces.VALUE_TOP, element);
                	} else if (RichFaces.VALUE_LEFT.equalsIgnoreCase(position)) {
                		panels.put(RichFaces.VALUE_LEFT, element);
                	} else if (RichFaces.VALUE_CENTER.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_CENTER, element);
					} else if (RichFaces.VALUE_RIGHT.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_RIGHT, element);
					} else if (RichFaces.VALUE_BOTTOM.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_BOTTOM, element);
					}
				}
            }
		}
		
		/*
		 * Adding panels' divs.
		 * Order is important!
		 */
		int columsCount = 0;
		if (null != panels.get(RichFaces.VALUE_LEFT)) {
			columsCount++;
		}
		if (null != panels.get(RichFaces.VALUE_CENTER)) {
			columsCount++;
		}
		if (null != panels.get(RichFaces.VALUE_RIGHT)) {
			columsCount++;
		}
		addPanelFromMap(RichFaces.VALUE_TOP, panels, mainDiv, topDiv,
				Constants.EMPTY, columsCount, creationData);
		addPanelFromMap(RichFaces.VALUE_LEFT, panels, mainDiv, leftDiv,
				FLOAT_LEFT_STYLE, columsCount, creationData);
		addPanelFromMap(RichFaces.VALUE_CENTER, panels, mainDiv, centerDiv,
				FLOAT_LEFT_STYLE, columsCount, creationData);
		addPanelFromMap(RichFaces.VALUE_RIGHT, panels, mainDiv, rightDiv,
				FLOAT_RIGHT_STYLE, columsCount, creationData);
		addPanelFromMap(RichFaces.VALUE_BOTTOM, panels, mainDiv, bottomDiv,
				Constants.EMPTY, columsCount, creationData);
		return creationData;
	}
	
	private void addPanelFromMap(String panelPositionMapName, Map<String, Element> panelsMap,
			nsIDOMElement mainDiv, nsIDOMElement panelDiv, String style, int columsCount, VpeCreationData creationData) {
		Element panel = panelsMap.get(panelPositionMapName);
		if (null != panel) {
			String widthStr = panel.getAttribute(HTML.ATTR_WIDTH);
			double width = 100;
			switch (columsCount) {
			case 0:
				width = 100;
				break;
			case 1:
				width = 100;
				break;
			case 2:
				width = 50;
				break;
			case 3:
				width = 32.34;
				break;

			default:
				break;
			}
			/*
			 * Apply column width for left, center and right panels only.
			 * Top and bottom panels should always be 100% width.
			 */
			if (!RichFaces.VALUE_TOP.equalsIgnoreCase(panelPositionMapName)
					&& !RichFaces.VALUE_BOTTOM.equalsIgnoreCase(panelPositionMapName)) {
				style += "; width: " + width + "%;"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (ComponentUtil.isNotBlank(widthStr)) {
				panelDiv.setAttribute(HTML.ATTR_WIDTH, widthStr);
			}
			panelDiv.setAttribute(HTML.ATTR_STYLE, style);
			mainDiv.appendChild(panelDiv);
			VpeChildrenInfo panelInfo = new VpeChildrenInfo(panelDiv);
			panelInfo.addSourceChild(panel);
		    creationData.addChildrenInfo(panelInfo);
		}
	}

}
