package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.text.INodeReference;

public class BeansXMLDefinition {
	static String NODE_INTERCEPTORS = "Interceptors";
	static String NODE_DECORATORS = "Decorators";
	static String NODE_ALTERNATIVES = "Alternatives";
	static String ATTR_CLASS = "class";
	static String ATTR_STEREOTYPE = "stereotype";

	private IPath path;
	private Set<INodeReference> typeAlternatives = new HashSet<INodeReference>();
	private Set<INodeReference> stereotypeAlternatives = new HashSet<INodeReference>();
	private Set<INodeReference> decorators = new HashSet<INodeReference>();
	private Set<INodeReference> interceptors = new HashSet<INodeReference>();

	public BeansXMLDefinition() {}

	public void setBeansXML(XModelObject beansXML) {
		if(beansXML == null || !beansXML.getModelEntity().getName().startsWith("FileCDIBeans")) return;
		XModelObject interceptorsObject = beansXML.getChildByPath(NODE_INTERCEPTORS);
		if(interceptorsObject != null) {
			XModelObject[] cs = interceptorsObject.getChildren();
			for (XModelObject o: cs) {
				interceptors.add(new XMLNodeReference(o, ATTR_CLASS));
			}
		}
		XModelObject decoratorsObject = beansXML.getChildByPath(NODE_DECORATORS);
		if(decoratorsObject != null) {
			XModelObject[] cs = decoratorsObject.getChildren();
			for (XModelObject o: cs) {
				decorators.add(new XMLNodeReference(o, ATTR_CLASS));
			}
		}
		XModelObject alternativesObject = beansXML.getChildByPath(NODE_ALTERNATIVES);
		if(alternativesObject != null) {
			XModelObject[] cs = alternativesObject.getChildren("CDIClass");
			for (XModelObject o: cs) {
				typeAlternatives.add(new XMLNodeReference(o, ATTR_CLASS));
			}
			cs = alternativesObject.getChildren("CDIStereotype");
			for (XModelObject o: cs) {
				stereotypeAlternatives.add(new XMLNodeReference(o, ATTR_STEREOTYPE));
			}
		}
		
	}

	public void setPath(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	public Set<INodeReference> getTypeAlternatives() {
		return typeAlternatives;
	}

	public Set<INodeReference> getStereotypeAlternatives() {
		return stereotypeAlternatives;
	}

	public Set<INodeReference> getDecorators() {
		return decorators;
	}

	public Set<INodeReference> getInterceptors() {
		return interceptors;
	}


}
