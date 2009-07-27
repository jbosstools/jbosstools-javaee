/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for rich:editor.
 * 
 * @author yradtsevich
 * 
 */
public class RichFacesEditorTemplate extends VpeAbstractTemplate {
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		
		RichFacesEditorTemplateHelper editorTemplateHelper = 
			new RichFacesEditorTemplateHelper(
				pageContext, (Element) sourceNode, visualDocument
			);
		nsIDOMElement mainElement = editorTemplateHelper.create();
		
		VpeCreationData creationData = new VpeCreationData(mainElement);
		return creationData;
	}
	
	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}
}

/**
 * Creates visual nodes for rich:editor.
 * 
 * @author yradtsevich
 * 
 */
class RichFacesEditorTemplateHelper {
	private static final String STYLE_PATH = "editor/editor.css"; //$NON-NLS-1$
	
	private final VpePageContext pageContext;
	private final Element sourceElement;
	private final nsIDOMDocument visualDocument;
	
	public RichFacesEditorTemplateHelper(VpePageContext pageContext,
			Element sourceNode, nsIDOMDocument visualDocument) {
		this.pageContext = pageContext;
		this.sourceElement = sourceNode;
		this.visualDocument = visualDocument;
	}

	public nsIDOMElement create() {
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "editor"); //$NON-NLS-1$

		String style = sourceElement.getAttribute(RichFaces.ATTR_STYLE);
		if (style == null) {
			style = ""; //$NON-NLS-1$
		}
		String styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		if (styleClass == null) {
			styleClass = ""; //$NON-NLS-1$
		}

		// create nodes
		nsIDOMElement mainSpan = visualDocument.createElement(HTML.TAG_SPAN); {
			mainSpan.setAttribute(HTML.ATTR_CLASS, "richfacesSimpleSkin " + styleClass); //$NON-NLS-1$

			// Yahor Radtsevich: Fix of JBIDE-3653: inFlasher doesn't shows for rich:editor component
			// (style "display: table;" has been added)
			mainSpan.setAttribute(HTML.ATTR_STYLE, "display: table;" + style); //$NON-NLS-1$
		}
		nsIDOMElement mainTable = createMainTable();
		nsIDOMElement textContainer = createTextContainer();
		nsIDOMElement toolbar = createToolbar();
		
		// nest created nodes
		mainSpan.appendChild(mainTable); {
			mainTable.appendChild(textContainer);
			mainTable.appendChild(toolbar);
		}
		
		return mainSpan;
	}

	/**
	 * Creates {@code
	 * 	<table cellspacing="0" cellpadding="0" class="mceLayout" style="width: ???px; height: ???px;"/>
	 * }
	 * @return created element
	 */
	private nsIDOMElement createMainTable() {
		// evaluate width and height
		String style = "width: 183px; height: 100px;"; //$NON-NLS-1$
		String width = sourceElement.getAttribute(RichFaces.ATTR_WIDTH);
		if (width != null) {
			width = VpeStyleUtil.addPxIfNecessary(width);
			style = VpeStyleUtil.setParameterInStyle(style, HTML.STYLE_PARAMETER_WIDTH, width);
		}
		String height = sourceElement.getAttribute(RichFaces.ATTR_HEIGHT);
		if (height != null) {
			height = VpeStyleUtil.addPxIfNecessary(height);
			style = VpeStyleUtil.setParameterInStyle(style, HTML.STYLE_PARAMETER_HEIGHT, height);
		}
		
		nsIDOMElement mainTable = visualDocument.createElement(HTML.TAG_TABLE); {
			mainTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
			mainTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
			mainTable.setAttribute(HTML.ATTR_CLASS, "mceLayout"); //$NON-NLS-1$
			mainTable.setAttribute(HTML.ATTR_STYLE, style);
		}

		return mainTable;
	}
	
	/**
	 * Creates {@code
	 * 	<tr style="height: 100%;">
	 *			<td>
	 *				<div class="mceIframeContainer" style="height: 100%;">
	 *					<table border="0" style="width: 100%; height: 100%;">
	 *						<tr><td>&nbsp;</td></tr>
	 *					</table>
	 *				</div>
	 *			</td>
	 *		</tr>
	 * }
	 * 
	 * @return created element
	 */
	private nsIDOMElement createTextContainer() {
		// create nodes
		nsIDOMElement outerTR = visualDocument.createElement(HTML.TAG_TR); {
			outerTR.setAttribute(HTML.ATTR_STYLE, "height: 100%;"); //$NON-NLS-1$
		}
		nsIDOMElement outerTD = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV); {
			div.setAttribute(HTML.ATTR_CLASS, "mceIframeContainer"); //$NON-NLS-1$
			div.setAttribute(HTML.ATTR_STYLE, "height: 100%;"); //$NON-NLS-1$
		}
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE); {
			table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
			table.setAttribute(HTML.ATTR_STYLE, "width: 100%; height: 100%;"); //$NON-NLS-1$
		}
		nsIDOMElement innerTR = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement innerTD = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMText text = visualDocument.createTextNode(" "); //$NON-NLS-1$
		
		// nest created nodes
		outerTR.appendChild(outerTD); {
			outerTD.appendChild(div); {
				div.appendChild(table); {
					table.appendChild(innerTR); {
						innerTR.appendChild(innerTD); {
							innerTD.appendChild(text);
						}
					}
				}
			}
		}
		
		return outerTR;
	}
	
	/**
	 * Creates {@code
	 * 		<tr>
	 *			<td class="mceToolbar" >
	 *				<table cellspacing="0" cellpadding="0" class="mceToolbarTable">
	 *					<tr>
	 *						<td>
	 *							<div class="mce_panel"/>
	 *						</td>
	 *					</tr>
	 *				</table>
	 *			</td>
	 *		</tr>
	 * }
	 * 
	 * @return created element
	 */
	private nsIDOMElement createToolbar() {
		// create nodes
		nsIDOMElement outerTR = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement outerTD = visualDocument.createElement(HTML.TAG_TD); {
			outerTD.setAttribute(HTML.ATTR_CLASS, "mceToolbar"); //$NON-NLS-1$
		}
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE); {
			table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
			table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
			table.setAttribute(HTML.ATTR_CLASS, "mceToolbarTable"); //$NON-NLS-1$
		}
		nsIDOMElement innerTR = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement innerTD = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV); {
			div.setAttribute(HTML.ATTR_CLASS, "mce_panel"); //$NON-NLS-1$
		}
		
		// nest created nodes
		outerTR.appendChild(outerTD); {
			outerTD.appendChild(table); {
				table.appendChild(innerTR); {
					innerTR.appendChild(innerTD); {
						innerTD.appendChild(div);
					}
				}
			}
		}

		return outerTR;
	}
}
