

package org.jboss.tools.jsf.vpe.richfaces.template;


import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.jboss.tools.vpe.xulrunner.browser.util.DOMTreeDumper;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The template for the <rich:fileUpload/>.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesFileUploadTemplate extends VpeAbstractTemplate {

    /** The Constant DEFAULT_CONTROL_LABEL_VALUE. */
    private static final String DEFAULT_CONTROL_LABEL_VALUE = "Add...";

    /** The Constant DEFAULT_LIST_HEIGHT. */
    private static final String DEFAULT_LIST_HEIGHT = "210px";

    /** The Constant DEFAULT_LIST_WIDTH. */
    private static final String DEFAULT_LIST_WIDTH = "400px";

    /** The Constant FILE_UPLOAD_FILE_UPLOAD_CSS. */
    private static final String FILE_UPLOAD_FILE_UPLOAD_CSS = "fileUpload/fileUpload.css";

    /** The Constant RICH_FACES_FILE_UPLOAD_EXT. */
    private static final String RICH_FACES_FILE_UPLOAD_EXT = "richFacesFileUpload";

    /** The add control label. */
    private String addControlLabel;

    /** The default style classes. */
    private final Map<String, String> defaultStyleClasses = new HashMap<String, String>();

    /** The list height. */
    private String listHeight;

    /** The list width. */
    private String listWidth;

    /**
     * The Constructor.
     */
    public RichFacesFileUploadTemplate() {
        super();
        initDefaultStyleClasses();
    }

    /**
     * Create.
     * 
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * @param pageContext the page context
     * 
     * @return the vpe creation data
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

        final Element source = (Element) sourceNode;
        prepareData(source);
        VpeCreationData data = null;
        ComponentUtil.setCSSLink(pageContext, FILE_UPLOAD_FILE_UPLOAD_CSS, RICH_FACES_FILE_UPLOAD_EXT); //$NON-NLS-1$ //$NON-NLS-2$

        final nsIDOMElement rootDiv = visualDocument.createElement(HTML.TAG_DIV);

        rootDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-list-decor");
        rootDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + this.listWidth);
        final nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);

        table.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-toolbar-decor");
        final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement buttonBorderDiv = visualDocument.createElement(HTML.TAG_DIV);

        buttonBorderDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-button-border");
        buttonBorderDiv.setAttribute(HTML.ATTR_STYLE, "float: left;");

        final nsIDOMElement fileuploadButtonDiv = visualDocument.createElement(HTML.TAG_DIV);
        fileuploadButtonDiv.setAttribute(HTML.ATTR_CLASS, defaultStyleClasses.get("addButtonClass"));
        fileuploadButtonDiv.setAttribute(HTML.ATTR_STYLE, "position: relative;");

        final nsIDOMElement labelDiv = visualDocument.createElement(HTML.TAG_DIV);

        labelDiv.setAttribute(HTML.ATTR_CLASS, defaultStyleClasses.get("addButtonClassDiv2"));

        rootDiv.appendChild(table);
        rootDiv.appendChild(createPanelDiv(pageContext, source, visualDocument));
        table.appendChild(tr);
        tr.appendChild(td);
        td.appendChild(buttonBorderDiv);
        buttonBorderDiv.appendChild(fileuploadButtonDiv);
        fileuploadButtonDiv.appendChild(labelDiv);
        labelDiv.appendChild(visualDocument.createTextNode(this.addControlLabel));

//        DOMTreeDumper dumper = new DOMTreeDumper();
//        dumper.dumpToStream(System.err, rootDiv);

        data = new VpeCreationData(rootDiv);
        return data;
    }

    /**
     * Creates the panel div.
     * 
     * @param visualDocument the visual document
     * @param pageContext the page context
     * @param source the source
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createPanelDiv(VpePageContext pageContext, Element source, nsIDOMDocument visualDocument) {
        final nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);

        div.setAttribute(HTML.ATTR_CLASS, defaultStyleClasses.get("uploadListClass"));
        div.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + "100%" + VpeStyleUtil.SEMICOLON_STRING
                + VpeStyleUtil.PARAMETER_HEIGHT + VpeStyleUtil.COLON_STRING + this.listHeight);
        return div;
    }

    /**
     * Inits the default style classes.
     */
    private void initDefaultStyleClasses() {
        defaultStyleClasses.put("addButtonClass", "rich-fileupload-button rich-fileupload-font");
        defaultStyleClasses.put("addButtonClassDiv2",
                " rich-fileupload-button-content rich-fileupload-font rich-fileupload-ico rich-fileupload-ico-add");
        defaultStyleClasses.put("uploadListClass", "rich-fileupload-list-overflow");

    }

    /**
     * Checks if is recreate at attr change.
     * 
     * @param sourceElement the source element
     * @param visualDocument the visual document
     * @param value the value
     * @param visualNode the visual node
     * @param data the data
     * @param pageContext the page context
     * @param name the name
     * 
     * @return true, if is recreate at attr change
     */
    @Override
    public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        return true;
    }

    /**
     * Prepare data.
     * 
     * @param sourceElement the source element
     */
    private void prepareData(Element sourceElement) {
        try {
            listHeight = String.valueOf(ComponentUtil.parseWidthHeightValue(sourceElement.getAttribute("listHeight")));
        } catch (NumberFormatException e) {
            listHeight = DEFAULT_LIST_HEIGHT;
        }
        try {
            listWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(sourceElement.getAttribute("listWidth")));
        } catch (NumberFormatException e) {
            listWidth = DEFAULT_LIST_WIDTH;
        }

        addControlLabel = sourceElement.getAttribute("addControlLabel");
        if (addControlLabel == null) {
            addControlLabel = DEFAULT_CONTROL_LABEL_VALUE;
        }

        String addButtonClass = sourceElement.getAttribute("addButtonClass");

        if (ComponentUtil.isNotBlank(addButtonClass)) {
            defaultStyleClasses.put("addButtonClass", defaultStyleClasses.get("addButtonClass") + " " + addButtonClass);
            defaultStyleClasses.put("addButtonClassDiv2", defaultStyleClasses.get("addButtonClassDiv2") + " " + addButtonClass);
        }

        String uploadListClass = sourceElement.getAttribute("uploadListClass");

        if (ComponentUtil.isNotBlank(uploadListClass)) {
            defaultStyleClasses.put("uploadListClass", defaultStyleClasses.get("uploadListClass") + " " + uploadListClass);
        }
    }

}
