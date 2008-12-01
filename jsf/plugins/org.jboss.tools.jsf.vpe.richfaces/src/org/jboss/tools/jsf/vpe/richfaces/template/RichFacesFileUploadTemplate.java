

package org.jboss.tools.jsf.vpe.richfaces.template;


import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The template for the <rich:fileUpload/>.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesFileUploadTemplate extends VpeAbstractTemplate {

    /** The Constant DEFAULT_CONTROL_LABEL_VALUE. */
    private static final String DEFAULT_CONTROL_LABEL_VALUE = "Add..."; //$NON-NLS-1$

    /** The Constant DEFAULT_LIST_HEIGHT. */
    private static final String DEFAULT_LIST_HEIGHT = "210px"; //$NON-NLS-1$

    /** The Constant DEFAULT_LIST_WIDTH. */
    private static final String DEFAULT_LIST_WIDTH = "400px"; //$NON-NLS-1$

    /** The Constant FILE_UPLOAD_FILE_UPLOAD_CSS. */
    private static final String FILE_UPLOAD_FILE_UPLOAD_CSS = "fileUpload/fileUpload.css"; //$NON-NLS-1$

    /** The Constant RICH_FACES_FILE_UPLOAD_EXT. */
    private static final String RICH_FACES_FILE_UPLOAD_EXT = "richFacesFileUpload"; //$NON-NLS-1$

    /** The add control label. */
    private String addControlLabel;

    /** The default style classes. */

    /** The list height. */
    private String listHeight;

    /** The list width. */
    private String listWidth;
    
    private String uploadControlLabel;

    
    private String clearAllControlLabel;
    
    /**
     * The Constructor.
     */
    public RichFacesFileUploadTemplate() {
        super();
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
        final StyleClasses styleClasses = new StyleClasses(source);
        prepareData(source);
        VpeCreationData data = null;
        ComponentUtil.setCSSLink(pageContext, FILE_UPLOAD_FILE_UPLOAD_CSS, RICH_FACES_FILE_UPLOAD_EXT); //$NON-NLS-1$ //$NON-NLS-2$

        final nsIDOMElement rootDiv = visualDocument.createElement(HTML.TAG_DIV);

        rootDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-list-decor"); //$NON-NLS-1$
        rootDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + this.listWidth);
        final nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);

        table.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-toolbar-decor"); //$NON-NLS-1$
        final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement buttonBorderDiv = visualDocument.createElement(HTML.TAG_DIV);

        buttonBorderDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-button-border"); //$NON-NLS-1$
        buttonBorderDiv.setAttribute(HTML.ATTR_STYLE, "float: left;"); //$NON-NLS-1$

        final nsIDOMElement fileuploadButtonDiv = visualDocument.createElement(HTML.TAG_DIV);
        fileuploadButtonDiv.setAttribute(HTML.ATTR_CLASS, styleClasses.getAddButtonClass());
        fileuploadButtonDiv.setAttribute(HTML.ATTR_STYLE, "position: relative;"); //$NON-NLS-1$

        final nsIDOMElement labelDiv = visualDocument.createElement(HTML.TAG_DIV);

        labelDiv.setAttribute(HTML.ATTR_CLASS, styleClasses.getAddButtonClassDiv2());
        labelDiv.appendChild(visualDocument.createTextNode(this.addControlLabel));
        fileuploadButtonDiv.appendChild(labelDiv);

  

        rootDiv.appendChild(table);
        rootDiv.appendChild(createPanelDiv(pageContext, source, visualDocument, styleClasses));
        table.appendChild(tr);
        tr.appendChild(td);
        td.appendChild(buttonBorderDiv);
        buttonBorderDiv.appendChild(fileuploadButtonDiv);
        td.appendChild(createControl(pageContext, sourceNode, visualDocument, styleClasses.getUploadButtonClass2(),
                uploadControlLabel, false, styleClasses));
        td.appendChild(createControl(pageContext, sourceNode, visualDocument, styleClasses.getClearAllButtonClass2(),
                clearAllControlLabel, true , styleClasses));
    

//        DOMTreeDumper dumper = new DOMTreeDumper();
//        dumper.dumpToStream(System.err, rootDiv);

        data = new VpeCreationData(rootDiv);
        return data;
    }

    /**
     * @param pageContext
     * @param sourceNode
     * @param visualDocument
     * @return
     */
    private nsIDOMNode createControl(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument,
    		String secondCssClass,String text,boolean isClearButton, StyleClasses styleClasses) {
        //<div class="rich-fileupload-button-border" style="float: left;">
        final nsIDOMElement firstDiv = visualDocument.createElement(HTML.TAG_DIV);
        
        firstDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-button-border"); //$NON-NLS-1$
        
        firstDiv.setAttribute(HTML.ATTR_STYLE,"float: "+(isClearButton ? "right;" : "left;")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        final nsIDOMElement secondDiv = visualDocument.createElement(HTML.TAG_DIV);
        
        if (isClearButton) {
            secondDiv.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-button rich-fileupload-font"); //$NON-NLS-1$
            final nsIDOMElement thirdDiv = visualDocument.createElement(HTML.TAG_DIV);
            final nsIDOMElement aElement = visualDocument.createElement(HTML.TAG_A);
            
            aElement.setAttribute(HTML.ATTR_CLASS, "rich-fileupload-button-selection"); //$NON-NLS-1$
            thirdDiv.setAttribute(HTML.ATTR_CLASS, styleClasses.getClearAllButtonClass2());
            firstDiv.appendChild(secondDiv);
            secondDiv.appendChild(aElement);
            aElement.appendChild(thirdDiv);
            thirdDiv.appendChild(visualDocument.createTextNode(text));
        }else{
            secondDiv.setAttribute(HTML.ATTR_CLASS, secondCssClass);

            final nsIDOMElement bDiv = visualDocument.createElement(HTML.TAG_B);

            bDiv.appendChild(visualDocument.createTextNode(text));

            firstDiv.appendChild(secondDiv);
            secondDiv.appendChild(bDiv);  
        }
        
   
        
        
            
        return firstDiv;
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
    private nsIDOMElement createPanelDiv(VpePageContext pageContext, Element source, nsIDOMDocument visualDocument, StyleClasses styleClasses) {
        final nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);

        div.setAttribute(HTML.ATTR_CLASS, styleClasses.getUploadListClass());
        div.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + "100%" + VpeStyleUtil.SEMICOLON_STRING //$NON-NLS-1$
                + VpeStyleUtil.PARAMETER_HEIGHT + VpeStyleUtil.COLON_STRING + this.listHeight);
        return div;
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
            listHeight = String.valueOf(ComponentUtil.parseWidthHeightValue(sourceElement.getAttribute("listHeight"))); //$NON-NLS-1$
        } catch (NumberFormatException e) {
            listHeight = DEFAULT_LIST_HEIGHT;
        }
        try {
            listWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(sourceElement.getAttribute("listWidth"))); //$NON-NLS-1$
        } catch (NumberFormatException e) {
            listWidth = DEFAULT_LIST_WIDTH;
        }

        addControlLabel = sourceElement.getAttribute("addControlLabel"); //$NON-NLS-1$
        if (addControlLabel == null) {
            addControlLabel = DEFAULT_CONTROL_LABEL_VALUE;
        }

        this.uploadControlLabel = ComponentUtil.getAttribute(sourceElement, "uploadControlLabel"); //$NON-NLS-1$
        
        if (ComponentUtil.isBlank(this.uploadControlLabel)) {
            this.uploadControlLabel = "Upload"; //$NON-NLS-1$
        }
        
        this.clearAllControlLabel = ComponentUtil.getAttribute(sourceElement, "clearAllControlLabel"); //$NON-NLS-1$
        
        if(ComponentUtil.isBlank(this.clearAllControlLabel)){
            this.clearAllControlLabel = "Clear All"; //$NON-NLS-1$
        }
   }
    
    class StyleClasses {

		private String addButtonClass = "rich-fileupload-button rich-fileupload-font"; //$NON-NLS-1$
    	private String uploadButtonClass = "rich-fileupload-button rich-fileupload-font"; //$NON-NLS-1$
    	private String addButtonClassDiv2 = " rich-fileupload-button-content rich-fileupload-font rich-fileupload-ico rich-fileupload-ico-add"; //$NON-NLS-1$
    	private String clearAllButtonClass2 = "rich-fileupload-button-content rich-fileupload-font rich-fileupload-ico rich-fileupload-ico-clear"; //$NON-NLS-1$
    	private String uploadButtonClass2 = "rich-fileupload-button-content rich-fileupload-font rich-fileupload-ico rich-fileupload-ico-start "; //$NON-NLS-1$
    	private String uploadListClass = "rich-fileupload-list-overflow"; //$NON-NLS-1$
    	
    	public StyleClasses(final Element sourceElement) {
    		final String addButtonClass = sourceElement.getAttribute("addButtonClass"); //$NON-NLS-1$

            if (ComponentUtil.isNotBlank(addButtonClass)) {
                this.addButtonClass += HTML.VALUE_CLASS_DELIMITER + addButtonClass;
                this.addButtonClassDiv2 += addButtonClassDiv2 + HTML.VALUE_CLASS_DELIMITER + addButtonClass;
            }

            final String uploadListClass = sourceElement.getAttribute("uploadListClass"); //$NON-NLS-1$

            if (ComponentUtil.isNotBlank(uploadListClass)) {
                this.uploadListClass += HTML.VALUE_CLASS_DELIMITER + uploadListClass;
            }
            
            final String uploadControlClass = ComponentUtil.getAttribute(sourceElement, "uploadControlClass"); //$NON-NLS-1$
            
            if(ComponentUtil.isNotBlank(uploadControlClass)){
                this.uploadButtonClass2+= HTML.VALUE_CLASS_DELIMITER + uploadControlClass;
            }
    	}
    	
    	public String getAddButtonClass() {
    		return addButtonClass;
    	}
    	
    	public String getUploadButtonClass() {
    		return uploadButtonClass;
    	}
    	
    	public String getAddButtonClassDiv2() {
    		return addButtonClassDiv2;
    	}
    	
    	public String getClearAllButtonClass2() {
    		return clearAllButtonClass2;
    	}
    	
    	public String getUploadButtonClass2() {
    		return uploadButtonClass2;
    	}
    	
    	public String getUploadListClass() {
    		return uploadListClass;
    	}
    }
}

