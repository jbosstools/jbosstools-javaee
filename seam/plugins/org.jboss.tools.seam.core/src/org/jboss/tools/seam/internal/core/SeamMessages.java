 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamMessages;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.event.Change;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamMessages extends SeamAnnotatedFactory implements ISeamMessages {
	SeamMessagesLoader messagesLoader = new SeamMessagesLoader(this, "org.jboss.seam.core.resourceLoader");
	
	public SeamMessages() {}
	
	public void revalidate() {
		messagesLoader.revalidate();
	}
	
	public Collection<ISeamProperty> getProperties() {
		return messagesLoader.getProperties();
	}

	public Collection<String> getPropertyNames() {
		return messagesLoader.getPropertyNames();
	}
	
	public Map<String, IResource> getResourcesMap() {
		return messagesLoader.getResources();
	}

	public SeamMessages clone() throws CloneNotSupportedException {
		SeamMessages c = (SeamMessages)super.clone();
		return c;
	}

	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);

	}

	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		messagesLoader.keys = null;
		return changes;
	}

	public String getXMLClass() {
		return SeamXMLConstants.CLS_MESSAGES;
	}

	public Element toXML(Element parent, Properties context) {
		Element e = super.toXML(parent, context);
		return e;
	}
	
}
