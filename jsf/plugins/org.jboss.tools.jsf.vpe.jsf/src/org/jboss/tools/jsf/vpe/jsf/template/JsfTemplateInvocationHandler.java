/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.jsf.template;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class JsfTemplateInvocationHandler implements InvocationHandler {

	private static String KEY_WORD = "Offset"; //$NON-NLS-1$

	private int offset;

	private Object nodeItem;

	public static NodeList createNodeListProxy(Object nodeList, int offset) {

		Class<?>[] classes = { NodeList.class };
		return (NodeList) Proxy.newProxyInstance(nodeList.getClass()
				.getClassLoader(), classes, new JsfTemplateInvocationHandler(
				nodeList, offset));
	}

	public static Object createNodeItemProxy(Object nodeItem, int offset) {

		Class<?>[] classes = new Class<?>[1];

		if (nodeItem instanceof IDOMElement)
			classes[0] = IDOMElement.class;
		else if (nodeItem instanceof IDOMAttr)
			classes[0] = IDOMAttr.class;
		else if (nodeItem instanceof IDOMNode)
			classes[0] = IDOMNode.class;
		else if (nodeItem instanceof NamedNodeMap)
			classes[0] = NamedNodeMap.class;
		else
			return null;

		return Proxy.newProxyInstance(nodeItem.getClass().getClassLoader(),
				classes, new JsfTemplateInvocationHandler(nodeItem, offset));

	}

	private JsfTemplateInvocationHandler(Object nodeItem, int offset) {
		this.offset = offset;
		this.nodeItem = nodeItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		Object result = method.invoke(nodeItem, args);

		if (method.getReturnType() == NodeList.class)
			return JsfTemplateInvocationHandler.createNodeListProxy(result,
					offset);
		if ((result instanceof IDOMNode) || (result instanceof NamedNodeMap))
			return JsfTemplateInvocationHandler.createNodeItemProxy(result,
					offset);
		if ((result instanceof Integer)
				&& (method.getName().contains(KEY_WORD))) {
			return Integer.valueOf(((Integer) result).intValue() + offset);
		}

		return result;
	}
}
