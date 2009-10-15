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
package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.List;

import org.eclipse.core.resources.IStorage;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCustomTemplate;
import org.jboss.tools.vpe.editor.template.custom.CustomTLDReference;
import org.jboss.tools.vpe.editor.util.XmlUtil;
import org.w3c.dom.Node;

/**
 * Class implements support for custom jsf 2.0 components
 * 
 * @author mareshkau
 *
 */
public class Jsf2CustomComponentTemplate extends VpeCustomTemplate{
	
	@Override
	protected IStorage getCustomTemplateStorage(VpePageContext pageContext, Node sourceNode){
		String sourcePrefix = sourceNode.getPrefix();
		
		List<TaglibData> taglibs = XmlUtil.getTaglibsForNode(sourceNode,pageContext);
		
		TaglibData sourceNodeTaglib = XmlUtil.getTaglibForPrefix(sourcePrefix, taglibs);		

		if(sourceNodeTaglib == null) {
			return null;
		}
		
		String sourceNodeUri = sourceNodeTaglib.getUri();
		
		return CustomTLDReference.getJsf2CustomComponentStorage(pageContext, sourceNodeUri, sourceNode.getLocalName());
	}
}
