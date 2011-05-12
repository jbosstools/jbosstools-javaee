package org.jboss.tools.cdi.seam.config.core.definition;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.cdi.seam.config.core.xml.SAXNode;
import org.jboss.tools.common.text.ITextSourceReference;

public class TextSourceReference implements ITextSourceReference {
	IResource resource;
	SAXNode node;

	public TextSourceReference(IResource resource, SAXNode node) {
		this.resource = resource;
		this.node = node;
	}

	public int getStartPosition() {
		return node.getLocation().getStartPosition();
	}

	public int getLength() {
		return node.getLocation().getLength();
	}

	public IResource getResource() {
		return resource;
	}

}
