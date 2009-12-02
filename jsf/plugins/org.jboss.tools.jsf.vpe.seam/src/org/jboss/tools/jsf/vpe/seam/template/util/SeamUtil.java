/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.jsf.vpe.seam.SeamTemplatesActivator;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeTemplateManager;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.XmlUtil;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class contains Seam tags, general attributes and static methods usually used
 * in Seam template classes.
 * 
 * @author dmaliarevich
 * 
 */
public class SeamUtil {

	public static final String ATTR_TEMPLATE = "template"; //$NON-NLS-1$
	public static final String ATTR_STYLE_CLASS = "styleClass"; //$NON-NLS-1$
	public static final String ATTR_URL = "url"; //$NON-NLS-1$
	public final static String SEAM_ATTR_MARGINS = "margins"; //$NON-NLS-1$
	public final static String[] POSSIBLE_ALIGNS = new String[] { "left", //$NON-NLS-1$
			"right", "center", "justify", "justifyall" };   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
	public final static String SEAM_ATTR_ALIGNMENT = "alignment"; //$NON-NLS-1$
	public final static String SEAM_DIV_BASIC_STYLE_VALUE = "line-height: 142.0pt; font-family: unknown;"; //$NON-NLS-1$
	public final static String SEAM_SPAN_BASIC_STYLE_VALUE = "font-family: unknown;"; //$NON-NLS-1$
	public final static String SEAM_ATTR_HORIZONAL_ALIGNMENT = "horizontalAlignment"; //$NON-NLS-1$
	public final static String SEAM_ATTR_VERTICAL_ALIGNMENT = "verticalAlignment"; //$NON-NLS-1$
	public final static String SEAM_ATTR_WIDTH_PERCENTAGE = "widthPercentage"; //$NON-NLS-1$

	public static Node getParentByName(VpePageContext pageContext,
			Node sourceNode, String parentName) {

		Node parentNode = sourceNode.getParentNode();

		while (parentNode != null) {
			String parentSourcePrefix = parentNode.getPrefix();
			if(parentSourcePrefix==null) {
				parentSourcePrefix=""; //$NON-NLS-1$
			}
			List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(parentNode,
					pageContext);
			TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(
					parentSourcePrefix, taglibs);

			String parentNodeName = parentNode.getNodeName();
			if (sourceNodeTaglib != null) {
				String sourceNodeUri = sourceNodeTaglib.getUri();
				String templateTaglibPrefix = VpeTemplateManager.getInstance()
						.getTemplateTaglibPrefix(sourceNodeUri);

				if (templateTaglibPrefix != null) {
					parentNodeName = templateTaglibPrefix
							+ ":" + parentNode.getLocalName(); //$NON-NLS-1$
				}
			}
			if (parentNodeName.equals(parentName)) {
				return parentNode;
			}
			parentNode = parentNode.getParentNode();
		}
		return null;
	}

	public static void setAlignment(Element sourceElement,
			nsIDOMElement visualElement) {
		String align = sourceElement.getAttribute(SeamUtil.SEAM_ATTR_ALIGNMENT);
		if (align != null) {
			visualElement.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_ALIGN_LEFT);
			for (int i = 0; i < SeamUtil.POSSIBLE_ALIGNS.length; i++) {
				if (SeamUtil.POSSIBLE_ALIGNS[i].equalsIgnoreCase(align)) {
					if (SeamUtil.POSSIBLE_ALIGNS[i]
							.equalsIgnoreCase("justifyall")) { //$NON-NLS-1$
						visualElement.setAttribute(HTML.ATTR_ALIGN,
								HTML.VALUE_ALIGN_JUSTIFY);
						return;
					}
					visualElement.setAttribute(HTML.ATTR_ALIGN,
							SeamUtil.POSSIBLE_ALIGNS[i]);
					return;
				}
			}
		}
	}

	public static void getChildsByName(VpePageContext pageContext,
			Node sourceNode, String childName, List<Node> childNodes) {
		NodeList children = sourceNode.getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node childNode = children.item(i);
				String childSourcePrefix = childNode.getPrefix();
				if(childSourcePrefix==null) {
					childSourcePrefix=""; //$NON-NLS-1$
				}
				List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(childNode,
						pageContext);

				TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(
						childSourcePrefix, taglibs);

				String childNodeName = childNode.getNodeName();
				if (sourceNodeTaglib != null) {
					String sourceNodeUri = sourceNodeTaglib.getUri();
					String templateTaglibPrefix = VpeTemplateManager
							.getInstance().getTemplateTaglibPrefix(
									sourceNodeUri);

					if (templateTaglibPrefix != null) {
						childNodeName = templateTaglibPrefix
								+ ":" + childNode.getLocalName(); //$NON-NLS-1$
					}
				}
				if (childNodeName.equals(childName)) {
					childNodes.add(childNode);
				}
				getChildsByName(pageContext, childNode, childName, childNodes);
			}
		}
	}

	public static Node[] getChildsByName(VpePageContext pageContext,
			Node sourceNode, String childName) {
		List<Node> footerNodes = new ArrayList<Node>(0);
		getChildsByName(pageContext, sourceNode, childName, footerNodes);
		return footerNodes.toArray(new Node[0]);
	}

	public static String getStyleAttr(Node sourceFontNode) {
		if (sourceFontNode == null
				|| !sourceFontNode.getNodeName().endsWith("font")) { //$NON-NLS-1$
			return null;
		}
		String styleAttrValue = getFontFamily(sourceFontNode)
				+ getSize(sourceFontNode) + parseFontStyleValue(sourceFontNode);
		return styleAttrValue;
	}

	private static String parseFontStyleValue(Node sourceFontNode) {
		StringBuffer styleAttrValue = new StringBuffer(""); //$NON-NLS-1$
		String stringValueToParse = ((Element) sourceFontNode)
				.getAttribute(HTML.ATTR_STYLE);
		if (stringValueToParse != null) {
			int boldPos = stringValueToParse.indexOf("bold"); //$NON-NLS-1$
			if (boldPos != -1) {
				styleAttrValue.append(" font-weight : bold;"); //$NON-NLS-1$
			}
			int italicPos = stringValueToParse.indexOf("italic"); //$NON-NLS-1$
			if (italicPos != -1) {
				styleAttrValue.append(" font-style : italic;"); //$NON-NLS-1$
			}
			int underLinePos = stringValueToParse.indexOf("underline"); //$NON-NLS-1$
			if (underLinePos != -1) {
				styleAttrValue.append(" text-decoration : underline;"); //$NON-NLS-1$
			}
			int lineThroughPos = stringValueToParse.indexOf("line-through"); //$NON-NLS-1$
			if (lineThroughPos != -1) {
				styleAttrValue.append(" text-decoration : line-through;"); //$NON-NLS-1$
			}
		}
		return styleAttrValue.toString();
	}

	private static String getFontFamily(Node sourceFontNode) {
		String fontFamily = "unknown"; //$NON-NLS-1$
		String stringName = ((Element) sourceFontNode)
				.getAttribute(HTML.ATTR_NAME);
		if (stringName != null) {
			fontFamily = stringName;
		}
		return " font-family : " + fontFamily + ";"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static String getSize(Node sourceFontNode) {
		String stringSizeAttr = ((Element) sourceFontNode)
				.getAttribute(HTML.ATTR_SIZE);
		if (stringSizeAttr != null) {
			try {
				int intSize = Integer.parseInt(stringSizeAttr);
				if (intSize < 0) {
					return ""; //$NON-NLS-1$
				}
			} catch (NumberFormatException e) {
				return ""; //$NON-NLS-1$
			}
		} else {
			return ""; //$NON-NLS-1$
		}
		return " font-size : " + stringSizeAttr + "pt;"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
    public static String getAbsoluteResourcePath(String resourcePathInPlugin) {
        String pluginPath = SeamTemplatesActivator.getPluginResourcePath();
        IPath pluginFile = new Path(pluginPath);
        File file = pluginFile.append(resourcePathInPlugin).toFile();
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            throw new IllegalArgumentException("Can't get path for " //$NON-NLS-1$
                    + resourcePathInPlugin);
        }
    }
	
    public static void setImg(nsIDOMElement img, String fileImageName) {
        img.setAttribute(HTML.ATTR_SRC, "file://" //$NON-NLS-1$
                + getAbsoluteResourcePath(fileImageName).replace('\\', '/'));
    }
    
}
