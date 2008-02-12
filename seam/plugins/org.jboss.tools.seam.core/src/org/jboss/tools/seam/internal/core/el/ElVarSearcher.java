 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class helps to find var/value attributes in DOM tree.   
 * @author Alexey Kazakov
 */
public class ElVarSearcher {

	private final static String VAR_ATTRIBUTE_NAME = "var";
	private final static String VALUE_ATTRIBUTE_NAME = "value";

	/**
	 * @param node
	 * @return All var/value that can be used in node and null if can't find anyone.
	 */
	public static List<Var> findAllVars(Node node) {
		ArrayList<Var> vars = null;
		Node parentNode = node.getParentNode();
		while(parentNode!=null) {
			Var var = findVar(parentNode);
			if(var!=null) {
				if(vars == null) {
					vars = new ArrayList<Var>();
				}
				vars.add(var);
			}
			parentNode = parentNode.getParentNode();
		}
		return vars;
	}

	/**
	 * Finds var/value attribute in node
	 * @param node
	 * @param vars
	 * @return found var/value or null
	 */
	public static Var findVar(Node node) {
		if(node!=null && Node.ELEMENT_NODE == node.getNodeType()) {
			Element element = (Element)node;
			String var = element.getAttribute(VAR_ATTRIBUTE_NAME);
			if(var!=null) {
				var = var.trim();
				if(!"".equals(var)) {
					String value = element.getAttribute(VALUE_ATTRIBUTE_NAME);
					if(value!=null) {
						value = value.trim();
						Var newVar = new Var(var, value);
						if(newVar.getElToken()!=null) {
							return newVar;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Represents "var"/"value" attributes.
	 * @author Alexey Kazakov
	 */
	public static class Var {
		private String name;
		private String value;
		private ELToken elToken;

		/**
		 * Constructor
		 * @param name - value of "var" attribute. 
		 * @param value - value of "value" attribute.
		 */
		public Var(String name, String value) {
			super();
			this.name = name;
			this.value = value;
			if(value.length()>3 && value.startsWith("#{") && value.endsWith("}")) {
				String elBody = value.substring(0, value.length()-1).substring(2);
				SeamELTokenizer elTokenizer = new SeamELTokenizer(elBody);
				List<ELToken> tokens = elTokenizer.getTokens();
				for (ELToken token : tokens) {
					if(token.getType()==ELToken.EL_VARIABLE_TOKEN) {
						elToken = token;
						break;
					}
				}
			}
		}

		/**
		 * @return parsed EL from "value" attribute. Returns null if EL is not valid.
		 */
		public ELToken getElToken() {
			return elToken;
		}

		/**
		 * @return name of variable. 
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return value of variable. It's EL.
		 */
		public String getValue() {
			return value;
		}
	}
}