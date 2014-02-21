package org.jboss.tools.jsf.jsf2.model;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.NamespaceMapping;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FileCompositeComponentLoader extends SimpleWebFileLoader {

	public FileCompositeComponentLoader() {}

	protected XModelObjectLoaderUtil createUtil() {
		return new FileCompositeComponentUtil();
	}

    protected boolean isCheckingDTD() {
    	return false;
    }
    
    protected boolean isCheckingSchema() {
    	return false;
    }

    protected String loadNamespace(Element element, XModelObject object) {
    	String version = getMappingVersion(object);
    	NamespaceMapping namespaceMapping = CompositeComponentNamespaces.getInstance(object.getModel().getMetaData(), version).getNamespaceMapping(element);
    	object.set(NamespaceMapping.ATTR_NAMESPACE_MAPPING, namespaceMapping.toString()); //$NON-NLS-1$
    	util.setNamespaceMapping(namespaceMapping);
    	
    	return super.loadNamespace(element, object);
    }

    public Element createRootElement(XModelObject object) {
        String systemId = object.getAttributeValue("systemId"); //$NON-NLS-1$
        String publicId = object.getAttributeValue("publicId"); //$NON-NLS-1$
    	String rootName = getRootName(object);
        Element element = createRootElement(rootName, publicId, systemId);
        String version = getMappingVersion(object);
        CompositeComponentNamespaces.getInstance(object.getModel().getMetaData(), version).validateNamespaces(object, element);
		NamespaceMapping namespaceMapping = NamespaceMapping.load(object);
    	util.setNamespaceMapping(namespaceMapping);
    	return element;
    }

    private String getMappingVersion(XModelObject object) {
    	String entity = object.getModelEntity().getName();
    	return (CompositeComponentConstants.ENT_FILE_COMPONENT_22.equals(entity))
        		? "22" : "20";
    }

}

class FileCompositeComponentUtil extends XModelObjectLoaderUtil {

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}

    public void load(Element element, XModelObject o) {
    	super.load(element, o);
    	if(o.getModelEntity().getName().startsWith(CompositeComponentConstants.ENT_FILE_COMPONENT)) {
    		o.setAttributeValue("tag", element.getNodeName());
    		Element c = XMLUtilities.getUniqueChild(element, "composite:interface");
    		if(c == null) {
    			NodeList l = element.getElementsByTagName("composite:interface");
    			if(l != null && l.getLength() > 0) {
    				Element is = (Element)l.item(0);
    				XModelObject io = o.getChildByPath("Interface");
    				if(io != null) load(is, io);
    			}
    		}
    	}
    }

    public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	return super.save(parent, o);
    }

    protected boolean needToSave(XModelObject o) {
    	String s = o.getModelEntity().getProperty("saveDefault"); //$NON-NLS-1$
    	if(!"false".equals(s)) return true; //$NON-NLS-1$
//    	if(hasSetAttributes(o)) return true;
    	if(o.getChildren().length > 0) return true;
    	return false;
    }

}